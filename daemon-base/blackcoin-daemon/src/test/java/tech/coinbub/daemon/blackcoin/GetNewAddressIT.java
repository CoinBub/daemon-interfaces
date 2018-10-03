package tech.coinbub.daemon.blackcoin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Blackcoin;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetNewAddressIT;

@ExtendWith(Dockerized.class)
@DockerizedBlackcoin
public class GetNewAddressIT implements StandardGetNewAddressIT<Blackcoin> {
    @Test
    @Override
    public void canGetAddressForDefaultAccount(final Blackcoin blackcoin) {
        final String address = blackcoin.getnewaddress();
        assertThat(address.length(), is(equalTo(34)));
    }
}
