package rest.controller.dto;

public class ApiError {
    public final String error;

    public ApiError(String error) {
        this.error = error;
    }

    public ApiError(Exception e) {
        this.error = e.getLocalizedMessage();
    }

    public String getError() {
        return error;
    }
}
