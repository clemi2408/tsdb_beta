package de.cleem.bm.tsdb.common.json;

import de.cleem.bm.tsdb.common.exception.TsdbBenchmarkException;

public class JsonException extends TsdbBenchmarkException {


    public JsonException(String message){

        super(message);

    }
    public JsonException(Exception e){

        super(e);

    }


}
