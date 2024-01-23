package codes.erbilcan.springboot.idempotency.exception;

public class TooManyRequestsForAnIdempotentEndpoint extends Exception{

    public TooManyRequestsForAnIdempotentEndpoint() {
        super();
    }

    public TooManyRequestsForAnIdempotentEndpoint(String message) {
        super(message);
    }

    public TooManyRequestsForAnIdempotentEndpoint(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyRequestsForAnIdempotentEndpoint(Throwable cause) {
        super(cause);
    }

    protected TooManyRequestsForAnIdempotentEndpoint(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
