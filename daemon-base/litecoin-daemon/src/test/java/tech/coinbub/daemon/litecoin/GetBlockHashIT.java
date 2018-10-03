package tech.coinbub.daemon.litecoin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Litecoin;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetBlockHashIT;

@ExtendWith(Dockerized.class)
@DockerizedLitecoin
public class GetBlockHashIT implements StandardGetBlockHashIT<Litecoin> {
    public static final Long HEIGHT = 6L;

    @Test
    @Override
    public void canGetBlockHash(final Litecoin litecoin) {
        final String best = litecoin.getblockhash(HEIGHT);
        assertThat(best, is(equalTo("10e1e5c8e08eacbbd765607157341ccc300864e3b4cf8a7978daca7222a40fae")));
    }
}
