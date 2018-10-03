package tech.coinbub.daemon.normalized.exception;

public class RewindingBlocksException extends BlockException {
    public RewindingBlocksException(final Throwable t) {
        super(t);
    }
}
