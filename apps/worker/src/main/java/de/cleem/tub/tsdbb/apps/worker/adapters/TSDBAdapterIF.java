package de.cleem.tub.tsdbb.apps.worker.adapters;

import de.cleem.tub.tsdbb.api.model.Insert;
import de.cleem.tub.tsdbb.api.model.WorkerSetupRequest;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbEndpoint;


public interface TSDBAdapterIF {

    void setup(final WorkerSetupRequest workerSetupRequest) throws TSDBAdapterException;

    void createStorage(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    int write(final Insert insert, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    void cleanup(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    void close();

}
