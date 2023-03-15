package de.cleem.tub.tsdbb.commons.http;

import de.cleem.tub.tsdbb.commons.base.exception.BaseException;

public class HttpException extends BaseException {


    public HttpException(String message) {

        super(message);

    }

    public HttpException(Exception e) {

        super(e);

    }


}
