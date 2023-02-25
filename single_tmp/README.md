

Both the already generated workload and the generator configuration are part of the benchmark configuration, along with
the connection information for the specific TSDBs and the number of client threads used.
The general benchmark configuration contains:

- Workload generation configuration `generatorConfig`.
- Workload `workload`.
- Number of client threads `threadCount`.
- Boolean value to enable cleanup functionality after benchmark `cleanStorage`






The generated data as well as the existing workload data will be tested against different TSDB implementations and the
storage performance will be measured.
For this purpose, the benchmark to be developed shall implement client adapters for different TSDBs via a uniform and
extensible adapter concept.
This should enable uniform or identical workload data to be tested against different TSDBs implementations.
The adapter concept provides the following interface operations which have to be implemented partially and depending on
the TSDB:

- Prepare or connect the client adapter before the benchmark `setup`.
- creating the storage target `createStorage` (optional).
- Write a record and measure the latency `write`.
- Clean up the storage target after running the benchmark `cleanup`.
- Close the connection to the TSDB `close` (optional).

With the execution of the `write` operation first a start timestamp `startTimestamp` with current date and time is set.
Then the record is written into the memory of the TSDB.
After the write operation the end timestamp `endTimestamp` is set.
The record written to the TSDB, start and end timestamp and the latency resulting from the timestamps are stored in the
benchmark result.

