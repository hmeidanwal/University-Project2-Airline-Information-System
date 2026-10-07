package io.github.fontysvenlo.ais.persistence.api;

public class SeatNotAvailableException extends Exception {
    public SeatNotAvailableException(String message) {
        super(message);
    }

    public SeatNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
} 