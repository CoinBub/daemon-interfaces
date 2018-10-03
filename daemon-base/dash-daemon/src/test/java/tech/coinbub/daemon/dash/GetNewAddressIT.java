package tech.coinbub.daemon.dash;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Dash;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetNewAddressIT;

@ExtendWith(Dockerized.class)
@DockerizedDash
public class GetNewAddressIT implements StandardGetNewAddressIT<Dash> {
    @Test
    @Override
    public void canGetAddressForDefaultAccount(final Dash dash) {
        final String address = dash.getnewaddress();
        assertThat(address.length(), is(equalTo(34)));
    }
}
