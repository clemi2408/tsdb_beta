# Preconditions
## Software
To run a query its required to have the Time Series Databases (TSDB) installed.
The following Guides are available:

- [InfluxDB](/INSTALL_INFLUX_DB.md)
- [VictoriaMetrics](/INSTALL_VICTORIAMETRICS.md)
## Environment Variables
the examples for the influxdb make use of environment variables.
the following variables are set:

### Org and Bucket

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG=testorg
export INFLUX_BUCKET=bucket
```

### OrgID and BucketID

some queries need ids instead of names. 
Besides the names in `INFLUX_ORG` and `INFLUX_BUCKET` their ids are required in `INFLUX_ORG_ID` in and `INFLUX_BUCKET_ID`.
Set those variables after initial org and bucket creation:

```bash
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET_ID=b9132e431c712bfa
```

### Measurement, Labels and Label Values

```bash
export MEASUREMENT_NAME=myMeasurement
export LABEL1_NAME=myLabelKey1
export LABEL1_VALUE=myLabelValue1
```

### Field and Field Values

```bash
export FIELD1_NAME=myField1
export FIELD1_VALUE=10
export FIELD2_NAME=myField2
export FIELD2_VALUE=1.23
```

### Time Variables

```bash
export START_VALUE=-1d
export STEP_VALUE=1s
```

# Queries

## Health Check

### Victoria

> https://github.com/VictoriaMetrics/VictoriaMetrics/issues/3539

Command:

```bash
curl -v http://victoria:8428/metrics
```

### Influx

Command:

```bash
curl "http://localhost:8086/health"
```

Returns:

```
todo
```

## Structural Queries

### Victoria
Not required as concepts like `orgs` and `buckets are not present
...

### Influx

#### List organisations

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
```

Command:

```bash
curl "http://localhost:8086/api/v2/orgs" \
--header "Authorization: Token ${INFLUX_TOKEN}"
```

Returns:

```
todo
```

#### List buckets

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
```

Command:

```bash
curl "http://localhost:8086/api/v2/buckets" \
--header "Authorization: Token ${INFLUX_TOKEN}"
```

Returns:

```
todo
```

#### Create buckets

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
```

Command:

```bash
curl -v --request POST \
	  "http://localhost:8086/api/v2/buckets" \
      --header "Authorization: Token ${INFLUX_TOKEN}" \
      --header "Content-type: application/json" \
      --data "{ \"orgID\": \"$INFLUX_ORG_ID\", \"name\": \"$INFLUX_BUCKET\" }"
```

Returns:

```
todo
```

use bucket and org id in response to populate `INFLUX_BUCKET_ID` env variable:

#### Delete bucket

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_BUCKET_ID=b9132e431c712bfa
```

Command:

```bash
curl --request DELETE "http://localhost:8086/api/v2/buckets/${INFLUX_BUCKET_ID}" \
  --header "Authorization: Token ${INFLUX_TOKEN}" \
  --header "Accept: application/json"
```

Returns:

```
todo
```

## Insert Queries

### Insert data without timestamp

#### Victoria

Variables:

```bash
export MEASUREMENT_NAME=myMeasurement
export LABEL1_NAME=myLabelKey1
export LABEL1_VALUE=myLabelValue1
export FIELD1_NAME=myField1
export FIELD1_VALUE=10
export FIELD2_NAME=myField2
export FIELD2_VALUE=1.23
```
Command:

```bash
curl \
-d "${MEASUREMENT_NAME},${LABEL1_NAME}=${LABEL1_VALUE} ${FIELD1_NAME}=${FIELD1_VALUE},${FIELD2_NAME}=${FIELD2_VALUE}" \
-X POST \
"http://localhost:8428/write"
```

Returns:

```
todo
```

#### Influx

Variables: 

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG=testorg
export INFLUX_BUCKET=bucket
export MEASUREMENT_NAME=myMeasurement
export LABEL1_NAME=myLabelKey1
export LABEL1_VALUE=myLabelValue1
export FIELD1_NAME=myField1
export FIELD1_VALUE=10
export FIELD2_NAME=myField2
export FIELD2_VALUE=1.23
```

Command:

```bash
curl \
-d "${MEASUREMENT_NAME},${LABEL1_NAME}=${LABEL1_VALUE} ${FIELD1_NAME}=${FIELD1_VALUE},${FIELD2_NAME}=${FIELD2_VALUE}" \
-X POST \
-H "Authorization: Token ${INFLUX_TOKEN}" \
"http://localhost:8086/api/v2/write?org=${INFLUX_ORG}&bucket=${INFLUX_BUCKET}&precision=s"
```

Returns:

```
todo
```

### Insert Data with timestamp

#### Victoria

Variables:

```bash
export MEASUREMENT_NAME=myMeasurement
export LABEL1_NAME=myLabelKey1
export LABEL1_VALUE=myLabelValue1
export FIELD1_NAME=myField1
export FIELD1_VALUE=10
export FIELD2_NAME=myField2
export FIELD2_VALUE=1.23
```
Command:

```bash
curl \
-d "${MEASUREMENT_NAME},${LABEL1_NAME}=${LABEL1_VALUE} ${FIELD1_NAME}=${FIELD1_VALUE},${FIELD2_NAME}=${FIELD2_VALUE} 1692726276" \
-X POST \
"http://localhost:8428/write" 
```

Returns:

```
todo
```

#### Influx

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG=testorg
export INFLUX_BUCKET=bucket
export MEASUREMENT_NAME=myMeasurement
export LABEL1_NAME=myLabelKey1
export LABEL1_VALUE=myLabelValue1
export FIELD1_NAME=myField1
export FIELD1_VALUE=10
export FIELD2_NAME=myField2
export FIELD2_VALUE=1.23
```

Command:

```bash
curl \
-d "${MEASUREMENT_NAME},${LABEL1_NAME}=${LABEL1_VALUE} ${FIELD1_NAME}=${FIELD1_VALUE},${FIELD2_NAME}=${FIELD2_VALUE} 1692726276" \
-X POST \
-H "Authorization: Token ${INFLUX_TOKEN}" \
"http://localhost:8086/api/v2/write?org=${INFLUX_ORG}&bucket=${INFLUX_BUCKET}&precision=s"
```

Returns:

```
todo
```

## Label Queries

### Get all labels

#### Victoria

Command:

```bash
curl -g "http://localhost:8428/api/v1/labels"
```
Returns:

```
myLabelKey1
```

#### Influx

> Flux requires a time range when querying time series data. "Unbounded" https://docs.influxdata.com/influxdb/cloud/query-data/get-started/query-influxdb/

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
export START_VALUE=-1d
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
import "influxdata/influxdb/schema"
schema.tagKeys(
bucket: "'$INFLUX_BUCKET'",
predicate: (r) => true,
start: '$START_VALUE'
)
'
```

Returns:

```
,result,table,_value
,_result,0,_start
,_result,0,_stop
,_result,0,_field
,_result,0,_measurement
,_result,0,myLabelKey1
```

### Get single label values

#### Victoria

Variables:

```bash
export LABEL1_NAME=myLabelKey1
```

Command:

```bash
curl -G "http://localhost:8428/api/v1/label/${LABEL1_NAME}/values"
```

Returns:

```
myLabelValue1
```

#### Influx

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
export START_VALUE=-1d
export LABEL1_NAME=myLabelKey1
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
from(bucket: "'$INFLUX_BUCKET'")
|> range(start: '$START_VALUE')
|> group(columns: ["'LABEL1_NAME'"])
|> distinct(column: "'LABEL1_NAME'")
|> keep(columns: ["_value"])
'
```

Returns:

```
,result,table,_value
,_result,0,myLabelValue1
```

### Get all label values field values

#### Victoria

Variables:

```bash
export MEASUREMENT_NAME=myMeasurement
```
Command:

```bash
curl -X GET "http://localhost:8428/api/v1/label/__name__/values" --data-urlencode "match[]={__name__=~".+", run="${MEASUREMENT_NAME}"}"
```

Returns:

```
"myMeasurement_myField1", "myMeasurement_myField2"
```

#### Influx

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
export START_VALUE=-1d
export MEASUREMENT_NAME=myMeasurement
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
from(bucket: "'$INFLUX_BUCKET'")
|> range('$START_VALUE'))
|> filter(fn: (r) => r["_measurement"] == "'$MEASUREMENT_NAME'")
|> distinct(column: "_field")
|> keep(columns: ["_field"])
'
```

Returns:

```
,result,table,_field
,_result,0,myField1
,_result,1,myField2
```

## Series Queries

### Count series

#### Victoria

Command:

```bash
curl -s "http://localhost:8428/api/v1/series/count"
```

Returns:

```
2 # "myField1" and "myField2"
```

#### Influx

> Series cardinality is the number of unique database, measurement, tag set, and field key combinations in an InfluxDB instance. https://community.influxdata.com/t/influx-query-return-number-of-unique-series-in-measurement/16679/2

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
export START_VALUE=-1d
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
import "influxdata/influxdb/schema"
schema.fieldKeys(
bucket: "'$INFLUX_BUCKET'",
predicate: (r) => true,
start: '$START_VALUE'
)
|> count()
'
```

Returns:

```
,result,table,_value
,_result,0,2
```

### Get all series (one series per field)

#### Victoria

Command:

```bash
curl -G "http://localhost:8428/api/v1/series" -d "match[]={__name__=~".*"}"
```

Returns:

```
myMeasurement_myField1, myMeasurement_myField2
```

#### Influx

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
export START_VALUE=-1d
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
import "influxdata/influxdb/schema"
schema.fieldKeys(
bucket: "'$INFLUX_BUCKET'",
predicate: (r) => true,
start: '$START_VALUE'
)
'
```

Returns:

```
,result,table,_value
,_result,0,myField1
,_result,0,myField2
```

### Get all series with prefix "myMeasurement" (one series per field)

#### Victoria

Variables:

```bash
export MEASUREMENT_NAME=myMeasurement
```
Command:

```bash
curl -G "http://localhost:8428/api/v1/series" -d "match[]={__name__=~"${MEASUREMENT_NAME}.*"}"
```

Returns:

```
myMeasurement_myField1, myMeasurement_myField2
```

#### Influx

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
export START_VALUE=-1d
export MEASUREMENT_NAME=myMeasurement
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
from(bucket: "'$INFLUX_BUCKET'")
|> range('$START_VALUE'))
|> filter(fn: (r) => r["_measurement"] == "'$MEASUREMENT_NAME'")
|> distinct(column: "_field")
|> keep(columns: ["_field"])
'
```

Returns:

```
,result,table,_value
,_result,0,myField1
,_result,0,myField2
```

### Export series values

#### Victoria

Variables:

```bash
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
```
Command:

```bash
curl -X POST "http://localhost:8428/api/v1/export" -d "match[]={__name__=~"${MEASUREMENT_NAME}_${FIELD1_NAME}"}"
```

Returns:

```
todo
```

#### Influx

Variables:

```bash
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_BUCKET=bucket
export START_VALUE=-1d
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
from(bucket: "'$INFLUX_BUCKET'")
|> range('$START_VALUE'))
|> filter(fn: (r) => r["_measurement"] == "'$MEASUREMENT_NAME'")
|> filter(fn: (r) => r["_field"] == "'$FIELD1_NAME'")
|> keep(columns: ["_time", "_value"])
'
```

Returns:

```
tbd
```

### Delete series

#### Victoria

Variables:

```bash
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
```

Command:

```bash
curl "http://localhost:8428/api/v1/admin/tsdb/delete_series?match[]=${MEASUREMENT_NAME}_${FIELD1_NAME}"
```

Returns:

```
todo
```

#### Influx

> There is no way to delete a "column" (i.e. a field or a tag) from an Influx measurement. https://github.com/influxdata/influxdb/issues/6150

## Value Queries

### Single Value Queries

#### Query latest value

##### Victoria

Variables:

```bash
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
```
Command:

```bash
curl "http://localhost:8428/api/v1/query?query=${MEASUREMENT_NAME}_${FIELD1_NAME}"
```

Returns:
 
```
13
```

##### Influx

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
export START_VALUE=-1d
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
from(bucket: "'$INFLUX_BUCKET'")
|> range('$START_VALUE'))
|> filter(fn: (r) => r["_measurement"] == "'$MEASUREMENT_NAME'")
|> filter(fn: (r) => r["_field"] == "'$FIELD1_NAME'")
|> last()
|> keep(columns: ["_value"])
'
```

Returns:

```
,result,table,_value
,_result,0,10
```

### Aggregation Queries

#### SUM Query

##### Victoria

Variables:

```bash
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
export START_VALUE=-1d
export STEP_VALUE=1s
```
Command:

```bash
curl "http://localhost:8428/api/v1/query" -d "query=sum_over_time(${MEASUREMENT_NAME}_${FIELD1_NAME})" -d "start=${START_VALUE}" -d "step=${STEP_VALUE}"
```

Returns:

```
todo
```

##### Influx

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
export START_VALUE=-1d
export STEP_VALUE=1s
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
from(bucket: "'$INFLUX_BUCKET'")
  |> range('$START_VALUE'))
  |> filter(fn: (r) => r["_measurement"] == "'$MEASUREMENT_NAME'")
  |> filter(fn: (r) => r["_field"] == "'$FIELD1_NAME'")
  |> aggregateWindow(every: '$STEP_VALUE', fn: sum, createEmpty: false)
  |> yield(name: "sum")
'
```

Returns:

```
,result,table,_start,_stop,_time,_value,_field,_measurement,myLabelKey1
,sum,0,1969-12-31T23:57:00Z,2023-09-03T11:11:46.533846003Z,2023-08-22T17:43:29Z,10,myField1,myMeasurement,myLabelValue1
,sum,0,1969-12-31T23:57:00Z,2023-09-03T11:11:46.533846003Z,2023-08-22T17:43:37Z,10,myField1,myMeasurement,myLabelValue1
,sum,0,1969-12-31T23:57:00Z,2023-09-03T11:11:46.533846003Z,2023-08-22T17:43:45Z,10,myField1,myMeasurement,myLabelValue1
```

#### AVG Query

##### Victoria

Variables:

```bash
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
export START_VALUE=-1d
export STEP_VALUE=1s
```
Command:

```bash
curl "http://localhost:8428/api/v1/query_range" -d "query=avg_over_time(${MEASUREMENT_NAME}_${FIELD1_NAME}[10m])" -d "start=${START_VALUE}" -d "step=${STEP_VALUE}"
```

Returns:

```
todo
```

##### Influx

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
export START_VALUE=-1d
export STEP_VALUE=1s
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
from(bucket: "'$INFLUX_BUCKET'")
  |> range('$START_VALUE'))
  |> filter(fn: (r) => r["_measurement"] == "'$MEASUREMENT_NAME'")
  |> filter(fn: (r) => r["_field"] == "'$FIELD1_NAME'")
  |> aggregateWindow(every: 'STEP_VALUE', fn: mean, createEmpty: false)
  |> yield(name: "avg")
'
```

Returns:

```
,result,table,_start,_stop,_time,_value,_field,_measurement,myLabelKey1
,avg,0,1969-12-31T23:57:00Z,2023-09-03T11:35:12.518463347Z,2023-08-22T17:43:29Z,10,myField1,myMeasurement,myLabelValue1
,avg,0,1969-12-31T23:57:00Z,2023-09-03T11:35:12.518463347Z,2023-08-22T17:43:37Z,10,myField1,myMeasurement,myLabelValue1
,avg,0,1969-12-31T23:57:00Z,2023-09-03T11:35:12.518463347Z,2023-08-22T17:43:45Z,10,myField1,myMeasurement,myLabelValue1
```

#### MIN Query

##### Victoria

Variables:

```bash
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
export START_VALUE=-1d
export STEP_VALUE=1s
```
Command:

```bash
curl "http://localhost:8428/api/v1/query_range" -d "query=min_over_time(${MEASUREMENT_NAME}_${FIELD1_NAME}[10m])" -d "start=${START_VALUE}" -d "step=${STEP_VALUE}"
```

Returns:

```
todo
```

##### Influx

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
export START_VALUE=-1d
export STEP_VALUE=1s
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
from(bucket: "'$INFLUX_BUCKET'")
  |> range('$START_VALUE'))
  |> filter(fn: (r) => r["_measurement"] == "'$MEASUREMENT_NAME'")
  |> filter(fn: (r) => r["_field"] == "'$FIELD1_NAME'")
  |> aggregateWindow(every: '$STEP_VALUE', fn: min, createEmpty: false)
  |> yield(name: "min")
'
```

Returns:

```
,result,table,_start,_stop,_time,_value,_field,_measurement,myLabelKey1
,min,0,1969-12-31T23:57:00Z,2023-09-03T11:41:26.822476847Z,2023-08-22T17:43:29Z,10,myField1,myMeasurement,myLabelValue1
,min,0,1969-12-31T23:57:00Z,2023-09-03T11:41:26.822476847Z,2023-08-22T17:43:37Z,10,myField1,myMeasurement,myLabelValue1
,min,0,1969-12-31T23:57:00Z,2023-09-03T11:41:26.822476847Z,2023-08-22T17:43:45Z,10,myField1,myMeasurement,myLabelValue1
```

#### MAX Query

##### Victoria

Variables:

```bash
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
export START_VALUE=-1d
export STEP_VALUE=1s
```
Command:

```bash
curl "http://localhost:8428/api/v1/query_range" -d "query=max_over_time(${MEASUREMENT_NAME}_${FIELD1_NAME}[10m])" -d "start=${START_VALUE}" -d "step=${STEP_VALUE}"
```

Returns:

```
todo
```

##### Influx

Variables:

```bash
export INFLUX_TOKEN=4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==
export INFLUX_ORG_ID=07ba829c0ea87d3c
export INFLUX_BUCKET=bucket
export MEASUREMENT_NAME=myMeasurement
export FIELD1_NAME=myField1
export START_VALUE=-1d
export STEP_VALUE=1s
```

Command:

```bash
curl --request POST \
"http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
--header "Authorization: Token ${INFLUX_TOKEN}" \
--header 'Accept: application/csv' \
--header 'Content-type: application/vnd.flux' \
--data '
from(bucket: "'$INFLUX_BUCKET'")
  |> range('$START_VALUE'))
  |> filter(fn: (r) => r["_measurement"] == "'$MEASUREMENT_NAME'")
  |> filter(fn: (r) => r["_field"] == "'$FIELD1_NAME'")
  |> aggregateWindow(every: '$STEP_VALUE', fn: max, createEmpty: false)
  |> yield(name: "max")
'
```

Returns:

```
,result,table,_start,_stop,_time,_value,_field,_measurement,myLabelKey1
,min,0,1969-12-31T23:57:00Z,2023-09-03T11:41:26.822476847Z,2023-08-22T17:43:29Z,10,myField1,myMeasurement,myLabelValue1
,min,0,1969-12-31T23:57:00Z,2023-09-03T11:41:26.822476847Z,2023-08-22T17:43:37Z,10,myField1,myMeasurement,myLabelValue1
,min,0,1969-12-31T23:57:00Z,2023-09-03T11:41:26.822476847Z,2023-08-22T17:43:45Z,10,myField1,myMeasurement,myLabelValue1
```

# Work in Progress
## Prios
### Prio 1 

Query Returns: currentValue of a {random|defined} field (based on field key presence):
Former query F
```json
{
    "type" : "CURRENT_FIELD_VALUE",
    "selectCount" : 100,
    "key": "cpu"
}
```

Returns: aggregated type value of a {random|defined} field (based on field key presence) from startOffset in resolution.
Default values if not set:

- StartOffset=1H 
- Resolution 1Min

Former query G

```json
{
    "type" : "AGGREGATED_FIELD_VALUES",
    "selectCount" : 100,
    "aggregateType" : "SUM",
    "key": "cpu",
    "startOffset": "P1H",
    "resolution": "P1S"
}
```

Return A all labels, B a specific label value or C all field values of records matching the label.

A= ALL_LABELS --> All Labels (--> Prio 3)
B= LABELS_VALUE --> Specific Label value (--> Prio 3)
C= FIELD_VALUES_MATCHING_LABELS --> All Field Values of Records matching the label (--> Prio 3)
aggregateType is Optional and defaulted to NONE

```json
{
    "type" : "{ALL_LABELS,LABELS_VALUE,FIELD_VALUES_MATCHING_LABELS}", 
    "minSelectInterval" : "P1H",
    "maxSelectInterval" : "P8H",
    "selectCount" : 10,
    "aggregateType" : "NONE",
    "labels": ["label1", "label2"]
}
```
### Prio 2

Returns: all fields and their values in a series
Former query D

```json
{
    "type" : "ALL_FIELDS_WITH_VALUES",
    "selectCount" : 100
}
```

Query Returns: all fields starting with a prefix
Former query E

```json
{
    "type" : "ALL_FIELDS_STARTING_WITH_PREFIX",
    "selectCount" : 100
}
```