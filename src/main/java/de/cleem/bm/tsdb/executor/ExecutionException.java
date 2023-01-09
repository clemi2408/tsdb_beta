package de.cleem.bm.tsdb.executor;

import de.cleem.bm.tsdb.common.exception.TsdbBenchmarkException;

public class ExecutionException extends TsdbBenchmarkException {


    public ExecutionException(String message){

        super(message);

    }
    public ExecutionException(Exception e){

        super(e);

    }


}
