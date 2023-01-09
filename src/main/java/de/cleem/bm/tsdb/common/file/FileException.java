package de.cleem.bm.tsdb.common.file;

import de.cleem.bm.tsdb.common.exception.TsdbBenchmarkException;

import java.io.IOException;

public class FileException extends TsdbBenchmarkException {


    public FileException(String message){

        super(message);

    }
    public FileException(IOException e){

        super(e);

    }


}
