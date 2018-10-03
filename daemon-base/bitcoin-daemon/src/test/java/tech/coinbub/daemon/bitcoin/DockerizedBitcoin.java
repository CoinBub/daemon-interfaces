package tech.coinbub.daemon.bitcoin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tech.coinbub.daemon.Bitcoin;
import tech.coinbub.daemon.NormalizedBitcoin;
import tech.coinbub.daemon.testutils.docker.Dockerize;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Dockerize(
        image = "coinbub/bitcoin-daemon",
        port = 10001,
        name = "test-bitcoin",
        cmd = { "bitcoind", "-rpcport=10001" },
        conf = "/home/crypto/.bitcoin",
        rawInterface = Bitcoin.class,
        normalizedInterface = NormalizedBitcoin.class,
        persistentOnError = true
)
public @interface DockerizedBitcoin {}
