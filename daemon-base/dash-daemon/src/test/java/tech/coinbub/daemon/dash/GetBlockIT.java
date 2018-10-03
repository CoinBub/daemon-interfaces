package tech.coinbub.daemon.dash;

import java.math.BigDecimal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Dash;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import static tech.coinbub.daemon.testutils.matchers.BeanPropertyMatcher.property;
import tech.coinbub.daemon.testutils.suites.StandardGetBlockIT;
import static tech.coinbub.daemon.testutils.matchers.BeanMatcher.hasAllIgnoringNull;

@ExtendWith(Dockerized.class)
@DockerizedDash
public class GetBlockIT implements StandardGetBlockIT<Dash> {
    @Override
    public void canGetSimpleBlock(final Dash dash) {
        final Block block = dash.getblock("20a3f5d2b3f1b61845c8e96124192a7d6ec072ac3b8913278d8f5c6dfde5b30b");
        assertThat(block, hasAllIgnoringNull(
                property("hash", is(equalTo("20a3f5d2b3f1b61845c8e96124192a7d6ec072ac3b8913278d8f5c6dfde5b30b"))),
                property("confirmations", is(equalTo(90L))),
                property("size", is(equalTo(180L))),
                property("height", is(equalTo(22L))),
                property("version", is(equalTo(536870912L))),
                property("merkleroot", is(equalTo("ffb2bc52163ab14c3dbf75b02cf1f8a446cea8288cca05902b7cb796bb386600"))),
                property("tx", hasSize(1)),
                property("time", is(equalTo(1538167886L))),
                property("mediantime", is(equalTo(1538167886L))),
                property("nonce", is(equalTo(9L))),
                property("bits", is(equalTo("207fffff"))),
                property("difficulty", is(equalTo(new BigDecimal("4.656542373906925e-10")))),
                property("chainwork", is(equalTo("000000000000000000000000000000000000000000000000000000000000002e"))),
                property("previousblockhash", is(equalTo("3ac03d02172ce1449fb8244c848117ac5a6516ae15518f5722ac612726461685"))),
                property("nextblockhash", is(equalTo("4b6d0b2dff1c58587c33175a360873ac229fc80c2a1d9d1b40f8a6a4f8932cb3")))
        ));
        
        assertThat(block.tx.get(0), is(equalTo("ffb2bc52163ab14c3dbf75b02cf1f8a446cea8288cca05902b7cb796bb386600")));
    }

    @Override
    public void canGetVerboseBlock(final Dash dash) {
        final Block block = dash.getblock("20a3f5d2b3f1b61845c8e96124192a7d6ec072ac3b8913278d8f5c6dfde5b30b", true);
        assertThat(block, hasAllIgnoringNull(
                property("hash", is(equalTo("20a3f5d2b3f1b61845c8e96124192a7d6ec072ac3b8913278d8f5c6dfde5b30b"))),
                property("confirmations", is(equalTo(90L))),
                property("size", is(equalTo(180L))),
                property("height", is(equalTo(22L))),
                property("version", is(equalTo(536870912L))),
                property("merkleroot", is(equalTo("ffb2bc52163ab14c3dbf75b02cf1f8a446cea8288cca05902b7cb796bb386600"))),
                property("tx", hasSize(1)),
                property("time", is(equalTo(1538167886L))),
                property("mediantime", is(equalTo(1538167886L))),
                property("nonce", is(equalTo(9L))),
                property("bits", is(equalTo("207fffff"))),
                property("difficulty", is(equalTo(new BigDecimal("4.656542373906925e-10")))),
                property("chainwork", is(equalTo("000000000000000000000000000000000000000000000000000000000000002e"))),
                property("previousblockhash", is(equalTo("3ac03d02172ce1449fb8244c848117ac5a6516ae15518f5722ac612726461685"))),
                property("nextblockhash", is(equalTo("4b6d0b2dff1c58587c33175a360873ac229fc80c2a1d9d1b40f8a6a4f8932cb3")))
        ));
        
        assertThat(block.tx.get(0), is(equalTo("ffb2bc52163ab14c3dbf75b02cf1f8a446cea8288cca05902b7cb796bb386600")));
    }
    
}
