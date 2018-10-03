package tech.coinbub.daemon.testutils.suites;

import org.junit.jupiter.api.Test;

public interface StandardGetTransactionIT<T> {
    @Test
    void canGetTransactionWithMultipleOuts(T coin);
}
