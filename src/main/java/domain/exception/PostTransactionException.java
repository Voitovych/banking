package domain.exception;

public class PostTransactionException extends RuntimeException {

    public PostTransactionException(final String message) {
        super(message);
    }
}
