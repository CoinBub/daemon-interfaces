package tech.coinbub.daemon;

import java.io.IOException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NotificationListenerTest {
    private NotificationListener underTest;

    @BeforeEach
    public void setup() throws IOException {
        if (underTest != null) {
            underTest.stop();
            underTest = null;
        }
    }

    @Test
    public void noargInstantiationUsesRandomPort() throws IOException {
        underTest = new NotificationListener();
        assertThat(underTest.getPort(), is(greaterThan(0)));
    }

    @Test
    public void intArgInstantiationUsesGivenPort() throws IOException {
        underTest = new NotificationListener(49875);
        assertThat(underTest.getPort(), is(equalTo(49875)));
    }
}
