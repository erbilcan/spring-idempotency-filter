package codes.erbilcan.springboot.idempotency.exception;

public class CacheNotFoundException extends Exception{

    public CacheNotFoundException() {
        super();
    }

    public CacheNotFoundException(String message) {
        super(message);
    }

    public CacheNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheNotFoundException(Throwable cause) {
        super(cause);
    }

    protected CacheNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
