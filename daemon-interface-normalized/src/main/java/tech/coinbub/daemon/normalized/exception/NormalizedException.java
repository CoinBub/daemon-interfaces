package tech.coinbub.daemon.normalized.exception;

/**
 * The NormalizedException is the ancestor of all exceptions that can be produced from the `normalizeException` method
 * of the `Normalized` interface, save the original exception itself.
 */
public class NormalizedException extends RuntimeException {
    public NormalizedException() {
        super();
    }
    public NormalizedException(final Throwable t) {
        super(t);
    }
}
