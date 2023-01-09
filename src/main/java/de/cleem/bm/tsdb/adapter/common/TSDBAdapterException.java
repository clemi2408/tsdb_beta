package de.cleem.bm.tsdb.adapter.common;

import de.cleem.bm.tsdb.common.exception.TsdbBenchmarkException;

public class TSDBAdapterException extends TsdbBenchmarkException {


    public TSDBAdapterException(String message){

        super(message);

    }
    public TSDBAdapterException(Exception e){

        super(e);

    }


}
