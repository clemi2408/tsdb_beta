package de.cleem.tub.tsdbb.apps.worker.adapters;

import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbConnection;

public interface TSDBAdapterIF {

    void setup(final WorkerTsdbConnection config) throws TSDBAdapterException;

    void createStorage() throws TSDBAdapterException;

    int write(final Record record) throws TSDBAdapterException;

    void cleanup() throws TSDBAdapterException;

    void close();

    String getConnectionInfo();

}
