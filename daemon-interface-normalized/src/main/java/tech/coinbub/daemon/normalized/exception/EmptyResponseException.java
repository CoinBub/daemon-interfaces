package tech.coinbub.daemon.normalized.exception;

public class EmptyResponseException extends CommunicationException {
    public EmptyResponseException(final Throwable t) {
        super(t);
    }
}
