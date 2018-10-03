package tech.coinbub.daemon.niko;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Niko;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetNewAddressIT;

@ExtendWith(Dockerized.class)
@DockerizedNiko
public class GetNewAddressIT implements StandardGetNewAddressIT<Niko> {
    @Test
    @Override
    public void canGetAddressForDefaultAccount(final Niko niko) {
        final String address = niko.getnewaddress();
        assertThat(address.length(), is(equalTo(34)));
    }
}
