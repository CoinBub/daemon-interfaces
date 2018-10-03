package tech.coinbub.daemon.normalized.exception;

public class VerifyingBlocksException extends BlockException {
    public VerifyingBlocksException(final Throwable t) {
        super(t);
    }
}
