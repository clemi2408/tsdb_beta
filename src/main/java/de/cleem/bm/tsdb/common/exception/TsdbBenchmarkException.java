package de.cleem.bm.tsdb.common.exception;

public class TsdbBenchmarkException extends Exception{

    public TsdbBenchmarkException(){

    }

    public TsdbBenchmarkException(Throwable cause){
        super(cause);
    }

    public TsdbBenchmarkException(String message){
        super(message);
    }
}
