package de.cleem.bm.tsdb.adapter;

import de.cleem.bm.tsdb.model.config.adapter.TSDBAdapterConfig;
import de.cleem.tub.tsdbb.commons.exception.BaseException;
import de.cleem.tub.tsdbb.commons.model.workload.Record;

public interface TSDBAdapterIF {

    abstract void setup(final TSDBAdapterConfig config) throws BaseException;

    abstract void createStorage() throws BaseException;

    abstract void write(final Record record) throws BaseException;

    abstract void cleanup() throws BaseException;

    abstract void close();

}
