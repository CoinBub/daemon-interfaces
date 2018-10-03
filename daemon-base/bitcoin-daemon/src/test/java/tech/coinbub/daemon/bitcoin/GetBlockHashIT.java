package tech.coinbub.daemon.bitcoin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Bitcoin;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetBlockHashIT;

@ExtendWith(Dockerized.class)
@DockerizedBitcoin
public class GetBlockHashIT implements StandardGetBlockHashIT<Bitcoin> {

    @Test
    @Override
    public void canGetBlockHash(final Bitcoin coin) {
        assertThat(coin.getblockhash(22L), is(equalTo("29128d296a50754ebc1231a5ceba6b2526c995e4925b48a37159156f54196d73")));
    }

}
