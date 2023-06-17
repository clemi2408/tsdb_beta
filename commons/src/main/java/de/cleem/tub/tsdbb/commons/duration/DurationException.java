package de.cleem.tub.tsdbb.commons.duration;


import de.cleem.tub.tsdbb.commons.base.exception.BaseException;

public class DurationException extends BaseException {


    public DurationException(final Exception e) {

        super(e);

    }

    public DurationException(final String message) {

        super(message);

    }


}
