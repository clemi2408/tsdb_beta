package de.cleem.bm.tsdb.adapter;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;
import de.cleem.bm.tsdb.model.config.adapter.TSDBAdapterConfig;
import de.cleem.bm.tsdb.model.config.workload.WorkloadRecord;

public interface TSDBAdapterIF {

    abstract void setup(final TSDBAdapterConfig config) throws TSDBBenchmarkException;

    abstract void createStorage() throws TSDBBenchmarkException;

    abstract void write(final WorkloadRecord record) throws TSDBBenchmarkException;

    abstract void cleanup() throws TSDBBenchmarkException;

    abstract void close();

}
