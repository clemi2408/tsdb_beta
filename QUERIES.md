# Queries 

## Check health

### Victoria

https://github.com/VictoriaMetrics/VictoriaMetrics/issues/3539
--> curl -v http://victoria:8428/metrics
...

### Influx

```
curl "http://localhost:8086/health"
```

## Structural Queries

### Victoria
Not required as concepts like orgs and buckets are not present
...

### Influx

#### List organisations

```
curl "http://localhost:8086/api/v2/orgs" \
--header "Authorization: Token ${INFLUX_TOKEN}"
```

#### List buckets

```
curl "http://localhost:8086/api/v2/buckets" \
--header "Authorization: Token ${INFLUX_TOKEN}"
```

#### Create buckets

```
curl --request POST \
	"http://localhost:8086/api/v2/buckets" \
	--header "Authorization: Token ${INFLUX_TOKEN}" \
  --header "Content-type: application/json" \
  --data "{
    "orgID": """${INFLUX_ORG_ID}""",
    "name": """${INFLUX_BUCKET}"""
  }"
```

#### Delete bucket

```
curl --request DELETE "http://localhost:8086/api/v2/buckets/${INFLUX_BUCKET_ID}" \
  --header "Authorization: Token ${INFLUX_TOKEN}" \
  --header "Accept: application/json"
```

## Create data without timestamp

### Victoria

```
curl \
-d "myMeasurement,myLabelKey1=myLabelValue1 myField1=10,myField2=1.23" \
-X POST \
"http://localhost:8428/write"
```

### Influx

```
curl \
-d "myMeasurement,myLabelKey1=myLabelValue1 myField1=10,myField2=1.23" \
-X POST \
-H "Authorization: Token ${INFLUX_TOKEN}"
"http://localhost:8086/api/v2/write?org=${INFLUX_ORG}&bucket=${INFLUX_BUCKET}&precision=s"
```

## Create Data with timestamp

### Victoria

```
curl \
-d "myMeasurement,myLabelKey1=myLabelValue1 myField1=123,myField2=1.23 1673176214" \
-X POST \
"http://localhost:8428/write" 
```

### Influx

```
curl \
-d "myMeasurement,myLabelKey1=myLabelValue1 myField1=10,myField2=1.23 1673176214" \
-X POST \
-H "Authorization: Token ${INFLUX_TOKEN}" \
"http://localhost:8086/api/v2/write?org=${INFLUX_ORG}&bucket=${INFLUX_BUCKET}&precision=s"
```

##  Get all labels

### Victoria

```
curl -g "http://localhost:8428/api/v1/labels"
```
---> returns "myLabelKey1"

### Influx
....

## Get single label values

### Victoria

```
curl -G "http://localhost:8428/api/v1/label/myLabelKey1/values"
```
---> returns "myLabelValue1"

### Influx
....

## Get all label values field values

### Victoria

```
curl -XGET -G "http://localhost:8428/api/v1/label/__name__/values"
```
---> returns "myMeasurement_myField1", "myMeasurement_myField2"

```
curl -X GET "http://localhost:8428/api/v1/label/__name__/values" --data-urlencode "match[]={__name__=~".+", run="myMeasurement"}"
```
---> returns "myMeasurement_myField1", "myMeasurement_myField2" because in run "myMeasurement"

```
curl -X GET "http://localhost:8428/api/v1/label/__name__/values" --data-urlencode "match[]={__name__=~"myMeasurement.+"}"
```
---> returns "myMeasurement_myField1", "myMeasurement_myField2" because name starts with "myMeasurement"

### Influx
....

## Count series

### Victoria

```
curl -s "http://localhost:8428/api/v1/series/count"
```
---> Returns 2 because of "myField1" and "myField2"

```
curl -X GET "http://localhost:8428/api/v1/series/count" --data-urlencode "match[]={__name__=~".+", run="myMeasurement"}"
```
---> returns 2 because of 2 series in run "myMeasurement"

```
curl -X GET "http://localhost:8428/api/v1/series/count" --data-urlencode "match[]={__name__=~"myMeasurement.+"}"
```
---> returns 2 because of 2 series starting  with "myMeasurement"

### Influx
....

## Get all series (one series per field)

### Victoria

```
curl -G "http://localhost:8428/api/v1/series" -d "match[]={__name__=~".*"}"
```
--> returns "myMeasurement_myField1" and "myMeasurement_myField2"

### Influx
....

## Get all series with prefix "myMeasurement" (one series per field)

### Victoria

```
curl -G "http://localhost:8428/api/v1/series" -d "match[]={__name__=~"myMeasurement.*"}"
```
--> returns "myMeasurement_myField1" and "myMeasurement_myField2"

### Influx
....

## Get a series

### Victoria

```
curl -G "http://localhost:8428/api/v1/series?match[]=myMeasurement_myField1"
```

### Influx
....

## Query series value

### Victoria

```
curl "http://localhost:8428/api/v1/query" -d "query=myMeasurement_myField1"
```

### Influx
....

## Export series values

### Victoria

```
curl -X POST "http://localhost:8428/api/v1/export" -d "match[]={__name__=~"myMeasurement_myField1"}"
```

### Influx
....

## Delete series

### Victoria

```
curl "http://localhost:8428/api/v1/admin/tsdb/delete_series?match[]=myMeasurement_myField1"
```

### Influx
....

## Query latest value

### Victoria

```
curl "http://localhost:8428/api/v1/query" -d "query=myMeasurement_myField1"
```
--> Returns "13" as it was added last

### Influx
....

## Query sum_over_time

### Victoria

```
curl "http://localhost:8428/api/v1/query" -d "query=sum_over_time(myMeasurement_myField1)"
```
--> Returns "13" as it was added last

```
curl "http://localhost:8428/api/v1/query" -d "query=sum_over_time(myMeasurement_myField1)" -d "start=-10m" -d "step=1s"
```

### Influx
....

## Query range sum_over_time myMeasurement_myField1 for last [10m]

### Victoria

```
curl "http://localhost:8428/api/v1/query_range" -d "query=sum_over_time(myMeasurement_myField1[10m])"
```
--> returns sum of values of myMeasurement_myField1 in the last 10m

### Influx
....

## Query range sum_over_time up myMeasurement_myField1 for last [10m] and return values from "-10m" to now in resulution "1m"

### Victoria

```
curl "http://localhost:8428/api/v1/query_range" -d "query=sum_over_time(myMeasurement_myField1[10m])" -d "start=-10m" -d "step=1m"
```

--> returns sums of values of myMeasurement_myField1 in the last 10m "plotted" in frame now() -10m to now() in resolution 1m

### Influx
....

## Query range avg_over_time myMeasurement_myField1 for last [10m]

### Victoria

```
curl "http://localhost:8428/api/v1/query_range" -d "query=avg_over_time(myMeasurement_myField1[10m])" 
```

### Influx
....

## Query range avg_over_time up myMeasurement_myField1 for last [10m] and return values from "-10m" to now in resulution "1m"

### Victoria

```
curl "http://localhost:8428/api/v1/query_range" -d "query=avg_over_time(myMeasurement_myField1[10m])" -d "start=-10m" -d "step=1m"
```

### Influx
....

## Query range: min_over_time myMeasurement_myField1 for last [10m]

### Victoria

```
curl "http://localhost:8428/api/v1/query_range" -d "query=min_over_time(myMeasurement_myField1[10m])" 
```

### Influx
....

## Query range min_over_time up myMeasurement_myField1 for last [10m] and return values from "-10m" to now in resulution "1m"

### Victoria

```
curl "http://localhost:8428/api/v1/query_range" -d "query=min_over_time(myMeasurement_myField1[10m])" -d "start=-10m" -d "step=1m"
```

### Influx
....

## Query range max_over_time myMeasurement_myField1 for last [10m]

### Victoria

```
curl "http://localhost:8428/api/v1/query_range" -d "query=max_over_time(myMeasurement_myField1[10m])" 
```

### Influx
....

## Query range max_over_time up myMeasurement_myField1 for last [10m] and return values from "-10m" to now in resulution "1m"

### Victoria

```
curl "http://localhost:8428/api/v1/query_range" -d "query=max_over_time(myMeasurement_myField1[10m])" -d "start=-10m" -d "step=1m"
```

### Influx
....

# WIP
## Prios
### Prio 1 

Query returns currentValue of a {random|defined} field (based on field key presence):
Former query F
```json
{
    "type" : "CURRENT_FIELD_VALUE",
    "selectCount" : 100,
    "key": "cpu"
}
```

Returns aggregated type value of a {random|defined} field (based on field key presence) from startOffset in resolution.
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

Returns all fields and their values in a series
Former query D
```json
{
    "type" : "ALL_FIELDS_WITH_VALUES",
    "selectCount" : 100
}
```

Query returns all fields starting with a prefix

Former query E

```json
{
    "type" : "ALL_FIELDS_STARTING_WITH_PREFIX",
    "selectCount" : 100
}
```