package de.cleem.bm.tsdb.common.http;

import de.cleem.tub.tsdbb.commons.exception.BaseException;

public class HttpException extends BaseException {


    public HttpException(String message) {

        super(message);

    }

    public HttpException(Exception e) {

        super(e);

    }


}
