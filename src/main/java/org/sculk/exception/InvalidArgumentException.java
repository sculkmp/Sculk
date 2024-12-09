package org.sculk.exception;

public class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String message) {
        super(message);
    }

    public InvalidArgumentException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

