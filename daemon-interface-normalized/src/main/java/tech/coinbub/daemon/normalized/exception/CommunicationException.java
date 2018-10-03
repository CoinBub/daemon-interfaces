package tech.coinbub.daemon.normalized.exception;

/**
 * The CommunicationException is the ancestor to all normalized communication-related exceptions including
 * socket-closed, empty response, etc.
 */
public class CommunicationException extends DaemonException {
    public CommunicationException(final Throwable t) {
        super(t);
    }
}
