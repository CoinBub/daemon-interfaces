package tech.coinbub.daemon.normalized.exception;

public class LoadingBlockIndexException extends BlockException {
    public LoadingBlockIndexException(final Throwable t) {
        super(t);
    }
}
