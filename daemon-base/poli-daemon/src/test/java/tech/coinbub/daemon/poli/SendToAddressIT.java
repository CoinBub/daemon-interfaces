package tech.coinbub.daemon.poli;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.coinbub.daemon.Poli;
import tech.coinbub.daemon.poli.Transaction;
import tech.coinbub.daemon.poli.TransactionDetail;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.suites.StandardSendToAddressIT;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;

@ExtendWith(Dockerized.class)
@DockerizedPoli
public class SendToAddressIT implements StandardSendToAddressIT<Poli> {
    public static final Logger LOGGER = LoggerFactory.getLogger(SendToAddressIT.class);
    public static final String VALID_ADDRESS = "mm1PNXvwUo9PB7eScmoNyTjz93Wwt6qwDM";

    @Test
    @Override
    public void throwsErrorOnInvalidAddress(final Poli poli) {
        final JsonRpcClientException ex = Assertions.assertThrows(JsonRpcClientException.class, () -> {
            poli.sendtoaddress("abc", BigDecimal.ONE);
        });
        assertThat(ex.getMessage(), is(equalTo("Invalid POLI address")));
    }

    @Test
    @Override
    public void sendToAddressWithNoComments(final Poli poli) throws IOException, InterruptedException {
        final String txid = poli.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE);
        final Transaction tx = poli.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", is(equalTo(txid))),
                property("version", is(equalTo(1L))),
                property("time", is(not(nullValue()))),
                property("timereceived", is(not(nullValue()))),
                property("locktime", is(not(nullValue()))),
                property("vin", hasSize(1)),
                property("vout", hasSize(2)),
                property("confirmations", is(equalTo(0L))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(equalTo(new BigDecimal("-0.00010")))),
                property("details", hasSize(1))
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
    public void sendToAddressWithSourceComment(final Poli poli) throws IOException, InterruptedException {
        final String txid = poli.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!");
        final Transaction tx = poli.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", is(equalTo(txid))),
                property("version", is(equalTo(1L))),
                property("time", is(not(nullValue()))),
                property("timereceived", is(not(nullValue()))),
                property("locktime", is(not(nullValue()))),
                property("vin", hasSize(1)),
                property("vout", hasSize(2)),
                property("confirmations", is(equalTo(0L))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(equalTo(new BigDecimal("-0.00010")))),
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
    public void sendToAddressWithDestinationComment(final Poli poli) throws IOException, InterruptedException {
        final String txid = poli.sendtoaddress(VALID_ADDRESS, BigDecimal.ONE, "test transaction!", "receiving test!");
        final Transaction tx = poli.gettransaction(txid);
        assertThat(tx, hasAllIgnoringNull(
                property("txid", is(equalTo(txid))),
                property("version", is(equalTo(1L))),
                property("time", is(not(nullValue()))),
                property("timereceived", is(not(nullValue()))),
                property("locktime", is(not(nullValue()))),
                property("vin", hasSize(1)),
                property("vout", hasSize(2)),
                property("confirmations", is(equalTo(0L))),
                property("amount", is(equalTo(new BigDecimal("-1.0")))),
                property("fee", is(equalTo(new BigDecimal("-0.00010")))),
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
