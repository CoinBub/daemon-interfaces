package tech.coinbub.daemon.testutils.suites;

import org.junit.jupiter.api.Test;

public interface StandardGetBlockIT<T> {
    @Test
    void canGetSimpleBlock(T coin);
    @Test
    void canGetVerboseBlock(T coin);
}
