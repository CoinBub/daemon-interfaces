package tech.coinbub.daemon.niko;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tech.coinbub.daemon.Niko;
import tech.coinbub.daemon.NormalizedNiko;
import tech.coinbub.daemon.testutils.docker.Dockerize;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Dockerize(
        image = "coinbub/niko-daemon",
        port = 10001,
        name = "test-niko",
        cmd = { "nikod" },
        conf = "/home/crypto/.NIKO",
        rawInterface = Niko.class,
        normalizedInterface = NormalizedNiko.class
)
public @interface DockerizedNiko {}
