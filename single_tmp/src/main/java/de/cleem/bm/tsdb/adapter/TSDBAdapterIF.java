package de.cleem.bm.tsdb.adapter;

import de.cleem.bm.tsdb.model.config.adapter.TSDBAdapterConfig;
import de.cleem.tub.tsdbb.commons.exception.TSDBBException;
import de.cleem.tub.tsdbb.commons.model.workload.Record;

public interface TSDBAdapterIF {

    abstract void setup(final TSDBAdapterConfig config) throws TSDBBException;

    abstract void createStorage() throws TSDBBException;

    abstract void write(final Record record) throws TSDBBException;

    abstract void cleanup() throws TSDBBException;

    abstract void close();

}
