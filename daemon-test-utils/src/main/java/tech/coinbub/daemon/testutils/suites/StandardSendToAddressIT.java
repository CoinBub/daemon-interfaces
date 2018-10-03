package tech.coinbub.daemon.testutils.suites;

import org.junit.jupiter.api.Test;

public interface StandardSendToAddressIT<T> {
    @Test
    void throwsErrorOnInvalidAddress(final T coin) throws Exception;
    @Test
    void sendToAddressWithNoComments(final T coin) throws Exception;
    @Test
    void sendToAddressWithSourceComment(final T coin) throws Exception;
    @Test
    void sendToAddressWithDestinationComment(final T coin) throws Exception;
}
