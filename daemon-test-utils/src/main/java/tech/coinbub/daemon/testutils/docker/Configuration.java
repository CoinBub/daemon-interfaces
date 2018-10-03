package tech.coinbub.daemon.testutils.docker;

import java.net.MalformedURLException;
import java.net.URL;
import tech.coinbub.daemon.normalized.Normalized;

public final class Configuration {
    /**
     * Required. The Docker image to spin up. For example, `coinbub/bitcoin-daemon`.
     * @return The name of the image to spin up
     */
    private String image;
    /**
     * Optional. The name to assign to the container.
     * @return The name to assign to the container
     */
    private String name;
    /**
     * The port upon which to reach the RPC server.
     * @return The port upon which to reach the RPC server
     */
    private Integer port = -1;
    /**
     * The username for accessing the RPC server.
     * @return The username for accessing the RPC server
     */
    private String user;
    /**
     * The password for accessing the RPC server.
     * @return The password for accessing the RPC server
     */
    private String pass;
    /**
     * Optional. The custom command to execute inside the container, rather than the default in the Dockerfile.
     * @return The custom command to execute inside the container
     */
    private String[] cmd;
    /**
     * The path of the RPC server's configuration directory for copying the configuration archive to the container.
     * @return The path of the container's configuration directory
     */
    private String conf;
    /**
     * The raw daemon interface classname.
     * @return The raw daemon interface classname
     */
    private Class rawInterface;
    /**
     * The normalized daemon interface classname
     * @return The normalized daemon interface classname
     */
    private Class<? extends Normalized> normalizedInterface;
    /**
     * Whether or not the docker container should be stopped and reconfigured before each test.
     */
    private boolean reconfigurePerTest;
    /**
     * Whether or not the docker container should be terminated and rebuilt before each test.
     */
    private boolean rebuildPerTest;
    /**
     * Whether or not to leave the docker container in place if a test fails and to halt further testing.
     */
    private boolean persistentOnError;
    /**
     * Whether or not to leave the docker container in place on successful test completion.
     */
    private boolean persistentOnSuccess;
    /**
     * The hostname or IP address at which the container may be reached.
     */
    private String containerHost;
    /**
     * The port at which the container may be reached. Does not necessarily correlate to the port used by the RPC server
     * as the Docker host may choose to forward that port to another port, as indicated here.
     */
    private int containerPort = -1;

    public Configuration(final Dockerize...dockerize) {
        for (Dockerize d : dockerize) {
            extend(d);
        }
    }
    
    public void extend(final Dockerize dockerize) {
        if (dockerize == null) {
            return;
        }

        if (dockerize.image() != null) {
            this.image = dockerize.image();
        }
        if (dockerize.name() != null) {
            this.name = dockerize.name();
        }
        if (dockerize.port() > -1) {
            this.port = dockerize.port();
        }
        if (dockerize.user() != null) {
            this.user = dockerize.user();
        }
        if (dockerize.pass() != null) {
            this.pass = dockerize.pass();
        }
        if (dockerize.cmd() != null && dockerize.cmd().length > 0) {
            this.cmd = dockerize.cmd();
        }
        if (dockerize.conf() != null) {
            this.conf = dockerize.conf();
        }
        if (dockerize.rawInterface() != null) {
            this.rawInterface = dockerize.rawInterface();
        }
        if (dockerize.normalizedInterface() != null) {
            this.normalizedInterface = dockerize.normalizedInterface();
        }
        this.reconfigurePerTest = dockerize.reconfigurePerTest();
        this.rebuildPerTest = dockerize.rebuildPerTest();
        this.persistentOnError = dockerize.persistentOnError();
        this.persistentOnSuccess = dockerize.persistentOnSuccess();
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public Integer getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String[] getCmd() {
        return cmd;
    }

    public String getConf() {
        return conf;
    }

    public Class getRawInterface() {
        return rawInterface;
    }

    public Class<? extends Normalized> getNormalizedInterface() {
        return normalizedInterface;
    }

    public boolean getReconfigurePerTest() {
        return reconfigurePerTest;
    }

    public boolean getRebuildPerTest() {
        return rebuildPerTest;
    }

    public boolean getPersistentOnError() {
        return persistentOnError;
    }

    public boolean getPersistentOnSuccess() {
        return persistentOnSuccess;
    }

    public URL getURL() throws MalformedURLException {
        return new URL("http://" + getContainerHost() + ":" + getContainerPort());
    }

    /**
     * Retrieve a manually-specified URL at which the container may be found.
     * @return 
     */
    public String getContainerLocation() {
        return System.getProperty("containerLocation");
    }
    
    public boolean isDockerizedByIP() {
        return System.getProperty("dockerizedByIP", "false").equals("true");
    }
    
    public String getDockerHost() {
        return System.getProperty("dockerizedHost");
    }
    
    public boolean dockerHostUsesContainerPort() {
        return System.getProperty("dockerizedUseContainerPort", "false").equals("true");
    }

    public String getContainerHost() {
        if (containerHost != null) {
            return containerHost;
        }
        return System.getProperty("containerHost", "localhost");
    }

    public void setContainerHost(final String containerHost) {
        this.containerHost = containerHost;
    }

    public int getContainerPort() {
        if (containerPort > 0) {
            return containerPort;
        }
        return Integer.parseInt(System.getProperty("containerPort", Integer.toString(port)));
    }

    public void setContainerPort(final int containerPort) {
        this.containerPort = containerPort;
    }

}
