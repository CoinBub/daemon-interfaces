package tech.coinbub.daemon.poli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Poli;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetBlockHashIT;

@ExtendWith(Dockerized.class)
@DockerizedPoli
public class GetBlockHashIT implements StandardGetBlockHashIT<Poli> {
    public static final Long HEIGHT = 22L;

    @Test
    @Override
    public void canGetBlockHash(final Poli poli) {
        final String best = poli.getblockhash(HEIGHT);
        assertThat(best, is(equalTo("10e8298ecc7a4668273cf69aa2f66324b100cacfd0916cf60b8e46df14d1b9f4")));
    }
}
