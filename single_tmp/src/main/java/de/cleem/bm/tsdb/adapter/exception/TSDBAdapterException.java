package de.cleem.bm.tsdb.adapter.exception;


import de.cleem.tub.tsdbb.commons.exception.TSDBBException;

public class TSDBAdapterException extends TSDBBException {


    public TSDBAdapterException(String message) {

        super(message);

    }

    public TSDBAdapterException(Exception e) {

        super(e);

    }


}
