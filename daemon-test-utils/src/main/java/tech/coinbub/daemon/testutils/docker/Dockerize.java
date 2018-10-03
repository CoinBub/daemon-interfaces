package tech.coinbub.daemon.testutils.docker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tech.coinbub.daemon.normalized.Normalized;

@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Dockerize {

    /**
     * Required. The Docker image to spin up. For example, `coinbub/bitcoin-daemon`.
     * @return The name of the image to spin up
     */
    String image();

    /**
     * Optional. The name to assign to the container.
     * @return The name to assign to the container
     */
    String name();

    /**
     * The port upon which to reach the RPC server.
     * @return The port upon which to reach the RPC server
     */
    int port() default 10001;

    /**
     * The username for accessing the RPC server.
     * @return The username for accessing the RPC server
     */
    String user() default "user";

    /**
     * The password for accessing the RPC server.
     * @return The password for accessing the RPC server
     */
    String pass() default "pass";

    /**
     * Optional. The custom command to execute inside the container, rather than the default in the Dockerfile.
     * @return The custom command to execute inside the container
     */
    String[] cmd() default {};

    /**
     * The path of the RPC server's configuration directory for copying the configuration archive to the container.
     * @return The path of the container's configuration directory
     */
    String conf();

    /**
     * The raw daemon interface classname.
     * @return The raw daemon interface classname
     */
    Class rawInterface();

    /**
     * The normalized daemon interface classname
     * @return The normalized daemon interface classname
     */
    Class<? extends Normalized> normalizedInterface();

    /**
     * Whether or not the docker container should be stopped and reconfigured before each test.
     * @return True if we need to reconfigure the container, false otherwise
     */
    boolean reconfigurePerTest() default true;

    /**
     * Whether or not the docker container should be terminated and rebuilt before each test.
     * @return True if we need to rebuild the container, false otherwise
     */
    boolean rebuildPerTest() default false;

    /**
     * Whether or not to leave the docker container in place if a test fails. If true, the container will not be
     * destroyed when a test fails, but will be shut down and renamed to the test class + method names.
     * @return True if we need to leave the container up, false otherwise
     */
    boolean persistentOnError() default true;

    /**
     * Whether or not to leave the docker container in place on successful test completion. This will leave the
     * _last instance_ of the container alive. If the container is rebuild/reconfigured per-test, then this will be the
     * last build/configuration run. Java/JUnit do not run tests in a deterministic order, thus the result could be
     * different every time.
     * @return True if we need to leave the container up, false otherwise
     */
    boolean persistentOnSuccess() default false;
    
}
