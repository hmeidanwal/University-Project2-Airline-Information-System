package io.github.fontysvenlo.ais.exceptions;

public class DateOfBirthException extends IllegalArgumentException {
    public DateOfBirthException(String message) {
        super(message);
    }

    public DateOfBirthException(String message, Throwable cause) {
        super(message, cause);
    }

    public DateOfBirthException(Throwable cause) {
        super(cause);
    }
}