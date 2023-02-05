# Time Series Database (TSDB) Benchmark Beta

## Design

The following text describes a benchmark design form TSDBs.
The scenario of the experiment and the qualities to be measured are described.
On the one hand, general design objectives are explained.
On the other hand, new design objectives for cloud service benchmarks are described.

### Scenario

There are many possible reasons for collecting and storing data in a temporal context.
Examples are measured values and events in scientific experiments but also status data of software applications in operation.
TSDBs support the storage of data in a time context.
They also offer additional query and analysis options for the stored data.

> A time series database (TSDB) is a database optimized for time-stamped or time series data. 
> Time series data are simply measurements or events that are tracked, monitored, downsampled, and aggregated over time. 
> This could be server metrics, application performance monitoring, network data, sensor data, events, clicks, trades in a market, and many other types of analytics data.
> 
> _Source: [https://www.influxdata.com/time-series-database/ 09.01.2023](https://www.influxdata.com/time-series-database/)_

There is a large number of different TSDB implementations that allow key-value pairs to be stored in the form of a time series in such a way that they can be queried.
Besides the query, the storage of data is of central importance.
This benchmark design - as a basis for a subsequent implementation - focuses on the storage of time series.

The benchmark implementation resulting from the design is to compare the quality performance when writing data to different TSDBs.
On the one hand, it should be possible to generate random workload data via a workload generator configuration.
On the other hand, it should be possible to use existing workload data generated in earlier benchmark runs.

Both the already generated workload and the generator configuration are part of the benchmark configuration, along with the connection information for the specific TSDBs and the number of client threads used.
The general benchmark configuration contains:

- Workload generation configuration `workloadGeneratorConfig`.
- Workload `workload`.
- Number of client threads `threadCount`.
- Boolean value to enable cleanup functionality after benchmark `cleanStorage`

For repeatability of the benchmark, the Benchmark Configuration holding the configuration of the workload generator and the generated workload is persisted.
The workload generator has the following configuration parameters:

- Number of records `recordCount`.
- List of `RecordConfig` entries `recordConfigList`. 

With the definition of `RecordConfig` elements the record to be generated can be described.
A record can consist of several KV pairs.
Each `RecordConfig` element describes a KV pair of the record to be generated.
With respect to the key, the following key strategies are possible:

- Key strategy A: Assignment of a predefined key.
- Key strategy B: Generation of a key by defining a minimum and maximum length.

The `RecordConfig` has the following key-specific parameters:

- Default value of the key `keyValue` (key strategy A).
- Minimum length of the key of the KV pair `minKeyLength` (key strategy B).
- Maximum length of the key of the KV pair `maxKeyLength` (key strategy B).

With respect to the generated value, the following generation strategies are possible `valueDistribution`:

- Value Strategy A: Generation of the value with normal distribution `uniform`.
- Value Strategy B: Generation of the value with Gaussian distribution `gaussian`.
- Value Strategy C: Generation of the value with triangular distribution `triangle`.

Depending on the selected generation strategy, the following configuration parameters are required:

- Minimum value `minValue` (value strategy A and C)
- Maximum value `minValue` (value strategy A and C)
- Value spike of the distribution `triangleSpike` (value strategy C)
- Middle of the distribution `gaussMiddle` (value strategy B)
- Range of the distribution `gaussRange` (value strategy B)

Independent of the generation strategy it is possible to generate the following data types `valueType`:

- double-precision 64-bit IEEE 754 floating point `java.lang.Double`.
- 32-bit signed two's complement integer `java.lang.Integer`.

Example `RecordConfig` with generated key and integer value to be generated with the value strategy `uniform`:

```
{
  "minKeyLength" : 2,
  "maxKeyLength" : 5,
  "valueDistribution" : "uniform",
  "minValue" : 100,
  "maxValue" : 1000,
  "valueType" : "java.lang.Integer"
}
```

Example `RecordConfig` with defined key and double value to be generated with the value strategy `gaussian`:

```
{
  "keyValue" : "gaussianDouble",
  "valueDistribution" : "gaussian",
  "valueType" : "java.lang.Double",
  "gaussMiddle" : 10,
  "gaussRange" : 30
}
```

Example `RecordConfig` with defined key and integer value to be generated with the value strategy `triangle`:

```
{
  "keyValue" : "triangleInteger",
  "valueDistribution" : "triangle",
  "minValue" : 0,
  "maxValue" : 100,
  "valueType" : "java.lang.Integer",
  "triangleSpike" : 50
}
```

The generated data as well as the existing workload data will be tested against different TSDB implementations and the storage performance will be measured.
For this purpose, the benchmark to be developed shall implement client adapters for different TSDBs via a uniform and extensible adapter concept.
This should enable uniform or identical workload data to be tested against different TSDBs implementations.
The adapter concept provides the following interface operations which have to be implemented partially and depending on the TSDB:

- Prepare or connect the client adapter before the benchmark `setup`.
- creating the storage target `createStorage` (optional).
- Write a record and measure the latency `write`.
- Clean up the storage target after running the benchmark `cleanup`.
- Close the connection to the TSDB `close` (optional).

With the execution of the `write` operation first a start timestamp `startTimestamp` with current date and time is set.
Then the record is written into the memory of the TSDB.
After the write operation the end timestamp `endTimestamp` is set.
The record written to the TSDB, start and end timestamp and the latency resulting from the timestamps are stored in the benchmark result.

#### Qualities

The benchmark to be implemented measures as quality the performance of different TSDBs.
The performance of the write operations of different TSDB implementations is measured via the latency in milliseconds (ms).
Identical data sets are sent to different TSDBs for storage via the benchmark application to be implemented.
In addition to the raw measurement data, the quality performance of the different TSDBs should become comparable as follows:

- Minimum latency.
- Maximum latency.
- Median Latency.
- Different Percentile Values.
- Visualization of the measurement results in discrete curves / sliding window aggregation.

### General Design Objectives

For the effectiveness of the benchmark to be implemented, the following section describes the general design objectives.

#### Relevance

The benchmark to be implemented is to measure the write performance of TSDB storage engine implementations.
For this purpose KV pairs are sent to the TSDB.
With the configurable thread count `threadCount` it is possible to stress the subject under test.
With the `write` operation a realistic TSDB request is executed and reflects the scenario domain.

#### Portability

The benchmark is implemented in Java to ensure compatibility with common hardware and software systems.
The workload generator configuration, generated workloads and benchmark results are stored in JSON.
With the adapter concept, the benchmark can be run against a variety of TSDBs implementations.
The benchmark to be implemented focuses on the general `write` operation.
No specific functionalities of individual TSDB systems are tested.

#### Reproducibility or repeatability

The repetition of the experiment is enabled by the benchmark configuration.
It is possible to reuse a workload or workload generator configuration in an experiment.
The repetition increases the probability of reproducing the same or similar results.

#### Fairness

All TSDB implementations to be tested are to be treated fairly, implicit assumptions are to be avoided.
As already described in Portability, a general functionality of TSDBs is used with the `write` operation.

#### Understandability

The latency of the `write` operation of the quality performance is easy to understand.
The duration in ms, is understandable and verifiable.
The metrics derived from latency have understandable meaning.
Generated KV pair workloads or the generator configuration to generate KV pairs is easy to understand.


### New Design Objectives in Cloud Service Benchmarking (DRAFT)

The adapter concept of the benchmark to be implemented allows a fast evolution and adaptation.
It allows a high degree of changeability on infrastructure side.
In the best case, infrastructure changes of the TSDB to be tested only result in changes of the TSDB client adapters.

Not yet considered:

- Cloud general
- Distributed Benchmark-Client
- Dynamic amount of Resources
- Cost assessment of `write` Request to set Performance in relation to cost.
- Geo Distribution of TSDB
- fault tolerance of distributed TSDB
- Multi tenancy and shareded resources

### Trade off (DRAFT)

__Relevance: Interact with the TSDB in realistic way__ 

Understandability is preferred to relevance (Realism of Application, Understandability vs. Relevance).
The benchmark to be implemented tests the performance in the PUSH procedure, in practice metrics are also collected in the PULL procedure.
For simplicity reasons a scraping proxy to support PULL is not implemented.

__Relevance: Longevity of the Benchmark__

Understandability is preferred over relevance (Realism of Application, Understandability vs. Relevance).
The longevity of the benchmark depends on the generator configuration and the amount of generated data.
A cron-like execution of the periodic or timed measurement is not implemented.

__Fairness: Multi Quality assessment__

Portability is preferred to fairness (Misuse of Services, Portability vs. Fairness).
A multi quality assessment is not implemented.
Only the quality performance for the `write` operation is measured via the latency.
Other quality measurements for e.g. Availability, Security, Elastic Scalability and Data Consistency are not implemented.