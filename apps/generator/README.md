# tsdbBenchmark BETA

[README](../../README.md)

## Apps

[README](../README.md)

### Generator

Main method class: `de.cleem.tub.tsdbb.apps.generator.App`

For repeatability of the benchmark, the `GeneratorConfig` holding the configuration of the `WorkloadGenerator` and
the generated workload is persisted.
The `GeneratorConfig` has the following configuration parameters:

- Number of records `recordCount`.
- List of `RecordConfig` entries `recordConfigs`.

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
- A collection of characters  `java.lang.String`.

#### Examples

Example `RecordConfig` with generated key with length in range `minKeyLength` to `maxKeyLength`.
The `Integer` value is generated with the value strategy `uniform` in range `minValue` and `maxValue`.

```
{
    "minKeyLength": 2,
    "maxKeyLength": 5,
    "valueDistribution": "uniform",
    "minValue": 0,
    "maxValue": 100,
    "valueType": "Integer"
  }
```

Example `RecordConfig` with defined key `keyValue`.
The `Double` value is generated with the value strategy `gaussian` with `gaussMiddle` and `gaussRange`.

```
{
    "keyValue": "gaussianDouble",
    "valueDistribution": "gaussian",
    "gaussMiddle": 10.0,
    "gaussRange": 30.0,
    "valueType": "Double"
}
```

Example `RecordConfig` with defined key `keyValue`.
The `Integer` value is generated with the value strategy `triangle` in range `minValue` and `maxValue` with `triangleSpike`.

```
{
    "keyValue": "triangleInteger",
    "valueDistribution": "triangle",
    "minValue": 0,
    "maxValue": 100,
    "triangleSpike": 50,
    "valueType": "Integer"
}
```

Example `RecordConfig` with generated key with length in range `minKeyLength` to `maxKeyLength`.
The `String` value is generated with length in range `minStringValueLength` to `maxStringValueLength`.

```
{
    "minKeyLength": 1,
    "maxKeyLength": 2,
    "valueType": "String",
    "minStringValueLength": 1,
    "maxStringValueLength": 3
}
```

Example `RecordConfig` with defined key `keyValue`.
The `String` value is generated with length in range `minStringValueLength` to `maxStringValueLength`.

```
{
    "keyValue": "sampleKey1",
    "valueType": "String",
    "minStringValueLength": 3,
    "maxStringValueLength": 5
}
```

Example `RecordConfig` with defined key `keyValue`.
The `String` values are defined in `stringEnumValues` for random selection.

```
{
    "keyValue": "sampleKey2",
    "valueType": "String",
    "stringEnumValues": [
      "s-val1",
      "s-val2"
    ]
}
```

Example `RecordConfig` with generated key with length in range `minKeyLength` to `maxKeyLength`.
The `String` values are defined in `stringEnumValues` for random selection.

```
{
    "minKeyLength": 12,
    "maxKeyLength": 15,
    "valueType": "String",
    "stringEnumValues": [
        "s-val3",
        "s-val4"
    ]
}
```

Example `RecordConfig` with defined key `keyValue`.
The amount of `String` values to be generated is in range `minStringEnumValues` to `maxStringEnumValues` for random selection.
The Generated Strings have length in range `minStringValueLength` to `maxStringValueLength`.

```
{
    "keyValue": "sampleKey3",
    "valueType": "String",
    "minStringValueLength": 1,
    "maxStringValueLength": 3,
    "minStringEnumValues": 1,
    "maxStringEnumValues": 5
}
```

Example `RecordConfig` with generated key and generated String values with the count in range `minStringEnumValues` to `maxStringEnumValues` and length in range `minStringValueLength` to `maxStringValueLength` for random selection:


Example `RecordConfig` with generated key with length in range `minKeyLength` to `maxKeyLength`.
The amount of `String` values to be generated is in range `minStringEnumValues` to `maxStringEnumValues` for random selection.
The Generated Strings have length in range `minStringValueLength` to `maxStringValueLength`.

```
{
    "minKeyLength": 6,
    "maxKeyLength": 8,
    "valueType": "String",
    "minStringValueLength": 1,
    "maxStringValueLength": 3,
    "minStringEnumValues": 1,
    "maxStringEnumValues": 5
}
```
