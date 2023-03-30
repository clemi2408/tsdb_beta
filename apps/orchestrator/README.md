# Time Series Database (TSDB) Benchmark - Orchestrator

[Home README](../../README.md)

[Apps README](../README.md)

Main method class: `de.cleem.tub.tsdbb.apps.orchestrator.OrchestratorApp`

Both the already generated workload and the generator configuration are part of the benchmark configuration, along with
the connection information for the specific TSDBs and the number of client threads used.
The general benchmark configuration contains:

- Workload generation configuration `generatorConfig`.
- Workload `workload`.
- Number of client threads `threadCount`.
- Boolean value to enable cleanup functionality after benchmark `cleanStorage`

