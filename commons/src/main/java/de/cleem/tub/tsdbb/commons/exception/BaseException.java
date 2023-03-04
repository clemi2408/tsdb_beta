package de.cleem.tub.tsdbb.commons.exception;

public class BaseException extends Exception {

    public BaseException() {

    }

    public BaseException(final Throwable cause) {
        super(cause);
    }

    public BaseException(final String message) {
        super(message);
    }
}
