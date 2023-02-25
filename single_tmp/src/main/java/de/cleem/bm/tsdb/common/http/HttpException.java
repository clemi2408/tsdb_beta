package de.cleem.bm.tsdb.common.http;

import de.cleem.tub.tsdbb.commons.exception.TSDBBException;

public class HttpException extends TSDBBException {


    public HttpException(String message) {

        super(message);

    }

    public HttpException(Exception e) {

        super(e);

    }


}
