package de.cleem.bm.tsdb.executor;


import de.cleem.tub.tsdbb.commons.exception.TSDBBException;

public class ExecutionException extends TSDBBException {


    public ExecutionException(String message) {

        super(message);

    }

    public ExecutionException(Exception e) {

        super(e);

    }


}
