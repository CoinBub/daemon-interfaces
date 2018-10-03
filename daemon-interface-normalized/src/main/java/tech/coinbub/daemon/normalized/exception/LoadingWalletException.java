package tech.coinbub.daemon.normalized.exception;

public class LoadingWalletException extends WalletException {
    public LoadingWalletException(final Throwable t) {
        super(t);
    }
}
