package io.github.fontysvenlo.ais.exceptions;

public class DeletionFailedException extends PersistenceException{
    public DeletionFailedException(String message) {
        super(message);
    }

    public DeletionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeletionFailedException(Throwable cause) {
        super(cause);
    }
  
}
