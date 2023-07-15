# Time Series Database (TSDB) Benchmark - Worker

[Home README](../../README.md)

[Apps README](../README.md)

Main method class: `de.cleem.tub.tsdbb.apps.worker.WorkerApp`


The generated data as well as the existing workload data will be tested against different TSDB implementations and the
storage performance will be measured.
For this purpose, the benchmark to be developed shall implement client adapters for different TSDBs via a uniform and
extensible adapter concept.
This should enable uniform or identical workload data to be tested against different TSDBs implementations.
The adapter concept provides the following interface operations which have to be implemented partially and depending on
the TSDB:

- Prepare or connect the client adapter before the benchmark `setup`.
- creating the storage target `createStorage` (optional).
- Write a insert and measure the latency `write`.
- Clean up the storage target after running the benchmark `cleanup`.
- Close the connection to the TSDB `close` (optional).

With the execution of the `write` operation first a start timestamp `startTimestamp` with current date and time is set.
Then the insert is written into the memory of the TSDB.
After the write operation the end timestamp `endTimestamp` is set.
The insert written to the TSDB, start and end timestamp and the latency resulting from the timestamps are stored in the
benchmark result.