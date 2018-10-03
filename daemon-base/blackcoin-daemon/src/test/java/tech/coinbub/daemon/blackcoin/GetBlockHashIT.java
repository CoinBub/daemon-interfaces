package tech.coinbub.daemon.blackcoin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Blackcoin;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetBlockHashIT;

@ExtendWith(Dockerized.class)
@DockerizedBlackcoin
public class GetBlockHashIT implements StandardGetBlockHashIT<Blackcoin> {
    public static final Long HEIGHT = 6L;

    @Test
    @Override
    public void canGetBlockHash(final Blackcoin blackcoin) {
        final String best = blackcoin.getblockhash(HEIGHT);
        assertThat(best, is(equalTo("976b82a23b2f82c206fe5114515791ad232dbfd43c5a65e03b5babb8029b6213")));
    }
}
