package tech.coinbub.daemon.litecoin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tech.coinbub.daemon.Litecoin;
import tech.coinbub.daemon.NormalizedLitecoin;
import tech.coinbub.daemon.testutils.docker.Dockerize;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Dockerize(
        image = "coinbub/litecoin-daemon",
        port = 10001,
        name = "test-litecoin",
        cmd = { "litecoind" },
        conf = "/home/crypto/.litecoin",
        rawInterface = Litecoin.class,
        normalizedInterface = NormalizedLitecoin.class
)
public @interface DockerizedLitecoin {}
