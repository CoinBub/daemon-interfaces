package tech.coinbub.daemon.testutils.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.googlecode.jsonrpc4j.IJsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.coinbub.daemon.normalized.Normalized;
import tech.coinbub.daemon.proxy.ProxyUtil;
import tech.coinbub.daemon.testutils.Util;
import static tech.coinbub.daemon.testutils.docker.DockerUtils.*;

public class Dockerized implements BeforeAllCallback,
        AfterAllCallback,
        BeforeEachCallback,
        AfterEachCallback,
        ParameterResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Dockerized.class);
    private static final String RAW_INTERFACE_KEY = "raw-interface";
    private static final String NORMALIZED_INTERFACE_KEY = "normalized-interface";
    private static Thread shutdownHook = null;

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        final Configuration config = getConfiguration(context);
        if (!config.getReconfigurePerTest() && !config.getRebuildPerTest()) {
            setup(context);
        }
    }

    @Override
    public void afterAll(final ExtensionContext context) throws Exception {
        final Configuration config = getConfiguration(context);
        if (!config.getReconfigurePerTest() && !config.getRebuildPerTest()) {
            teardown(context);
        }
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final Configuration config = getConfiguration(context);
        if (config.getReconfigurePerTest() || config.getRebuildPerTest()) {
            setup(context);
        }
    }

    @Override
    public void afterEach(final ExtensionContext context) throws InterruptedException {
        final Configuration config = getConfiguration(context);
        if (config.getReconfigurePerTest() || config.getRebuildPerTest()) {
            teardown(context);
        }
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
            final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        if (!isDockerized(extensionContext)) {
            return false;
        }

        final ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL);
        final Configuration config = store.get(Configuration.class, Configuration.class);
        return parameterContext.getParameter().getType().equals(config.getRawInterface())
                || parameterContext.getParameter().getType().equals(config.getNormalizedInterface())
                || parameterContext.getParameter().getType().equals(Normalized.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
            final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        final ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL);
        final Configuration config = store.get(Configuration.class, Configuration.class);
        if (parameterContext.getParameter().getType().equals(config.getRawInterface())) {
            return store.get(RAW_INTERFACE_KEY, config.getRawInterface());
        }
        if (parameterContext.getParameter().getType().equals(config.getNormalizedInterface())
                || parameterContext.getParameter().getType().equals(Normalized.class)) {
            return store.get(NORMALIZED_INTERFACE_KEY, config.getNormalizedInterface());
        }
        return null;
    }

    //
    // Core Helpers
    //
    /**
     * Determines whether or not the current execution context has at least one @Dockerize annotation.
     * @param context The current execution context
     * @return True if dockerized, false otherwise
     */
    private static boolean isDockerized(final ExtensionContext context) {
        return getTestClassDockerizeAnnotation(context) != null
                || getTestMethodDockerizeAnnotation(context) != null;
    }
    private static Dockerize getTestClassDockerizeAnnotation(final ExtensionContext context) {
        final Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isPresent()) {
            return Util.getAnnotation(testClass.get(), Dockerize.class);
        }
        return null;
    }
    private static Dockerize getTestMethodDockerizeAnnotation(final ExtensionContext context) {
        final Optional<Method> testMethod = context.getTestMethod();
        if (testMethod.isPresent()) {
            return Util.getAnnotation(testMethod.get(), Dockerize.class);
        }
        return null;
    }
    private Configuration getConfiguration(final ExtensionContext context) {
        final ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.GLOBAL);
        if (store.get(Configuration.class) == null) {
            final Dockerize dockerizedClass = getTestClassDockerizeAnnotation(context);
            final Dockerize dockerizedMethod = getTestMethodDockerizeAnnotation(context);
            final Configuration config = new Configuration(dockerizedClass, dockerizedMethod);
            store.put(Configuration.class, config);
            return config;
        }
        return store.get(Configuration.class, Configuration.class);
    }

    private void setup(final ExtensionContext context) throws Exception {
        if (!isDockerized(context)) {
            return;
        }

        final Dockerize dockerizedClass = getTestClassDockerizeAnnotation(context);
        final Dockerize dockerizedMethod = getTestMethodDockerizeAnnotation(context);
        final Configuration config = new Configuration(dockerizedClass, dockerizedMethod);
        final ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.GLOBAL);
        final DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        // Determine whether or not we need to run setup
        LOGGER.debug("Setup");
        if (config.getContainerLocation() == null) {
            boolean rebuild = false;
            if (config.getRebuildPerTest()) {
                LOGGER.debug(" > Rebuild per test");
                rebuild = true;
            }
            if (!containerExists(dockerClient, config)) {
                LOGGER.debug(" > Container does not exist");
                rebuild = true;
            }
            if (rebuild) {
                LOGGER.debug(" > Rebuild");
                terminateContainer(dockerClient, config);
                pullImage(dockerClient, config);
                createContainer(dockerClient, config);
            }

            if (rebuild || config.getReconfigurePerTest()) {
                LOGGER.debug(" > Reconfigure");
                stopContainer(dockerClient, config);
                copyConfiguration(dockerClient, config);
            }

            LOGGER.debug(" > Prepare for test");
            startContainer(dockerClient, config);
            getHostPortBinding(dockerClient, config);
            registerShutdownHook(dockerClient, config);
        }


        final URL url = config.getURL();
        LOGGER.info("Using URL {}", url.toString());
        final JsonRpcClient rpcClient = new JsonRpcHttpClient(url, Util.headers(config.getUser(), config.getPass()));
        final Object raw = ProxyUtil.createClientProxy(
                this.getClass().getClassLoader(),
                config.getRawInterface(),
                (IJsonRpcClient) rpcClient);
        final Normalized normalized = (Normalized) config.getNormalizedInterface()
                .getConstructor(config.getRawInterface())
                .newInstance(raw);
        try {
            normalized.waitForDaemon();
        } catch (Exception ex) {
            LOGGER.debug("Container logs:");
            dockerClient.logContainerCmd(config.getName())
                    .withTail(100)
                    .withStdOut(true)
                    .withStdErr(true)
                    .exec(new LogContainerResultCallback() {
                        @Override
                        public void onNext(Frame item) {
                            LOGGER.debug(item.toString());
                        }
                    })
                    .awaitCompletion();
            if (config.getPersistentOnError()) {
                stopContainer(dockerClient, config);
                renameContainer(dockerClient, config, context);
                removeShutdownHook();
            }
            throw new IllegalStateException(ex);
        }

        store.put(Configuration.class, config);
        store.put(DockerClient.class, dockerClient);
        store.put(JsonRpcClient.class, rpcClient);
        store.put(RAW_INTERFACE_KEY, raw);
        store.put(NORMALIZED_INTERFACE_KEY, normalized);
    }

    private void teardown(final ExtensionContext context) throws InterruptedException {
        if (!isDockerized(context)) {
            return;
        }

        final ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.GLOBAL);

        store.remove(Configuration.class);
        store.remove(DockerClient.class);
        store.remove(JsonRpcClient.class);
        store.remove(RAW_INTERFACE_KEY);
        store.remove(NORMALIZED_INTERFACE_KEY);
    }

    private static void removeShutdownHook() {
        if (shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
            shutdownHook = null;
        }
    }

    private static void registerShutdownHook(final DockerClient client, final Configuration config) {
        removeShutdownHook();
        shutdownHook = new Thread(() -> {
            try {
                terminateContainer(client, config);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

}
