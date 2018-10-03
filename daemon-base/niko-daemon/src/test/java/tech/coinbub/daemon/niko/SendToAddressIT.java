package tech.coinbub.daemon.niko;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import java.math.BigDecimal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Niko;
import tech.coinbub.daemon.niko.Transaction;
import tech.coinbub.daemon.niko.TransactionDetail;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardSendToAddressIT;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;

@ExtendWith(Dockerized.class)
@DockerizedNiko
public class SendToAddressIT implements StandardSendToAddressIT<Niko> {
    public static final int LISTENER_PORT = 20010;
    public static final String VALID_ADDRESS = "mhhhbW3S4XqYiJSzW2kx6krxYSm2JhGRqd";

    @Test
    @Override
    public void throwsErrorOnInvalidAddress(final Niko niko) {
        final JsonRpcClientException ex = Assertions.assertThrows(JsonRpcClientException.class, () -> {
            niko.sendtoaddress("abc", BigDecimal.ONE);
        });
        assertThat(ex.getMessage(), is(equalTo("Invalid NIKO address")));
    }

    @Test
    @Override
    public void sendToAddressWithNoComments(final Niko niko) {
        final String txid = niko.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE);
        final Transaction tx = niko.gettransaction(txid);
        assertThat(tx.amount, is(equalTo(new BigDecimal("-1.0"))));
    }

    @Test
    @Override
    public void sendToAddressWithSourceComment(final Niko niko) {
        final String txid = niko.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!");
        final Transaction tx = niko.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", not(isEmptyString())),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(equalTo(new BigDecimal("-0.00010")))),
                property("time", is(not(nullValue()))),
                property("timereceived", is(not(nullValue()))),
                property("locktime", is(not(nullValue()))),
                property("confirmations", is(equalTo(0L))),
                property("version", is(equalTo(1L))),
                property("vin", hasSize(1)),
                property("vout", hasSize(2)),
                property("details", hasSize(1)),
                property("comment", is(equalTo("test transaction!")))
        ));
        
        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("account", isEmptyString()),
                property("address", is(equalTo(VALID_ADDRESS))),
                property("category", is(equalTo(TransactionDetail.Category.send))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(equalTo(new BigDecimal("-0.00010"))))
        ));
    }

    @Test
    @Override
    public void sendToAddressWithDestinationComment(final Niko niko) {
        final String txid = niko.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!", "receiving test!");
        final Transaction tx = niko.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", not(isEmptyString())),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(equalTo(new BigDecimal("-0.00010")))),
                property("time", is(not(nullValue()))),
                property("timereceived", is(not(nullValue()))),
                property("locktime", is(not(nullValue()))),
                property("confirmations", is(equalTo(0L))),
                property("version", is(equalTo(1L))),
                property("vin", hasSize(1)),
                property("vout", hasSize(2)),
                property("details", hasSize(1)),
                property("comment", is(equalTo("test transaction!"))),
                property("to", is(equalTo("receiving test!")))
        ));
        
        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("account", isEmptyString()),
                property("address", is(equalTo(VALID_ADDRESS))),
                property("category", is(equalTo(TransactionDetail.Category.send))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(equalTo(new BigDecimal("-0.00010"))))
        ));
    }

}
