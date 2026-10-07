package io.github.fontysvenlo.ais.exceptions;

public class RetrievalFailedException extends IllegalStateException{
    public RetrievalFailedException(String message) {
        super(message);
    }

    public RetrievalFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetrievalFailedException(Throwable cause) {
        super(cause);
    }
  
}
