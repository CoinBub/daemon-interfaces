package tech.coinbub.daemon.testutils.suites;

import org.junit.jupiter.api.Test;

public interface StandardGetBlockHashIT<T> {
    @Test
    void canGetBlockHash(T coin);
}
