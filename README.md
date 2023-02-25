# Time Series Database (TSDB) Benchmark Beta

## Design

The following text describes a benchmark design form TSDBs.
The scenario of the experiment and the qualities to be measured are described.
On the one hand, general design objectives are explained.
On the other hand, new design objectives for cloud service benchmarks are described.

### Scenario

There are many possible reasons for collecting and storing data in a temporal context.
Examples are measured values and events in scientific experiments but also status data of software applications in
operation.
TSDBs support the storage of data in a time context.
They also offer additional query and analysis options for the stored data.

> A time series database (TSDB) is a database optimized for time-stamped or time series data.
> Time series data are simply measurements or events that are tracked, monitored, downsampled, and aggregated over time.
> This could be server metrics, application performance monitoring, network data, sensor data, events, clicks, trades in
> a market, and many other types of analytics data.
>
>
_Source: [https://www.influxdata.com/time-series-database/ 09.01.2023](https://www.influxdata.com/time-series-database/)_

There is a large number of different TSDB implementations that allow key-value pairs to be stored in the form of a time
series in such a way that they can be queried.
Besides the query, the storage of data is of central importance.
This benchmark design - as a basis for a subsequent implementation - focuses on the storage of time series.

The benchmark implementation resulting from the design is to compare the quality performance when writing data to
different TSDBs.
On the one hand, it should be possible to generate random workload data via a workload generator configuration.
On the other hand, it should be possible to use existing workload data generated in earlier benchmark runs.

#### Qualities

The benchmark to be implemented measures as quality the performance of different TSDBs.
The performance of the write operations of different TSDB implementations is measured via the latency in milliseconds (
ms).
Identical data sets are sent to different TSDBs for storage via the benchmark application to be implemented.
In addition to the raw measurement data, the quality performance of the different TSDBs should become comparable as
follows:

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
The benchmark to be implemented tests the performance in the PUSH procedure, in practice metrics are also collected in
the PULL procedure.
For simplicity reasons a scraping proxy to support PULL is not implemented.

__Relevance: Longevity of the Benchmark__

Understandability is preferred over relevance (Realism of Application, Understandability vs. Relevance).
The longevity of the benchmark depends on the generator configuration and the amount of generated data.
A cron-like execution of the periodic or timed measurement is not implemented.

__Fairness: Multi Quality assessment__

Portability is preferred to fairness (Misuse of Services, Portability vs. Fairness).
A multi quality assessment is not implemented.
Only the quality performance for the `write` operation is measured via the latency.
Other quality measurements for e.g. Availability, Security, Elastic Scalability and Data Consistency are not
implemented.

## Modules & Packages

```
tsdbBenchmark (de.cleem.tub.tsdbb)
├── commons (de.cleem.tub.tsdbb.commons)
├── apps (de.cleem.tub.tsdbb.apps)
│   ├── generator (de.cleem.tub.tsdbb.apps.generator)
│   ├── orchestrator (de.cleem.tub.tsdbb.apps.orchestrator)
│   ├── worker (de.cleem.tub.tsdbb.apps.worker)
```

### General Dependencies

- org.projectlombok.lombok
- org.slf4j.slf4j-simple
- com.fasterxml.jackson.core.jackson-core
- com.fasterxml.jackson.core.jackson-databind

## Commons

[README](commons/README.md)

## Apps

[README](apps/README.md)
