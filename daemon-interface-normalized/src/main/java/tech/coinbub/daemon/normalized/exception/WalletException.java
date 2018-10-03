package tech.coinbub.daemon.normalized.exception;

/**
 * The WalletException is the ancestor to all normalized wallet-related exceptions, including loading and verification.
 */
public class WalletException extends DaemonException {
    public WalletException(final Throwable t) {
        super(t);
    }
}
