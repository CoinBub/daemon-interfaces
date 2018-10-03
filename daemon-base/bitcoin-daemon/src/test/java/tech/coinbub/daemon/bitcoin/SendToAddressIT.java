package tech.coinbub.daemon.bitcoin;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import java.io.IOException;
import java.math.BigDecimal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Bitcoin;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.suites.StandardSendToAddressIT;
import static tech.coinbub.daemon.bitcoin.Constants.*;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;

@ExtendWith(Dockerized.class)
@DockerizedBitcoin
public class SendToAddressIT implements StandardSendToAddressIT<Bitcoin> {
    public static final String NEW_ADDRESS = "mk9GeqVDJd4ZNf7U77bmi2bendv3hQxbxD";

    @Test
    @Override
    public void throwsErrorOnInvalidAddress(final Bitcoin bitcoin) {
        final JsonRpcClientException ex = Assertions.assertThrows(JsonRpcClientException.class, () -> {
            bitcoin.sendtoaddress("abc", BigDecimal.ONE);
        });
        assertThat(ex.getMessage(), is(equalTo("Invalid address")));
    }

    @Test
    @Override
    public void sendToAddressWithNoComments(final Bitcoin bitcoin) throws IOException, InterruptedException {
        final String txid = bitcoin.sendtoaddress(NEW_ADDRESS, BigDecimal.ONE);
        final Transaction tx = bitcoin.gettransaction(txid);
        assertThat(tx.details, hasSize(1));
        assertThat(tx.details.get(0).amount, is(equalTo(new BigDecimal("-1.0"))));
    }

    @Test
    @Override
    public void sendToAddressWithSourceComment(final Bitcoin bitcoin) throws IOException, InterruptedException {
        final String txid = bitcoin.sendtoaddress(NEW_ADDRESS, BigDecimal.ONE, "test transaction!");
        final Transaction tx = bitcoin.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", is(notNullValue())),
                property("hex", is(notNullValue())),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION))),
                property("confirmations", is(equalTo(0L))),
                property("trusted", is(equalTo(true))),
                property("walletconflicts", hasSize(0)),
                property("time", is(notNullValue())),
                property("timereceived", is(notNullValue())),
                property("bip125_replaceable", is(equalTo(false))),
                property("details", hasSize(1)),
                property("comment", is(equalTo("test transaction!")))
        ));

        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo(NEW_ADDRESS))),
                property("category", is(equalTo(TransactionDetail.Category.send))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION))),
                property("vout", is(notNullValue())),
                property("abandoned", is(equalTo(false)))
        ));
    }

    @Test
    @Override
    public void sendToAddressWithDestinationComment(final Bitcoin bitcoin) throws IOException, InterruptedException {
        final String txid = bitcoin.sendtoaddress(NEW_ADDRESS, BigDecimal.ONE, "test transaction!", "receiving test!");
        final Transaction tx = bitcoin.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", is(notNullValue())),
                property("hex", is(notNullValue())),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION))),
                property("confirmations", is(equalTo(0L))),
                property("trusted", is(equalTo(true))),
                property("walletconflicts", hasSize(0)),
                property("time", is(notNullValue())),
                property("timereceived", is(notNullValue())),
                property("bip125_replaceable", is(equalTo(false))),
                property("details", hasSize(1)),
                property("comment", is(equalTo("test transaction!"))),
                property("to", is(equalTo("receiving test!")))
        ));
        
        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("address", is(equalTo(NEW_ADDRESS))),
                property("category", is(equalTo(TransactionDetail.Category.send))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(FEE, FEE_DEVIATION))),
                property("vout", is(notNullValue())),
                property("abandoned", is(equalTo(false)))
        ));
    }
}
