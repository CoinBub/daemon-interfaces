package tech.coinbub.daemon.normalized.exception;

public class VerifyingWalletException extends WalletException {
    public VerifyingWalletException(final Throwable t) {
        super(t);
    }
}
