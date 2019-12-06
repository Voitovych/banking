package rest.exception;

public class RequestMalformedException extends RuntimeException {

    public RequestMalformedException(final String message) {
        super(message);
    }
}
