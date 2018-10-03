package tech.coinbub.daemon.testutils.suites;

import org.junit.jupiter.api.Test;

public interface StandardGetNewAddressIT<T> {
    @Test
    void canGetAddressForDefaultAccount(T coin);
}
