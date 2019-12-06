package domain.exception;

public class CreateAccountException extends RuntimeException {

    public CreateAccountException(final String message) {
        super(message);
    }
}
