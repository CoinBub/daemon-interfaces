package tech.coinbub.daemon.dash;

import java.math.BigDecimal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Dash;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.suites.StandardGetTransactionIT;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;

@ExtendWith(Dockerized.class)
@DockerizedDash
public class GetTransactionIT implements StandardGetTransactionIT<Dash> {
    @Test
    public void canGetTransaction(final Dash dash) {
        final Transaction tx = dash.gettransaction("8b2e01ff7c33b7a7d68fc7ebb196bb525ca103df5acb60e4a6b90f1d0cd5f416");
        assertThat(tx, hasAllIgnoringNull(
                property("txid", is(equalTo("8b2e01ff7c33b7a7d68fc7ebb196bb525ca103df5acb60e4a6b90f1d0cd5f416"))),
                property("amount", is(equalTo(new BigDecimal("-750.0")))),
                property("confirmations", is(equalTo(1L))),
                property("instantlock", is(equalTo(false))),
                property("blockhash", is(equalTo("03f452916f5fb7f261700da37da8f2568c30a6aa6f73b3954865474fc7c18a60"))),
                property("blockindex", is(equalTo(1L))),
                property("blocktime", is(equalTo(1538168078L))),
                property("walletconflicts", hasSize(0)),
                property("time", is(equalTo(1538168047L))),
                property("timereceived", is(equalTo(1538168047L))),
                property("bip125_replaceable", is(equalTo(false))),
                property("details", hasSize(1)),
                property("hex", is(equalTo("01000000026c6ce5d4f0bec91681a5e3e1c1ab8f66f5f8c6860d4b39e6d782bdcb0ced67aa0000000049483045022100bba999f007870911eca9f13cf25a77b0c643dfcb6f6795433f3e8a332c854c9f0220205dea9aec1336458581f8ca904efb92fb602a5d5a7151824465c706ab52dfc901feffffffb7e6b61f48d227dae7e6134c16920a0930e4355d3f5027a4873be600f158f9ca000000004847304402202ea73fb34d51bb53ed376f5bb752dc156d1652df9905a3378024da9614652b1a022037a040b63f616405df7ac4e69b051110315737f82a21ac9420d98e8fa0a1bc4101feffffff02ceb81dd2050000001976a914e23f41ac1569e0c4cf71a2fa2b1666bfbedf4b9788ac002e5976110000001976a91471f6547ac8ec444a3749ca087915d6ab588057e288ac6e000000"))),
                property("fee", is(equalTo(new BigDecimal("-0.00000306"))))
        ));

        final TransactionDetail detail = tx.details.get(0);
        assertThat(detail, hasAllIgnoringNull(
                property("account", isEmptyString()),
                property("address", is(equalTo("yWi2JBsWQzDbqUC7MRJeeA8Q6y4JPQih9A"))),
                property("category", is(equalTo(TransactionDetail.Category.send))),
                property("amount", is(equalTo(new BigDecimal("-750.0")))),
                property("fee", is(equalTo(new BigDecimal("-0.00000306")))),
                property("vout", is(equalTo(1L))),
                property("abandoned", is(equalTo(false)))
        ));
    }

    @Test
    @Override
    public void canGetTransactionWithMultipleOuts(final Dash coin) {}
}
