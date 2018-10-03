package tech.coinbub.daemon.niko;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Niko;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetBlockHashIT;

@ExtendWith(Dockerized.class)
@DockerizedNiko
public class GetBlockHashIT implements StandardGetBlockHashIT<Niko> {
    public static final Long HEIGHT = 22L;

    @Test
    @Override
    public void canGetBlockHash(final Niko niko) {
        final String best = niko.getblockhash(HEIGHT);
        assertThat(best, is(equalTo("06e21472e1832d4c9b926b96e5e038e8932f502e3fb0b98b6359ebe8812c0449")));
    }
}
