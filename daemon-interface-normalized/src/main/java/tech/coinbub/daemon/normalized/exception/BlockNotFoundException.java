package tech.coinbub.daemon.normalized.exception;

public class BlockNotFoundException extends BlockException {
    public BlockNotFoundException() {
        super();
    }
    public BlockNotFoundException(final Throwable t) {
        super(t);
    }
}
