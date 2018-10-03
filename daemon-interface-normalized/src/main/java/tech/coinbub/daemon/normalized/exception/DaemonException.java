package tech.coinbub.daemon.normalized.exception;

/**
 * The DaemonException is the ancestor of all normalized exceptions that result from daemon-specific errors, such as
 * loading blocks/wallets, missing transactions, etc.
 */
public class DaemonException extends NormalizedException {
    public DaemonException() {
        super();
    }
    public DaemonException(final Throwable t) {
        super(t);
    }
}
