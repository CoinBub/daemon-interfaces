package tech.coinbub.daemon.bitcoin;

import tech.coinbub.daemon.Bitcoin;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.testutils.docker.Dockerized;
import tech.coinbub.daemon.testutils.suites.StandardGetNewAddressIT;

@ExtendWith(Dockerized.class)
@DockerizedBitcoin
public class GetNewAddressIT implements StandardGetNewAddressIT<Bitcoin> {
    @Test
    @Override
    public void canGetAddressForDefaultAccount(final Bitcoin bitcoin) {
        final String address = bitcoin.getnewaddress();
        assertThat(address.length(), is(equalTo(35)));
    }
}
