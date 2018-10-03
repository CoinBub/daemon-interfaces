package tech.coinbub.daemon.testutils.docker;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.exception.BadRequestException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.NetworkSettings;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.command.PullImageResultCallback;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DockerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerUtils.class);

    private DockerUtils() {}

    public static boolean containerExists(final DockerClient client, final Configuration config) {
        try {
            client.inspectContainerCmd(config.getName()).exec();
            return true;
        } catch (NotFoundException ex) {
            return false;
        }
    }

    public static void terminateContainer(final DockerClient client, final Configuration config) throws InterruptedException {
        if (!containerExists(client, config)) {
            return;
        }

        LOGGER.info("Destroying container {}", config.getName());
        try {
            client.removeContainerCmd(config.getName()).withForce(true).exec();
        } catch (BadRequestException ex) {
            if (!ex.getMessage().contains("removal of container")) {
                throw ex;
            }
        }

        // Wait until the container is gone
        final long starttime = System.currentTimeMillis();
        boolean success = false;
        do {
            if (!containerExists(client, config)) {
                success = true;
                break;
            }
            Thread.sleep(250L);
        } while (System.currentTimeMillis() - starttime < 10000L);
        if (!success) {
            throw new IllegalStateException("Unable to destroy container");
        }
    }

    public static void stopContainer(final DockerClient client, final Configuration config) throws InterruptedException {
        if (!containerExists(client, config)) {
            return;
        }

        LOGGER.info("Stopping container {}", config.getName());
        try {
            client.stopContainerCmd(config.getName()).exec();
        } catch (NotModifiedException ex) {}
    }

    /**
     * Pulls down the requested image if it doesn't already exist locally.
     *
     * @throws InterruptedException
     */
    public static void pullImage(final DockerClient client, final Configuration config) throws InterruptedException {
        final List<Image> img = client.listImagesCmd()
                .withImageNameFilter(config.getImage())
                .exec();
        if (img.isEmpty()) {
            client.pullImageCmd(config.getImage())
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();
        }
    }
    /**
     * Create the container if it doesn't already exist
     */
    public static void createContainer(final DockerClient client, final Configuration config) {
        if (containerExists(client, config)) {
            LOGGER.info("Container {}  already exists", config.getName());
            return;
        }

        final CreateContainerCmd result = client.createContainerCmd(config.getImage())
                .withStdInOnce(false)
                .withStdinOpen(false)
                .withPortSpecs(config.getPort().toString())
                .withExposedPorts(ExposedPort.tcp(config.getPort()))
                .withPortBindings(new PortBinding(Ports.Binding.bindIp("0.0.0.0"), ExposedPort.tcp(config.getPort())))
                .withName(config.getName());
        if (config.getCmd() != null) {
            result.withCmd(config.getCmd());
        }
        final String containerId = result.exec()
                .getId();
        LOGGER.info("Built container {}", containerId);
    }

    public static void copyConfiguration(final DockerClient client, final Configuration config) throws IOException, InterruptedException {
        try (InputStream stream = DockerUtils.class.getResourceAsStream("/conf.tar.gz")) {
            if (stream == null) {
                LOGGER.warn("Could not retrieve conf.tar.gz. Ensure it exists in src/test/resources");
                return;
            }
            LOGGER.debug("Copy conf.tar.gz to {}", config.getConf());
            client.copyArchiveToContainerCmd(config.getName())
                    .withTarInputStream(stream)
                    .withRemotePath(config.getConf())
                    .exec();
            Thread.sleep(250L);
        }
    }

    public static void startContainer(final DockerClient client, final Configuration config) {
        try {
            client.startContainerCmd(config.getName())
                    .exec();
            LOGGER.info("Started container {}", config.getName());
        } catch (NotModifiedException ex) {
            LOGGER.info("Container {} already running", config.getName());
        }
    }

    public static void getHostPortBinding(final DockerClient client, final Configuration config) throws IOException {
        final NetworkSettings network = client.inspectContainerCmd(config.getName())
                .exec()
                .getNetworkSettings();

        // Grab the host of the docker container
        if (config.isDockerizedByIP()) {
            LOGGER.debug("Connecting to container by IP");
            for (Map.Entry<String, ContainerNetwork> net : network.getNetworks().entrySet()) {
                LOGGER.debug("Network {} IP {}", net.getValue().getNetworkID(), net.getValue().getIpAddress());
                config.setContainerHost(net.getValue().getIpAddress());
            }
            LOGGER.info("Using host {}", config.getContainerHost());
        } else if (config.getDockerHost() != null) {
            String urlStr = config.getDockerHost();
            if (urlStr.contains("://")) {
                urlStr = urlStr.split("://")[1];
            }
            final URL netUrl = new URL("http://" + urlStr);
            config.setContainerHost(netUrl.getHost());
        }

        if (config.dockerHostUsesContainerPort()) {
            config.setContainerPort(config.getPort());
            LOGGER.info("Using container port {}", config.getContainerPort());
            return;
        }

        // Otherwise, grab the port bound to the exposed docker port
        final Map<ExposedPort, Ports.Binding[]> bindings = network.getPorts()
                .getBindings();
        for (Map.Entry<ExposedPort, Ports.Binding[]> port : bindings.entrySet()) {
            if (port.getKey().getPort() == config.getPort()) {
                if (port.getValue() == null || port.getValue().length != 1) {
                    throw new RuntimeException("Found " + port.getValue().length + " bound ports. Expected 1");
                }
                config.setContainerPort(Integer.parseInt(port.getValue()[0].getHostPortSpec()));
            }
        }

        if (config.getContainerPort() < 0) {
            throw new RuntimeException("RPC port " + config.getPort() + " not bound to host");
        }
        LOGGER.info("RPC port {} bound to {}", config.getPort(), config.getContainerPort());

    }

    public static void renameContainer(final DockerClient client, final Configuration config, final ExtensionContext ctx) {
        final String name = ctx.getRequiredTestClass().getCanonicalName() + "-" + ctx.getRequiredTestMethod().getName();
        LOGGER.debug("Renaming container {} to {}", config.getName(), name);
        client.renameContainerCmd(config.getName())
                .withName(name)
                .exec();
    }
}
