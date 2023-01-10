package de.cleem.bm.tsdb.adapter.exception;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;

public class TSDBAdapterException extends TSDBBenchmarkException {


    public TSDBAdapterException(String message){

        super(message);

    }
    public TSDBAdapterException(Exception e){

        super(e);

    }


}
