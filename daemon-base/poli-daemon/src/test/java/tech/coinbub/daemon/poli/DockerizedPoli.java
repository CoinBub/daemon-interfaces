package tech.coinbub.daemon.poli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tech.coinbub.daemon.NormalizedPoli;
import tech.coinbub.daemon.Poli;
import tech.coinbub.daemon.testutils.docker.Dockerize;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Dockerize(
        image = "coinbub/poli-daemon",
        port = 10001,
        name = "test-poli",
        cmd = { "polid" },
        conf = "/home/crypto/.POLI",
        rawInterface = Poli.class,
        normalizedInterface = NormalizedPoli.class
)
public @interface DockerizedPoli {}
