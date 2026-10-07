package io.github.fontysvenlo.ais.exceptions;

public class FirstNameException extends IllegalArgumentException {
    public FirstNameException(String message) {
        super(message);
    }



    public FirstNameException(String message, Throwable cause) {
        super(message, cause);
    }



    public FirstNameException(Throwable cause) {
        super(cause);
    }
}
