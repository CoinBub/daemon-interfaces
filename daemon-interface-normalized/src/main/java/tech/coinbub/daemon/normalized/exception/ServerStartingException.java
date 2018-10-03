package tech.coinbub.daemon.normalized.exception;

public class ServerStartingException extends CommunicationException {
    public ServerStartingException(final Throwable t) {
        super(t);
    }
}
