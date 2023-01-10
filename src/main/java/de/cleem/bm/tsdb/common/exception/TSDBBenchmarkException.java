package de.cleem.bm.tsdb.common.exception;

public class TSDBBenchmarkException extends Exception{

    public TSDBBenchmarkException(){

    }

    public TSDBBenchmarkException(Throwable cause){
        super(cause);
    }

    public TSDBBenchmarkException(String message){
        super(message);
    }
}
