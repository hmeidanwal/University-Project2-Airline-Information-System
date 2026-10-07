package io.github.fontysvenlo.ais.exceptions;

public class PhoneException extends IllegalArgumentException {
    public PhoneException(String message) {
        super(message);
    }

    public PhoneException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhoneException(Throwable cause) {
        super(cause);
    }
}