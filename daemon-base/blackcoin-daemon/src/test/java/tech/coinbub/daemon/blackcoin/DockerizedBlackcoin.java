package tech.coinbub.daemon.blackcoin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tech.coinbub.daemon.Blackcoin;
import tech.coinbub.daemon.NormalizedBlackcoin;
import tech.coinbub.daemon.testutils.docker.Dockerize;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Dockerize(
        image = "coinbub/blackcoin-daemon",
        port = 10001,
        name = "test-blackcoin",
        cmd = { "blackcoind" },
        conf = "/home/crypto/.blackcoin",
        rawInterface = Blackcoin.class,
        normalizedInterface = NormalizedBlackcoin.class
)
public @interface DockerizedBlackcoin {}
