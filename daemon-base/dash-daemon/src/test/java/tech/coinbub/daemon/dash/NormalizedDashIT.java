package tech.coinbub.daemon.dash;

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
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.suites.StandardNormalizedIT;

@ExtendWith(Dockerized.class)
@DockerizedDash
public class NormalizedDashIT implements StandardNormalizedIT {
    public static final String BEST_BLOCK_HASH = "03f452916f5fb7f261700da37da8f2568c30a6aa6f73b3954865474fc7c18a60";
    public static final long ARBITRARY_BLOCK_HEIGHT = 22L;
    public static final String ARBITRARY_BLOCK_HASH = "20a3f5d2b3f1b61845c8e96124192a7d6ec072ac3b8913278d8f5c6dfde5b30b";
    public static final int ADDRESS_LENGTH = 34;
    public static final String VALID_ADDRESS = "yMQcwprKzfs4XYVouPoeYQyvHWZFzdfJR5";
    public static final String TRANSACTION_ID = "8b2e01ff7c33b7a7d68fc7ebb196bb525ca103df5acb60e4a6b90f1d0cd5f416";
    public static final BigDecimal FEE = new BigDecimal("-0.00000226");
    public static final BigDecimal FEE_DEVIATION = new BigDecimal("0.000002");
    public static final int LISTENER_PORT = 20010;

    private Object result = null;

    @Test
    @Override
    public void testGetinfo(final Normalized normalized) {
        final tech.coinbub.daemon.normalized.model.Info info = normalized.getinfo();
        assertThat(info, hasAllIgnoringNull(
            property("version", is(equalTo("120203"))),
            property("protocolversion", is(equalTo(70208L))),
            property("walletversion", is(equalTo(61000L))),
            property("balance", is(closeTo(new BigDecimal("1748.89404289"), new BigDecimal("2")))),
            property("newmint", is(equalTo(new BigDecimal("500.00000881")))),
            property("blocks", is(equalTo(111L))),
            property("timeoffset", is(equalTo(0L))),
            property("moneysupply", is(equalTo(new BigDecimal("55500.0")))),
            property("connections", is(equalTo(0L))),
            property("proxy", is(equalTo(null))),
            property("ip", is(equalTo(null))),
            property("difficulty", is(instanceOf(tech.coinbub.daemon.normalized.model.Info.Difficulty.class))),
            property("testnet", is(equalTo(null))),
            property("keypoololdest", is(equalTo(1538167749L))),
            property("keypoolsize", is(equalTo(999L))),
            property("paytxfee", is(equalTo(new BigDecimal("0.0")))),
            property("warnings", is(equalTo(""))),
            property("errors", is(equalTo(null)))
        ));

        final tech.coinbub.daemon.normalized.model.Info.Difficulty difficulty = info.difficulty;
        assertThat(difficulty, hasAllIgnoringNull(
                property("proofOfWork", is(equalTo(new BigDecimal("4.656542373906925E-10")))),
                property("proofOfStake", is(equalTo(null)))
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
                property("previousblockhash", is(equalTo("3ac03d02172ce1449fb8244c848117ac5a6516ae15518f5722ac612726461685"))),
                property("nextblockhash", is(equalTo("4b6d0b2dff1c58587c33175a360873ac229fc80c2a1d9d1b40f8a6a4f8932cb3"))),
                property("confirmations", is(equalTo(90L))),
                property("size", is(equalTo(180L))),
                property("height", is(equalTo(22L))),
                property("time", is(equalTo(1538167886L))),
                property("tx", hasSize(1))
        ));

        assertThat(actual.tx.get(0), is(equalTo("ffb2bc52163ab14c3dbf75b02cf1f8a446cea8288cca05902b7cb796bb386600")));
    }

    @Test
    @Override
    public void testGettransaction(final Normalized normalized) {
        final tech.coinbub.daemon.normalized.model.Transaction actual = normalized.gettransaction(TRANSACTION_ID);
        assertThat(actual, hasAllIgnoringNull(
                property("id", is(equalTo(TRANSACTION_ID))),
                property("amount", is(equalTo(new BigDecimal("-750.0")))),
                property("fee", is(equalTo(new BigDecimal("-0.00000306")))),
                property("confirmations", is(equalTo(1L))),
                property("blockhash", is(equalTo("03f452916f5fb7f261700da37da8f2568c30a6aa6f73b3954865474fc7c18a60"))),
                property("time", is(equalTo(1538168047L))),
                property("details", hasSize(1))
        ));

        final tech.coinbub.daemon.normalized.model.TransactionDetail detail = actual.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo("yWi2JBsWQzDbqUC7MRJeeA8Q6y4JPQih9A"))),
                property("amount", is(equalTo(new BigDecimal("-750.0")))),
                property("fee", is(equalTo(new BigDecimal("-0.00000306"))))
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
        // Setup
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
