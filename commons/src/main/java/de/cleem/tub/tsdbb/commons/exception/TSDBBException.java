package de.cleem.tub.tsdbb.commons.exception;

public class TSDBBException extends Exception {

    public TSDBBException() {

    }

    public TSDBBException(final Throwable cause) {
        super(cause);
    }

    public TSDBBException(final String message) {
        super(message);
    }
}
