package tech.coinbub.daemon.normalized.exception;

/**
 * The TransactionException is the ancestor to all normalized transaction-related exceptions, including missing
 * transactions or invalid transaction ids.
 */
public class TransactionException extends DaemonException {
    public TransactionException(final Throwable t) {
        super(t);
    }
}
