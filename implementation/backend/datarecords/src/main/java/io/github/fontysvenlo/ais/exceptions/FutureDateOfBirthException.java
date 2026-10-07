package io.github.fontysvenlo.ais.exceptions;

public class FutureDateOfBirthException extends DateOfBirthException {
    public FutureDateOfBirthException(String message) {
        super(message);
    }
    
    public FutureDateOfBirthException(String message, Throwable cause) {
        super(message, cause);
    }

    
    public FutureDateOfBirthException(Throwable cause) {
        super(cause);
    } 
}

