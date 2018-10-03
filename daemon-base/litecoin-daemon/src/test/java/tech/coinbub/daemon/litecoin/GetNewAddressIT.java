package tech.coinbub.daemon.litecoin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.Litecoin;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetNewAddressIT;

@ExtendWith(Dockerized.class)
@DockerizedLitecoin
public class GetNewAddressIT implements StandardGetNewAddressIT<Litecoin> {
    @Test
    @Override
    public void canGetAddressForDefaultAccount(final Litecoin litecoin) {
        final String address = litecoin.getnewaddress();
        assertThat(address.length(), is(equalTo(34)));
    }
}
