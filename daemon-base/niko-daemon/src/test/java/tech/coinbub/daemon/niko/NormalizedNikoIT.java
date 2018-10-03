package tech.coinbub.daemon.niko;

import java.math.BigDecimal;
import java.util.Observable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.NotificationListener;
import tech.coinbub.daemon.normalized.Normalized;
import tech.coinbub.daemon.normalized.model.Block;
import tech.coinbub.daemon.normalized.model.Info;
import tech.coinbub.daemon.normalized.model.Transaction;
import tech.coinbub.daemon.normalized.model.TransactionDetail;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.suites.StandardNormalizedIT;

@ExtendWith(Dockerized.class)
@DockerizedNiko
public class NormalizedNikoIT implements StandardNormalizedIT {
    public static final String BEST_BLOCK_HASH = "37762e5ce945ff724d96623cc94a5983f1ba9322224a5ac68d3d8e899615373d";
    public static final long ARBITRARY_BLOCK_HEIGHT = 22L;
    public static final String ARBITRARY_BLOCK_HASH = "06e21472e1832d4c9b926b96e5e038e8932f502e3fb0b98b6359ebe8812c0449";
    public static final int ADDRESS_LENGTH = 35;
    public static final String VALID_ADDRESS = "mhhhbW3S4XqYiJSzW2kx6krxYSm2JhGRqd";
    public static final String TRANSACTION_ID = "2553da6a3f4130e040c2dddfd9d14b1e394309c393d7f6a7cee8c5005fbb3f87";
    public static final BigDecimal FEE = new BigDecimal("-0.0001");
    public static final BigDecimal FEE_DEVIATION = new BigDecimal("0.0001");
    public static final int LISTENER_PORT = 20010;

    private Object result = null;

    @Test
    @Override
    public void testGetinfo(final Normalized normalized) {
        final Info info = normalized.getinfo();
        assertThat(info, hasAllIgnoringNull(
            property("version", is(equalTo("v1.2.0.0-g32a928e"))),
            property("protocolversion", is(equalTo(60016L))),
            property("walletversion", is(equalTo(60000L))),
            property("balance", is(equalTo(new BigDecimal("499993876.97534794")))),
            property("newmint", is(equalTo(new BigDecimal("0.0")))),
            property("blocks", is(equalTo(119L))),
            property("timeoffset", is(equalTo(0L))),
            property("moneysupply", is(equalTo(new BigDecimal("1.0E+9")))),
            property("connections", is(equalTo(0L))),
            property("proxy", is(equalTo(""))),
            property("ip", is(equalTo("0.0.0.0"))),
            property("difficulty", is(instanceOf(Info.Difficulty.class))),
            property("testnet", is(equalTo(false))),
            property("keypoololdest", is(equalTo(1524083903L))),
            property("keypoolsize", is(equalTo(101L))),
            property("paytxfee", is(equalTo(new BigDecimal("0.00010")))),
            property("errors", is(equalTo("")))
        ));

        final Info.Difficulty difficulty = info.difficulty;
        assertThat(difficulty, hasAllIgnoringNull(
                property("proofOfWork", is(equalTo(new BigDecimal("0.00019956")))),
                property("proofOfStake", is(equalTo(new BigDecimal("0.0"))))
        ));
    }

    @Test
    @Override
    public void testGetbestblockhash(final Normalized normalized) throws Exception {
        assertThat(normalized.getbestblockhash(), is(equalTo(BEST_BLOCK_HASH)));
    }

    @Test
    @Override
    public void testGetblockhash(final Normalized normalized) {
        assertThat(normalized.getblockhash(ARBITRARY_BLOCK_HEIGHT), is(equalTo(ARBITRARY_BLOCK_HASH)));
    }

    @Test
    @Override
    public void testGetblock(final Normalized normalized) {
        final Block block = normalized.getblock("37762e5ce945ff724d96623cc94a5983f1ba9322224a5ac68d3d8e899615373d");
        assertThat(block, hasAllIgnoringNull(
                property("hash", is(equalTo("37762e5ce945ff724d96623cc94a5983f1ba9322224a5ac68d3d8e899615373d"))),
                property("confirmations", is(equalTo(1L))),
                property("size", is(equalTo(175L))),
                property("height", is(equalTo(119L))),
                property("time", is(equalTo(1524090557L))),
                property("previousblockhash", is(equalTo("6f7ac94cd55f03e9121dc682e8075822c4bb03c0323301706f98e7af8d7dcd7d"))),
                property("tx", hasSize(1))
        ));

        assertThat(block.tx.get(0), is(equalTo("b237ae95d8cf3c0af24948248fbafe447512826f85cf9c3288f9c83634b7fa9a")));
    }

    @Test
    @Override
    public void testGettransaction(final Normalized normalized) {
        final Transaction tx = normalized.gettransaction("b237ae95d8cf3c0af24948248fbafe447512826f85cf9c3288f9c83634b7fa9a");
        assertThat(tx, hasAllIgnoringNull(
                property("id", is(equalTo("b237ae95d8cf3c0af24948248fbafe447512826f85cf9c3288f9c83634b7fa9a"))),
                property("time", is(equalTo(1524090557L))),
                property("amount", is(equalTo(new BigDecimal("0.0")))),
                property("confirmations", is(equalTo(1L))),
                property("blockhash", is(equalTo("37762e5ce945ff724d96623cc94a5983f1ba9322224a5ac68d3d8e899615373d"))),
                property("details", hasSize(1))
        ));

        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo("mtNiPdupyDA7LfG9Mbwx3qXweoiZKSsTtY"))),
                property("amount", is(equalTo(new BigDecimal("0.0"))))
        ));
    }

    @Test
    @Override
    public void testGetnewaddress(final Normalized normalized) {
        assertThat(normalized.getnewaddress().length(), is(equalTo(34)));
    }

    @Test
    @Override
    public void testSendToAddressNoComments(final Normalized normalized) {
        final String txid = normalized.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE);
        final Transaction tx = normalized.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("id", not(isEmptyString())),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION))),
                property("confirmations", is(equalTo(0L))),
                property("time", is(not(nullValue()))),
                property("details", hasSize(1))
        ));

        assertThat(tx.details.get(0), hasAllIgnoringNull(
                property("address", is(equalTo(VALID_ADDRESS))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION)))
        ));
    }

    @Test
    @Override
    public void testSendToAddressSourceComment(final Normalized normalized) {
        final String txid = normalized.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!");
        final Transaction tx = normalized.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("id", not(isEmptyString())),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION))),
                property("confirmations", is(equalTo(0L))),
                property("time", is(not(nullValue()))),
                property("comment_from", is(equalTo("test transaction!"))),
                property("details", hasSize(1))
        ));

        assertThat(tx.details.get(0), hasAllIgnoringNull(
                property("address", is(equalTo(VALID_ADDRESS))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION)))
        ));
    }

    @Test
    @Override
    public void testSendToAddressDestComment(final Normalized normalized) {
        final String txid = normalized.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!", "receiving test!");
        final Transaction tx = normalized.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("id", not(isEmptyString())),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION))),
                property("time", is(not(nullValue()))),
                property("confirmations", is(equalTo(0L))),
                property("details", hasSize(1)),
                property("comment_from", is(equalTo("test transaction!"))),
                property("comment_to", is(equalTo("receiving test!")))
        ));
    
        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo(VALID_ADDRESS))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION)))
        ));
    }

    @Test
    @Override
    public void daemonNotifiesListenerOnSend(final Normalized normalized) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        NotificationListener listener = new NotificationListener(LISTENER_PORT);
        listener.setTransformer(new NotificationListener.TransactionTransformer(normalized));
        listener.addObserver((Observable o, Object o1) -> {
            result = o1;
            latch.countDown();
        });
        listener.start();
        listener.await(500);
        
        try {
            final String txid = normalized.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!", "receiving test!");
            final tech.coinbub.daemon.normalized.model.Transaction tx = normalized.gettransaction(txid);
            assertThat(tx, hasAllIgnoringNull(
                    property("id", is(not(nullValue()))),
                    property("amount", is(equalTo(new BigDecimal("-1.0")))),
                    property("fee", is(closeTo(FEE, FEE_DEVIATION))),
                    property("time", is(not(nullValue()))),
                    property("confirmations", is(equalTo(0L))),
                    property("comment_from", is(equalTo("test transaction!"))),
                    property("comment_to", is(equalTo("receiving test!"))),
                    property("details", hasSize(1))
            ));

            final tech.coinbub.daemon.normalized.model.TransactionDetail detail = tx.details.get(0);
            assertThat(detail, hasAllIgnoringNull(
                    property("address", is(equalTo(VALID_ADDRESS))),
                    property("amount", is(equalTo(new BigDecimal("-1.0")))),
                    property("fee", is(closeTo(FEE, FEE_DEVIATION)))
            ));

            latch.await(500, TimeUnit.MILLISECONDS);
            assertThat(result, is(not(nullValue())));
            assertThat(((tech.coinbub.daemon.normalized.model.Transaction) result).id.length(), is(equalTo(64)));
        } finally {
            listener.stop();
        }
    }

}
