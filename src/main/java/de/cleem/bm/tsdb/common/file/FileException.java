package de.cleem.bm.tsdb.common.file;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;

import java.io.IOException;

public class FileException extends TSDBBenchmarkException {


    public FileException(String message){

        super(message);

    }
    public FileException(IOException e){

        super(e);

    }


}
