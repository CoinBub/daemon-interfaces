package tech.coinbub.daemon.poli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Poli;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetNewAddressIT;

@ExtendWith(Dockerized.class)
@DockerizedPoli
public class GetNewAddressIT implements StandardGetNewAddressIT<Poli> {
    @Test
    @Override
    public void canGetAddressForDefaultAccount(final Poli poli) {
        final String address = poli.getnewaddress();
        assertThat(address.length(), is(equalTo(34)));
    }
}
