package tech.coinbub.daemon.dash;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tech.coinbub.daemon.Dash;
import tech.coinbub.daemon.NormalizedDash;
import tech.coinbub.daemon.testutils.docker.Dockerize;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Dockerize(
        image = "coinbub/dash-daemon",
        port = 10001,
        name = "test-dash",
        cmd = { "dashd" },
        conf = "/home/crypto/.dashcore",
        rawInterface = Dash.class,
        normalizedInterface = NormalizedDash.class
)
public @interface DockerizedDash {}
