package tech.coinbub.daemon.normalized;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.coinbub.daemon.normalized.exception.BlockNotFoundException;
import tech.coinbub.daemon.normalized.exception.CommunicationException;
import tech.coinbub.daemon.normalized.exception.EmptyResponseException;
import tech.coinbub.daemon.normalized.exception.InvalidTransactionIdException;
import tech.coinbub.daemon.normalized.exception.LoadingBlockIndexException;
import tech.coinbub.daemon.normalized.exception.LoadingWalletException;
import tech.coinbub.daemon.normalized.exception.NormalizedException;
import tech.coinbub.daemon.normalized.exception.RewindingBlocksException;
import tech.coinbub.daemon.normalized.exception.ServerStartingException;
import tech.coinbub.daemon.normalized.exception.VerifyingBlocksException;
import tech.coinbub.daemon.normalized.exception.VerifyingWalletException;
import tech.coinbub.daemon.normalized.model.Block;
import tech.coinbub.daemon.normalized.model.Info;
import tech.coinbub.daemon.normalized.model.Transaction;

public interface Normalized<T> {
    public static final Logger LOGGER = LoggerFactory.getLogger(Normalized.class);
    public static final long WAIT_INTERVAL = 250L;
    public static final long WAIT_TIMEOUT = 10000L;

    String getSymbol();
    Info getinfo();
    String getbestblockhash();
    String getblockhash(final Long height);
    Block getblock(final String hash);
    Transaction gettransaction(final String txid);
    String getnewaddress();
    String sendtoaddress(final String address, final BigDecimal amount);
    String sendtoaddress(final String address, final BigDecimal amount, final String comment_from);
    String sendtoaddress(final String address, final BigDecimal amount, final String comment_from, final String comment_to);

    public default RuntimeException normalizeException(final RuntimeException ex) {
        if (ex == null || ex.getMessage() == null) {
            return ex;
        }

        // Block-related exceptions
        if (ex.getMessage().startsWith("Loading block index")) {
            return new LoadingBlockIndexException(ex);
        }
        if (ex.getMessage().startsWith("Verifying blocks")) {
            return new VerifyingBlocksException(ex);
        }
        if (ex.getMessage().startsWith("Rewinding blocks")) {
            return new RewindingBlocksException(ex);
        }
        if (ex.getMessage().startsWith("Block not found")) {
            return new BlockNotFoundException(ex);
        }

        // Transaction-related exceptions
        if (ex.getMessage().startsWith("Invalid or non-wallet transaction id")) {
            return new InvalidTransactionIdException(ex);
        }

        // Wallet-related exceptions
        if (ex.getMessage().startsWith("Loading wallet")) {
            return new LoadingWalletException(ex);
        }
        if (ex.getMessage().startsWith("Verifying wallet")) {
            return new VerifyingWalletException(ex);
        }

        // General communication exceptions
        if (ex.getMessage().startsWith("RPC server started")) {
            return new ServerStartingException(ex);
        }
        if (ex.getMessage().startsWith("Caught error with no response body")) {
            return new EmptyResponseException(ex);
        }
        if (ex.getCause() instanceof ConnectException) {
            return new CommunicationException(ex);
        }
        return ex;
    }
    public default <T> T normalizeException(final Supplier<T> func) {
        try {
            return func.get();
        } catch (RuntimeException ex) {
            throw normalizeException(ex);
        }
    }
    
    public static <C> Class<C> getRawType(final Class<? extends Normalized<C>> normalizedClass) {
        final ParameterizedType parameterizedType = (ParameterizedType) normalizedClass.getGenericInterfaces()[0];
        final Type[] typeArguments = parameterizedType.getActualTypeArguments();
        return (Class<C>) typeArguments[0];
    }

    /**
     * Wait for the daemon to respond. Waits for {@link Normalized.WAIT_INTERVAL} milliseconds between attempts, and a
     * total of {@link Normalized.WAIT_TIMEOUT} milliseconds before timing out.
     * @throws InterruptedException 
     */
    public default void waitForDaemon() throws InterruptedException {
        waitForDaemon(WAIT_INTERVAL, WAIT_TIMEOUT);
    }
    public default void waitForDaemon(final long interval, final long timeout) throws InterruptedException {
        final long starttime = System.currentTimeMillis();
        NormalizedException last = null;
        do {
            try {
                if (normalizeException(this::getbestblockhash) != null) {
                    return;
                }
            } catch (NormalizedException ex) {
                last = ex;
                Thread.sleep(interval);
            } catch (Exception ex) {
                throw ex;
            }
        } while (System.currentTimeMillis() - starttime < timeout);
        if (last != null) {
            throw last;
        }
    }

}
