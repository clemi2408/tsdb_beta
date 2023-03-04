package de.cleem.tub.tsdbb.commons.file;

import de.cleem.tub.tsdbb.commons.exception.BaseException;

import java.io.IOException;

public class FileException extends BaseException {


    public FileException(final String message) {

        super(message);

    }

    public FileException(final IOException e) {

        super(e);

    }


}
