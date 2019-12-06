package rest.exception;

public class RequestValidationException extends RuntimeException {

    public RequestValidationException(final String message) {
        super(message);
    }
}
