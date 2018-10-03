package tech.coinbub.daemon.poli;

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
@DockerizedPoli
public class NormalizedPoliIT implements StandardNormalizedIT {
    public static final String BEST_BLOCK_HASH = "e1a3773cfef6012f2498bbfd56256ea497a8d3202736ae31e1f6a837ccd4d848";
    public static final long ARBITRARY_BLOCK_HEIGHT = 22L;
    public static final String ARBITRARY_BLOCK_HASH = "10e8298ecc7a4668273cf69aa2f66324b100cacfd0916cf60b8e46df14d1b9f4";
    public static final int ADDRESS_LENGTH = 34;
    public static final String VALID_ADDRESS = "mk9GeqVDJd4ZNf7U77bmi2bendv3hQxbxD";
    public static final String TRANSACTION_ID = "371fa5e751bb27c9e908c30ddff3b98b96651c8b278c71a4da93151cbc563ca5";
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
            property("balance", is(equalTo(new BigDecimal("9999999979.9998")))),
            property("newmint", is(equalTo(new BigDecimal("0.00020")))),
            property("blocks", is(equalTo(33L))),
            property("timeoffset", is(equalTo(0L))),
            property("moneysupply", is(equalTo(new BigDecimal("1.0E+10")))),
            property("connections", is(equalTo(0L))),
            property("proxy", is(equalTo(""))),
            property("ip", is(equalTo("0.0.0.0"))),
            property("difficulty", is(instanceOf(Info.Difficulty.class))),
            property("testnet", is(equalTo(false))),
            property("keypoololdest", is(equalTo(1526327205L))),
            property("keypoolsize", is(equalTo(101L))),
            property("paytxfee", is(equalTo(new BigDecimal("0.00010")))),
            property("errors", is(equalTo("")))
        ));

        final Info.Difficulty difficulty = info.difficulty;
        assertThat(difficulty, hasAllIgnoringNull(
                property("proofOfWork", is(equalTo(new BigDecimal("0.00003936")))),
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
        final Block block = normalized.getblock("da381cce924f5865a8cd21d59c972376b8920262a19b436f8c1113b8f9416d9a");
        assertThat(block, hasAllIgnoringNull(
                property("hash", is(equalTo("da381cce924f5865a8cd21d59c972376b8920262a19b436f8c1113b8f9416d9a"))),
                property("confirmations", is(equalTo(3L))),
                property("size", is(equalTo(175L))),
                property("height", is(equalTo(31L))),
                property("time", is(equalTo(1526327417L))),
                property("previousblockhash", is(equalTo("b210ba1f4c84192690868e48130b41c06c800a38650cb9592fb71f878d5625ef"))),
                property("nextblockhash", is(equalTo("df3c2ec2337250daa07bbc8db81d32eeaa7fe0ebd0b1bfbe04c57f6065e04d2c"))),
                property("tx", hasSize(1))
        ));

        assertThat(block.tx.get(0), is(equalTo("371fa5e751bb27c9e908c30ddff3b98b96651c8b278c71a4da93151cbc563ca5")));
    }

    @Test
    @Override
    public void testGettransaction(final Normalized normalized) {
        final Transaction tx = normalized.gettransaction(TRANSACTION_ID);
        assertThat(tx, hasAllIgnoringNull(
                property("id", is(equalTo(TRANSACTION_ID))),
                property("time", is(equalTo(1526327417L))),
                property("amount", is(equalTo(new BigDecimal("0.0")))),
                property("confirmations", is(equalTo(3L))),
                property("blockhash", is(equalTo("da381cce924f5865a8cd21d59c972376b8920262a19b436f8c1113b8f9416d9a"))),
                property("details", hasSize(1))
        ));
        
        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo("mom7nFUjn8Xnn2RGLQpwsQys76AxrwrZgN"))),
                property("amount", is(equalTo(new BigDecimal("0.0"))))
        ));
    }

    @Test
    @Override
    public void testGetnewaddress(final Normalized normalized) {
        assertThat(normalized.getnewaddress().length(), is(equalTo(ADDRESS_LENGTH)));
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
