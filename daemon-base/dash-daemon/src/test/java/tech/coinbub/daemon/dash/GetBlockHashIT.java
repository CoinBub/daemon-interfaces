package tech.coinbub.daemon.dash;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Dash;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetBlockHashIT;

@ExtendWith(Dockerized.class)
@DockerizedDash
public class GetBlockHashIT implements StandardGetBlockHashIT<Dash> {
    public static final Long HEIGHT = 22L;

    @Test
    @Override
    public void canGetBlockHash(final Dash dash) {
        final String best = dash.getblockhash(HEIGHT);
        assertThat(best, is(equalTo("20a3f5d2b3f1b61845c8e96124192a7d6ec072ac3b8913278d8f5c6dfde5b30b")));
    }
}
