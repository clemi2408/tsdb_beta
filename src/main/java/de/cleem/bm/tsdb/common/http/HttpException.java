package de.cleem.bm.tsdb.common.http;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;

public class HttpException extends TSDBBenchmarkException {


    public HttpException(String message){

        super(message);

    }
    public HttpException(Exception e){

        super(e);

    }


}
