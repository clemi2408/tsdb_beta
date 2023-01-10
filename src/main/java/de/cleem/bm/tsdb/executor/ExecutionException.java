package de.cleem.bm.tsdb.executor;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;

public class ExecutionException extends TSDBBenchmarkException {


    public ExecutionException(String message){

        super(message);

    }
    public ExecutionException(Exception e){

        super(e);

    }


}
