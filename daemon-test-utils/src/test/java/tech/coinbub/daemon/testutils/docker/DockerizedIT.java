package tech.coinbub.daemon.testutils.docker;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.coinbub.daemon.normalized.Normalized;
import tech.coinbub.daemon.support.NormalizedTestDaemon;
import tech.coinbub.daemon.support.TestDaemon;

@ExtendWith(Dockerized.class)
@Dockerize(
        image = "coinbub/dockerized-test",
        name = "test-dockerized",
        port = 8080,
        rawInterface = TestDaemon.class,
        normalizedInterface = NormalizedTestDaemon.class,
        conf = "/home/test/.test"
)
public class DockerizedIT {
    @Test
    public void canInjectRawInterface(final TestDaemon daemon) {
        assertThat(daemon.getbestblockhash(), is(equalTo("abc")));
    }
    @Test
    public void canInjectGeneralNormalizedInterface(final Normalized normalized) {
        assertThat(normalized.getbestblockhash(), is(equalTo("abc")));
    }
    @Test
    public void canInjectSpecificNormalizedInterface(final NormalizedTestDaemon normalized) {
        assertThat(normalized.getbestblockhash(), is(equalTo("abc")));
    }
}
