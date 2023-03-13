package de.cleem.tub.tsdbb.apps.worker.adapters;


import de.cleem.tub.tsdbb.commons.exception.BaseException;

public class TSDBAdapterException extends BaseException {


    public TSDBAdapterException(final String message) {

        super(message);

    }

    public TSDBAdapterException(final Exception exception) {

        super(exception);

    }


}
