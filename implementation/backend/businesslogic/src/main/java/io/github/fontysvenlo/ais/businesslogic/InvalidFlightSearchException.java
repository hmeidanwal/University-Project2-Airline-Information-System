package io.github.fontysvenlo.ais.businesslogic;

public class InvalidFlightSearchException extends IllegalArgumentException  {
    public InvalidFlightSearchException(String message) {
        super(message);
    }
}