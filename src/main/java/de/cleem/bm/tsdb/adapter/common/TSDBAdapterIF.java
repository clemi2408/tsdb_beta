package de.cleem.bm.tsdb.adapter.common;

import java.util.HashMap;

public interface TSDBAdapterIF {

    abstract void setup(final TSDBAdapterConfig config) throws TSDBAdapterException;

    abstract void createStorage() throws TSDBAdapterException;

    abstract void close();

    abstract void cleanup() throws TSDBAdapterException;

    abstract void write(final HashMap<String,Number> record) throws TSDBAdapterException;

}
