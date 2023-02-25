package de.cleem.tub.tsdbb.commons.file;

import de.cleem.tub.tsdbb.commons.exception.TSDBBException;

import java.io.IOException;

public class FileException extends TSDBBException {


    public FileException(final String message) {

        super(message);

    }

    public FileException(final IOException e) {

        super(e);

    }


}
