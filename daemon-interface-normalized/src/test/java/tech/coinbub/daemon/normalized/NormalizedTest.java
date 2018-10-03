package tech.coinbub.daemon.normalized;

import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;


public class NormalizedTest {
    @Test
    public void testGetRawType() {
        MatcherAssert.assertThat(Normalized.getRawType(NormalizedBitcoin.class), is(equalTo(Bitcoin.class)));
    }

    public interface Bitcoin {}
    public abstract class NormalizedBitcoin implements Normalized<Bitcoin> {}
    
}
