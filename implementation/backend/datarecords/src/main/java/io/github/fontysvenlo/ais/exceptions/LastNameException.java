package io.github.fontysvenlo.ais.exceptions;

public class LastNameException extends IllegalArgumentException {
    public LastNameException(String message) {
        super(message);
    }

    public LastNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public LastNameException(Throwable cause) {
        super(cause);
    }
}