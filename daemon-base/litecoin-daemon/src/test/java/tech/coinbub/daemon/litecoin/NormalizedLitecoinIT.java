package tech.coinbub.daemon.litecoin;

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
@DockerizedLitecoin
public class NormalizedLitecoinIT implements StandardNormalizedIT {
    public static final String BEST_BLOCK_HASH = "fd5aa78425d7dc2200de2d500d034d544abbccbec50bc3220561a6ede242a97b";
    public static final long ARBITRARY_BLOCK_HEIGHT = 22L;
    public static final String ARBITRARY_BLOCK_HASH = "fd589520e81b383f7113029e576c3697ee91a983184cc6601bb7cfb055d35c8e";
    public static final int ADDRESS_LENGTH = 34;
    public static final String VALID_ADDRESS = "mp5fxxHxQ2hELnENjttUDUQF7jkUMhLPEC";
    public static final String TRANSACTION_ID = "76d3cb8276d4f8376e960278d766ff5066f40a78192a5b161c05739f26182da9";
    public static final BigDecimal FEE = new BigDecimal("-0.00384");
    public static final BigDecimal FEE_DEVIATION = new BigDecimal("0.0001");
    public static final int LISTENER_PORT = 20010;

    private Object result = null;

    @Test
    @Override
    public void testGetinfo(final Normalized normalized) {
        final tech.coinbub.daemon.normalized.model.Info info = normalized.getinfo();
        assertThat(info, hasAllIgnoringNull(
            property("version", is(equalTo("150100"))),
            property("protocolversion", is(equalTo(70015L))),
            property("walletversion", is(equalTo(139900L))),
            property("balance", is(equalTo(new BigDecimal("1000.0")))),
            property("newmint", is(equalTo(new BigDecimal("5000.0")))),
            property("blocks", is(equalTo(120L))),
            property("timeoffset", is(equalTo(0L))),
            property("moneysupply", is(equalTo(new BigDecimal("6000.0")))),
            property("connections", is(equalTo(0L))),
            property("proxy", is(nullValue())),
            property("ip", is(nullValue())),
            property("difficulty", is(instanceOf(tech.coinbub.daemon.normalized.model.Info.Difficulty.class))),
            property("testnet", is(nullValue())),
            property("keypoololdest", is(equalTo(1526482210L))),
            property("keypoolsize", is(equalTo(1000L))),
            property("paytxfee", is(equalTo(new BigDecimal("0.0")))),
            property("warnings", is(equalTo(""))),
            property("errors", is(equalTo(null)))
        ));

        final tech.coinbub.daemon.normalized.model.Info.Difficulty difficulty = info.difficulty;
        assertThat(difficulty, hasAllIgnoringNull(
                property("proofOfWork", is(equalTo(new BigDecimal("4.656542373906925E-10")))),
                property("proofOfStake", is(nullValue()))
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
                property("previousblockhash", is(equalTo("2e90a7726131709f77146db5d633ee5e57905f5cb5294cd2a7b3a5626aa1e327"))),
                property("confirmations", is(equalTo(99L))),
                property("size", is(equalTo(227L))),
                property("height", is(equalTo(22L))),
                property("time", is(equalTo(1526482383L))),
                property("tx", hasSize(1))
        ));
        
        assertThat(block.tx.get(0), is(equalTo("bd238353c15a89b6bb42b284d9a824bc0c2abfa0d689ec9f2981977752bc3f14")));
    }

    @Test
    @Override
    public void testGettransaction(final Normalized normalized) {
        final Transaction tx = normalized.gettransaction(TRANSACTION_ID);
        assertThat(tx, hasAllIgnoringNull(
                property("id", is(equalTo(TRANSACTION_ID))),
                property("blockhash", is(equalTo("fd5aa78425d7dc2200de2d500d034d544abbccbec50bc3220561a6ede242a97b"))),
                property("amount", is(equalTo(new BigDecimal("0.0")))),
                property("confirmations", is(equalTo(1L))),
                property("time", is(equalTo(1526482387L))),
                property("details", hasSize(1))
        ));

        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo("n35eVUVBZZVbhBZeF5x8Q8LRpUonKXdAY7"))),
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
                property("confirmations", is(equalTo(0L))),
                property("time", is(not(nullValue()))),
                property("comment_from", is(equalTo("test transaction!"))),
                property("comment_to", is(equalTo("receiving test!"))),
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
    public void daemonNotifiesListenerOnSend(final Normalized normalized) throws Exception {
        // Setup
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

            latch.await(500000, TimeUnit.MILLISECONDS);
            assertThat(result, is(not(nullValue())));
            assertThat(((tech.coinbub.daemon.normalized.model.Transaction) result).id.length(), is(equalTo(64)));
        } finally {
            listener.stop();
        }
    }
}
