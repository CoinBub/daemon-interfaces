package tech.coinbub.daemon.testutils.suites;

import org.junit.jupiter.api.Test;
import tech.coinbub.daemon.normalized.Normalized;

public interface StandardNormalizedIT {
    @Test
    void testGetinfo(final Normalized normalized);
    @Test
    void testGetbestblockhash(Normalized normalized) throws Exception;
    @Test
    void testGetblockhash(Normalized normalized) throws Exception;
    @Test
    void testGetblock(Normalized normalized) throws Exception;
    @Test
    void testGettransaction(Normalized normalized) throws Exception;
    @Test
    void testGetnewaddress(Normalized normalized) throws Exception;
    @Test
    void testSendToAddressNoComments(Normalized normalized) throws Exception;
    @Test
    void testSendToAddressSourceComment(Normalized normalized) throws Exception;
    @Test
    void testSendToAddressDestComment(Normalized normalized) throws Exception;
    @Test
    void daemonNotifiesListenerOnSend(Normalized normalized) throws Exception;
}
