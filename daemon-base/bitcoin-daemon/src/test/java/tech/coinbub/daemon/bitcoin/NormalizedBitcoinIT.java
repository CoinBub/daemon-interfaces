package tech.coinbub.daemon.bitcoin;

import java.io.IOException;
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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.NotificationListener;
import tech.coinbub.daemon.normalized.Normalized;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardNormalizedIT;
import static tech.coinbub.daemon.bitcoin.Constants.*;
import tech.coinbub.daemon.normalized.model.Info;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;

@ExtendWith(Dockerized.class)
@DockerizedBitcoin
public class NormalizedBitcoinIT implements StandardNormalizedIT {
    private Object result = null;

    @Test
    @Override
    public void testGetinfo(final Normalized normalized) {
        final Info info = normalized.getinfo();
        assertThat(info, hasAllIgnoringNull(
            property("version", is(equalTo("170000"))),
            property("protocolversion", is(equalTo(70015L))),
            property("walletversion", is(equalTo(159900L))),
            property("balance", is(equalTo(new BigDecimal("100.0")))),
            property("newmint", is(equalTo(new BigDecimal("5000.0")))),
            property("blocks", is(equalTo(102L))),
            property("timeoffset", is(equalTo(0L))),
            property("moneysupply", is(equalTo(new BigDecimal("5100.0")))),
            property("connections", is(equalTo(0L))),
            property("proxy", is(nullValue())),
            property("ip", is(nullValue())),
            property("difficulty", is(instanceOf(Info.Difficulty.class))),
            property("testnet", is(nullValue())),
            property("keypoololdest", is(equalTo(1525205529L))),
            property("keypoolsize", is(equalTo(1000L))),
            property("paytxfee", is(equalTo(new BigDecimal("0.0")))),
            property("warnings", is(equalTo(""))),
            property("errors", is(nullValue()))
        ));

        final Info.Difficulty difficulty = info.difficulty;
        assertThat(difficulty, hasAllIgnoringNull(
                property("proofOfWork", is(equalTo(new BigDecimal("4.656542373906925E-10")))),
                property("proofOfStake", is(nullValue()))
        ));
    }

    @Test
    @Override
    public void testGetbestblockhash(final Normalized normalized) {
        final String best = normalized.getbestblockhash();
        assertThat(best, is(equalTo(BEST_BLOCK_HASH)));
    }

    @Test
    @Override
    public void testGetblockhash(final Normalized normalized) {
        assertThat(normalized.getblockhash(ARBITRARY_BLOCK_HEIGHT), is(equalTo(ARBITRARY_BLOCK_HASH)));
    }

    @Test
    @Override
    public void testGetblock(final Normalized normalized) {
        final tech.coinbub.daemon.normalized.model.Block actual = normalized.getblock(ARBITRARY_BLOCK_HASH);
        assertThat(actual, hasAllIgnoringNull(
                property("hash", is(equalTo(ARBITRARY_BLOCK_HASH))),
                property("previousblockhash", is(equalTo("7998f420835165e22541331b3658c2891561f12a09d283ce3dcf63d670fe6ba1"))),
                property("confirmations", is(equalTo(81L))),
                property("size", is(equalTo(253L))),
                property("height", is(equalTo(22L))),
                property("time", is(equalTo(1525217563L))),
                property("tx", hasSize(1))
        ));
        
        assertThat(actual.tx.get(0), is(equalTo("2314c70246e358557ef7a395e7d0e19d784fa64155e24d6507d4c40d7bced1b8")));
    }

    @Test
    @Override
    public void testGettransaction(final Normalized normalized) {
        final tech.coinbub.daemon.normalized.model.Transaction actual = normalized.gettransaction(TRANSACTION_ID);
        assertThat(actual, hasAllIgnoringNull(
                property("id", is(equalTo(TRANSACTION_ID))),
                property("amount", is(equalTo(new BigDecimal("0.0")))),
                property("confirmations", is(equalTo(1L))),
                property("blockhash", is(equalTo("3cf12c2be37b180d6c955647cc8b468f814f1c5941dda2283bd9d903882fb6aa"))),
                property("time", is(equalTo(1525217560L))),
                property("details", hasSize(1))
        ));

        final tech.coinbub.daemon.normalized.model.TransactionDetail detail = actual.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo("mwh7N9LvZEGsHVWsyfkSxrPepMMSFs4N24"))),
                property("amount", is(equalTo(new BigDecimal("50.0"))))
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
        final tech.coinbub.daemon.normalized.model.Transaction tx = normalized.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("id", is(not(nullValue()))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION))),
                property("time", is(not(nullValue()))),
                property("confirmations", is(equalTo(0L))),
                property("details", hasSize(1))
        ));

        
        final tech.coinbub.daemon.normalized.model.TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo(VALID_ADDRESS))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION)))
        ));
    }

    @Test
    @Override
    public void testSendToAddressSourceComment(final Normalized normalized) {
        final String txid = normalized.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!");
        final tech.coinbub.daemon.normalized.model.Transaction tx = normalized.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("id", is(not(nullValue()))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION))),
                property("time", is(not(nullValue()))),
                property("confirmations", is(equalTo(0L))),
                property("details", hasSize(1)),
                property("comment_from", is(equalTo("test transaction!")))
        ));
        
        final tech.coinbub.daemon.normalized.model.TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo(VALID_ADDRESS))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION)))
        ));
    }

    @Test
    @Override
    public void testSendToAddressDestComment(final Normalized normalized) {
        final String txid = normalized.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!", "receiving test!");
        final tech.coinbub.daemon.normalized.model.Transaction tx = normalized.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("id", is(not(nullValue()))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION))),
                property("time", is(not(nullValue()))),
                property("confirmations", is(equalTo(0L))),
                property("details", hasSize(1)),
                property("comment_from", is(equalTo("test transaction!"))),
                property("comment_to", is(equalTo("receiving test!")))
        ));
        
        final tech.coinbub.daemon.normalized.model.TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo(VALID_ADDRESS))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION)))
        ));
    }

    @Test
    @Override
    public void daemonNotifiesListenerOnSend(final Normalized normalized) throws IOException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        NotificationListener listener = new NotificationListener(LISTENER_PORT);
        listener.setTransformer(new NotificationListener.TransactionTransformer(normalized));
        listener.addObserver((Observable o, Object o1) -> {
            result = o1;
            latch.countDown();
        });
        listener.start();
        listener.await();
        
        try {
            final String txid = normalized.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!", "receiving test!");
            final tech.coinbub.daemon.normalized.model.Transaction tx = normalized.gettransaction(txid);
            assertThat(tx, hasAllIgnoringNull(
                    property("id", is(equalTo(txid))),
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
