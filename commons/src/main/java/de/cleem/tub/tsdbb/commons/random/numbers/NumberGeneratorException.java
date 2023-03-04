package de.cleem.tub.tsdbb.commons.random.numbers;


import de.cleem.tub.tsdbb.commons.exception.BaseException;

import java.io.IOException;

public class NumberGeneratorException extends BaseException {
    public NumberGeneratorException(final String message) {

        super(message);

    }

    public NumberGeneratorException(final IOException e) {

        super(e);

    }


}
