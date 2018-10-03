package tech.coinbub.daemon.normalized.exception;

public class InvalidTransactionIdException extends TransactionException {
    public InvalidTransactionIdException(final Throwable t) {
        super(t);
    }
}
