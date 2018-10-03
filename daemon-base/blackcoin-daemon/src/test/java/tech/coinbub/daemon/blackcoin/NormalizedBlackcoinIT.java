package tech.coinbub.daemon.blackcoin;

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
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.NotificationListener;
import tech.coinbub.daemon.normalized.Normalized;
import tech.coinbub.daemon.normalized.model.Block;
import tech.coinbub.daemon.normalized.model.Transaction;
import tech.coinbub.daemon.normalized.model.TransactionDetail;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.suites.StandardNormalizedIT;

@ExtendWith(Dockerized.class)
@DockerizedBlackcoin
public class NormalizedBlackcoinIT implements StandardNormalizedIT {
    public static final String BEST_BLOCK_HASH = "8d5598d979159a4c75b2828b63b4e6dbb2e7d87ccf405922d86ab38f05871b32";
    public static final long ARBITRARY_BLOCK_HEIGHT = 16L;
    public static final String ARBITRARY_BLOCK_HASH = "2389a84b83b2ace81b39342cfc2ad72807828c0b247a56053b3ca1569cdb8f68";
    public static final int ADDRESS_LENGTH = 34;
    public static final String VALID_ADDRESS = "mk9GeqVDJd4ZNf7U77bmi2bendv3hQxbxD";
    public static final String TRANSACTION_ID = "e3348178922d0e9f27422bd2a62bb22be97f20b90bc0121d833adcc5d07997a0";
    public static final BigDecimal FEE = new BigDecimal("-0.0001");
    public static final BigDecimal FEE_DEVIATION = new BigDecimal("0.0001");
    public static final int LISTENER_PORT = 20010;

    private Object result = null;

    @Test
    @Override
    public void testGetinfo(final Normalized normalized) {
        final tech.coinbub.daemon.normalized.model.Info info = normalized.getinfo();
        assertThat(info, hasAllIgnoringNull(
            property("version", is(equalTo("v1.2.5.2"))),
            property("protocolversion", is(equalTo(60019L))),
            property("walletversion", is(equalTo(60000L))),
            property("balance", is(equalTo(new BigDecimal("34769.68081687")))),
            property("newmint", is(equalTo(new BigDecimal("0.0")))),
            property("blocks", is(equalTo(21L))),
            property("timeoffset", is(equalTo(0L))),
            property("moneysupply", is(equalTo(new BigDecimal("210000.0")))),
            property("connections", is(equalTo(0L))),
            property("proxy", is(equalTo(""))),
            property("ip", is(equalTo("0.0.0.0"))),
            property("difficulty", is(instanceOf(tech.coinbub.daemon.normalized.model.Info.Difficulty.class))),
            property("testnet", is(equalTo(true))),
            property("keypoololdest", is(equalTo(1537820648L))),
            property("keypoolsize", is(equalTo(101L))),
            property("paytxfee", is(equalTo(new BigDecimal("0.00010")))),
            property("errors", is(equalTo("")))
        ));

        final tech.coinbub.daemon.normalized.model.Info.Difficulty difficulty = info.difficulty;
        assertThat(difficulty, hasAllIgnoringNull(
                property("proofOfWork", is(equalTo(new BigDecimal("0.000146")))),
                property("proofOfStake", is(equalTo(new BigDecimal("0.00001526"))))
        ));
    }

    @Test
    @Override
    public void testGetbestblockhash(final Normalized normalized) {
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
        final Block block = normalized.getblock(ARBITRARY_BLOCK_HASH);
        assertThat(block, hasAllIgnoringNull(
                property("hash", is(equalTo(ARBITRARY_BLOCK_HASH))),
                property("previousblockhash", is(equalTo("3d199a5a9a5ff70b7fd1400579407ad1e5582ed7b64c5243377e59c58ed388a8"))),
                property("nextblockhash", is(equalTo("0aa2a1318703229fa2a657172c6b29d55497941d7d296e0c3803962c3a69c7cb"))),
                property("confirmations", is(equalTo(6L))),
                property("size", is(equalTo(174L))),
                property("height", is(equalTo(16L))),
                property("time", is(equalTo(1537820773L))),
                property("tx", hasSize(1))
        ));

        assertThat(block.tx.get(0), is(equalTo("8adbf29c95ee7b9116352a23ee7bb85c5358b0c17b52ea3100ea48034a1efb42")));
    }

    @Test
    @Override
    public void testGettransaction(final Normalized normalized) {
        final Transaction tx = normalized.gettransaction(TRANSACTION_ID);
        assertThat(tx, hasAllIgnoringNull(
                property("id", is(equalTo("e3348178922d0e9f27422bd2a62bb22be97f20b90bc0121d833adcc5d07997a0"))),
                property("time", is(equalTo(1537821085L))),
                property("amount", is(equalTo(new BigDecimal("-345.76709498")))),
                property("fee", is(equalTo(new BigDecimal("-0.00010")))),
                property("confirmations", is(equalTo(1L))),
                property("blockhash", is(equalTo("8d5598d979159a4c75b2828b63b4e6dbb2e7d87ccf405922d86ab38f05871b32"))),
                property("details", hasSize(1))
        ));

        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo("n17aowaVLQipu7Bfa5P51G3K76jMkFx3Hs"))),
                property("amount", is(equalTo(new BigDecimal("-345.76709498")))),
                property("fee", is(equalTo(new BigDecimal("-0.00010"))))
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
    public void daemonNotifiesListenerOnSend(final Normalized normalized) throws IOException, InterruptedException {
        // Setup
        final CountDownLatch latch = new CountDownLatch(1);
        NotificationListener listener = new NotificationListener(LISTENER_PORT);
        listener.setTransformer(new NotificationListener.TransactionTransformer(normalized));
        listener.addObserver((Observable o, Object o1) -> {
            result = o1;
            latch.countDown();
        });
        
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
