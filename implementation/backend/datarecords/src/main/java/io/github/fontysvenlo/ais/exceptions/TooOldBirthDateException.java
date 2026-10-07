package io.github.fontysvenlo.ais.exceptions;

public class TooOldBirthDateException extends DateOfBirthException {
    public TooOldBirthDateException(String message) {
        super(message);
    }
    
    public TooOldBirthDateException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public TooOldBirthDateException(Throwable cause) {
        super(cause);
    }
}