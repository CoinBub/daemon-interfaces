package tech.coinbub.daemon.normalized.exception;

/**
 * The BlockException is the ancestor of all normalized block-related exceptions, including loading or missing blocks,
 * verification, etc.
 */
public class BlockException extends DaemonException {
    public BlockException() {
        super();
    }
    public BlockException(final Throwable t) {
        super(t);
    }
}
