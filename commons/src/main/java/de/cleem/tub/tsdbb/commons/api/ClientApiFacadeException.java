package de.cleem.tub.tsdbb.commons.api;

import de.cleem.tub.tsdbb.commons.exception.BaseException;

public class ClientApiFacadeException extends BaseException {


    public ClientApiFacadeException(final String message) {

        super(message);

    }

    public ClientApiFacadeException(final Exception e) {

        super(e);

    }


}
