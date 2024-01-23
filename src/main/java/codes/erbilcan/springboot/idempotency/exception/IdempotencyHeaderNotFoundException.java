package codes.erbilcan.springboot.idempotency.exception;

public class IdempotencyHeaderNotFoundException extends Exception {

    public IdempotencyHeaderNotFoundException() {
        super();
    }

    public IdempotencyHeaderNotFoundException(String message) {
        super(message);
    }

    public IdempotencyHeaderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdempotencyHeaderNotFoundException(Throwable cause) {
        super(cause);
    }

    protected IdempotencyHeaderNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
