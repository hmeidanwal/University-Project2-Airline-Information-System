package io.github.fontysvenlo.ais.persistence;

public class FlightDataAccessException extends RuntimeException {
    public FlightDataAccessException(String message) {
        super(message);
    }

    public FlightDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}