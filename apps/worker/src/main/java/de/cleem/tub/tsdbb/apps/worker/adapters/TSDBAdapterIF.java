package de.cleem.tub.tsdbb.apps.worker.adapters;

import de.cleem.tub.tsdbb.api.model.WorkerTsdbConnection;
import de.cleem.tub.tsdbb.api.model.Record;

public interface TSDBAdapterIF {

    abstract void setup(final WorkerTsdbConnection config) throws TSDBAdapterException;

    abstract void createStorage() throws TSDBAdapterException;

    abstract void write(final Record record) throws TSDBAdapterException;

    abstract void cleanup() throws TSDBAdapterException;

    abstract void close();

    abstract String getConnectionInfo();

}
