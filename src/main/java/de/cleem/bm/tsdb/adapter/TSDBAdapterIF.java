package de.cleem.bm.tsdb.adapter;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;
import de.cleem.bm.tsdb.model.config.TSDBAdapterConfig;

import java.util.HashMap;

public interface TSDBAdapterIF {

    abstract void setup(final TSDBAdapterConfig config) throws TSDBBenchmarkException;

    abstract void createStorage() throws TSDBBenchmarkException;

    abstract void write(final HashMap<String,Number> record) throws TSDBBenchmarkException;

    abstract void cleanup() throws TSDBBenchmarkException;

    abstract void close();

}
