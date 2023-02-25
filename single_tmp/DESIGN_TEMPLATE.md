# TSDB Benchmark Design Template

## Scenario Domain

- target audence
- application domain

### Qualities

- performance
    - throughput: requests in parallel (requests per second)
        - Moving average for 1s interval
    - latency: time to complete a request (time in ms)
        - Median, Max, Min, Percentiles
    - Show values in discrete curve (sliding window aggregation)

## General Design Objectives

### Relevance

- Designed based on a realistic scenario
- Interact with SUT in realistic way
- Use query Types that are realistic
- Query Frequency should reflect scenario domain
- Stress SUT based on Scenario Domain
- Longevity

### Reproducibility or repeatability

- only chance to reproduce if repeatable
- re run will lead to same/similar results
- reproduce same run and same/similar results
- in cloud often repeatable but not reproduceable

### Fairness

- treat all SUT fair
- no implicit assumptions
- use common feature scope
- multi quality assessement (chapter 3)
- have intense knowledge about available solutions

### Portability

- run against a large number of Systems and Services
- test common feature area not bleeding edge
- does not rely on advanced features of the provider

### Understandability

- understand what benchmark is measuring
- results be easy to verify
- meaningful and understandable metrics (chapter 6)
- workload scenario needs to be understood
- simplicity vs. realistic scenario

## New Design Objectives in Cloud Service Benchmarking

- more dynamic
- not longer static
- often not fixed amount of resources (illusion of infinite)
- Performance weightened in relation to costs
- geo distribution (fault tolerance, latency)
- mulit tenancy (shared resources)
- nature of modern applications (fast evolution, new application requirements could lead to cloud infra changes)
- benchmark needs to be highly changeable

## Trade off

- simplicity vs. realistic 

