package tech.coinbub.daemon.dash;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import java.io.IOException;
import java.math.BigDecimal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.number.BigDecimalCloseTo.closeTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Dash;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.suites.StandardSendToAddressIT;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;

@ExtendWith(Dockerized.class)
@DockerizedDash
public class SendToAddressIT implements StandardSendToAddressIT<Dash> {
    public static final String VALID_ADDRESS = "yMQcwprKzfs4XYVouPoeYQyvHWZFzdfJR5";

    @Test
    @Override
    public void throwsErrorOnInvalidAddress(final Dash dash) {
        final JsonRpcClientException ex = Assertions.assertThrows(JsonRpcClientException.class, () -> {
            dash.sendtoaddress("abc", BigDecimal.ONE);
        });
        assertThat(ex.getMessage(), is(equalTo("Invalid Dash address")));
    }

    @Test
    @Override
    public void sendToAddressWithNoComments(final Dash dash) throws InterruptedException, IOException {
        final String txid = dash.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE);
        final Transaction tx = dash.gettransaction(txid);
        assertThat(tx.amount, is(equalTo(new BigDecimal("-1.0"))));
    }

    @Test
    @Override
    public void sendToAddressWithSourceComment(final Dash dash) throws InterruptedException, IOException {
        final String txid = dash.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!");
        final Transaction tx = dash.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", not(isEmptyString())),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(new BigDecimal("-0.00000225"), new BigDecimal("0.000015")))),
                property("confirmations", is(equalTo(0L))),
                property("instantlock", is(equalTo(false))),
                property("trusted", is(equalTo(true))),
                property("walletconflicts", hasSize(0)),
                property("time", is(not(nullValue()))),
                property("timereceived", is(not(nullValue()))),
                property("bip125_replaceable", is(equalTo(false))),
                property("details", hasSize(1)),
                property("comment", is(equalTo("test transaction!"))),
                property("hex", not(isEmptyString()))
        ));
        
        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("account", isEmptyString()),
                property("address", is(equalTo(VALID_ADDRESS))),
                property("category", is(equalTo(TransactionDetail.Category.send))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(new BigDecimal("-0.00000225"), new BigDecimal("0.000015")))),
                property("abandoned", is(equalTo(false))),
                property("vout", is(equalTo(0L)))
        ));
    }

    @Test
    @Override
    public void sendToAddressWithDestinationComment(final Dash dash) throws InterruptedException, IOException {
        final String txid = dash.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!", "receiving test!");
        final Transaction tx = dash.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", not(isEmptyString())),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(new BigDecimal("-0.00000225"), new BigDecimal("0.000015")))),
                property("confirmations", is(equalTo(0L))),
                property("instantlock", is(equalTo(false))),
                property("trusted", is(equalTo(true))),
                property("walletconflicts", hasSize(0)),
                property("time", is(not(nullValue()))),
                property("timereceived", is(not(nullValue()))),
                property("bip125_replaceable", is(equalTo(false))),
                property("details", hasSize(1)),
                property("comment", is(equalTo("test transaction!"))),
                property("to", is(equalTo("receiving test!"))),
                property("hex", not(isEmptyString()))
        ));
        
        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("account", isEmptyString()),
                property("address", is(equalTo(VALID_ADDRESS))),
                property("category", is(equalTo(TransactionDetail.Category.send))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(closeTo(new BigDecimal("-0.00000225"), new BigDecimal("0.000015")))),
                property("abandoned", is(equalTo(false))),
                property("vout", is(equalTo(0L)))
        ));
    }
}
