package de.cleem.tub.tsdbb.apps.worker.adapters;

import de.cleem.tub.tsdbb.api.model.Insert;
import de.cleem.tub.tsdbb.api.model.Select;
import de.cleem.tub.tsdbb.api.model.WorkerSetupRequest;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbEndpoint;


public interface TSDBAdapterIF {

    boolean setup(final WorkerSetupRequest workerSetupRequest) throws TSDBAdapterException;

    boolean createStorage(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    InsertResponse write(final Insert insert, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse read(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    boolean cleanup(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    boolean close();

    boolean healthCheck(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    ///
    SelectResponse getAllLabels(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse getSingleLabelValue(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse getMeasurementLabels(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse countSeries(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse getAllSeries(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse getMeasurementSeries(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse exportSeries(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse getFieldValue(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse getFieldValueSum(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse getFieldValueAvg(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse getFieldValueMin(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

    SelectResponse getFieldValueMax(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException;

}
