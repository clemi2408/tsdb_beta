# Queries 

## Create Data without timestamp

### Victoria

```
curl -d 'myMeasurement,myLabelKey1=myLabelValue1 myField1=10,myField2=1.23' -X POST 'http://localhost:8428/write'
curl -d 'myMeasurement,myLabelKey1=myLabelValue1 myField1=25,myField2=0.23' -X POST 'http://localhost:8428/write'
curl -d 'myMeasurement,myLabelKey1=myLabelValue1 myField1=30,myField2=0.99' -X POST 'http://localhost:8428/write'
```

### Influx
....

## Create Data with timestamp
### Victoria
```
curl -d 'myMeasurement,myLabelKey1=myLabelValue1 myField1=123,myField2=1.23 1673176214' -X POST 'http://localhost:8428/write' \
curl -d 'myMeasurement,myLabelKey1=myLabelValue1 myField1=13,myField2=0.23 1673176214' -X POST 'http://localhost:8428/write'
```

### Influx
....

##  Get all Labels

### Victoria
```
curl -g 'http://localhost:8428/api/v1/labels'
```
---> returns "myLabelKey1"

### Influx
....


## Victoria: Get single Label values

```
curl -G 'http://localhost:8428/api/v1/label/myLabelKey1/values'
```
---> returns "myLabelValue1"

## Victoria: Get all Label values field values

```
curl -XGET -G 'http://localhost:8428/api/v1/label/__name__/values'
```
---> returns "myMeasurement_myField1", "myMeasurement_myField2"

```
curl -X GET 'http://localhost:8428/api/v1/label/__name__/values' --data-urlencode 'match[]={__name__=~".+", run="myMeasurement"}'
```
---> returns "myMeasurement_myField1", "myMeasurement_myField2" because in run "myMeasurement"

```
curl -X GET 'http://localhost:8428/api/v1/label/__name__/values' --data-urlencode 'match[]={__name__=~"myMeasurement.+"}'
```
---> returns "myMeasurement_myField1", "myMeasurement_myField2" because name starts with "myMeasurement"

## Victoria: Count Series

```
curl -s 'http://localhost:8428/api/v1/series/count'
```
---> Returns 2 because of "myField1" and "myField2"

```
curl -X GET 'http://localhost:8428/api/v1/series/count' --data-urlencode 'match[]={__name__=~".+", run="myMeasurement"}'
```
---> returns 2 because of 2 series in run "myMeasurement"

```
curl -X GET 'http://localhost:8428/api/v1/series/count' --data-urlencode 'match[]={__name__=~"myMeasurement.+"}'
```
---> returns 2 because of 2 series starting  with "myMeasurement"

## Victoria: Get all Series (one Series per field)

```
curl -G 'http://localhost:8428/api/v1/series' -d 'match[]={__name__=~".*"}'
```
--> returns "myMeasurement_myField1" and "myMeasurement_myField2"

## Victoria: Get all Series with prefix "myMeasurement" (one Series per field)

```
curl -G 'http://localhost:8428/api/v1/series' -d 'match[]={__name__=~"myMeasurement.*"}'
```
--> returns "myMeasurement_myField1" and "myMeasurement_myField2"

## Victoria: Get a Series

```
curl -G 'http://localhost:8428/api/v1/series?match[]=myMeasurement_myField1'
```

## Victoria: Query Series value

```
curl 'http://localhost:8428/api/v1/query' -d 'query=myMeasurement_myField1'
```

## Victoria: Export Series values

```
curl -X POST 'http://localhost:8428/api/v1/export' -d 'match[]={__name__=~"myMeasurement_myField1"}'
```

## Victoria: Delete Series

```
curl 'http://localhost:8428/api/v1/admin/tsdb/delete_series?match[]=myMeasurement_myField1'
```

## Victoria: Query latest value?

```
curl 'http://localhost:8428/api/v1/query' -d 'query=myMeasurement_myField1'
```
--> Returns "13" as it was added last

## Queries

### Query: sum_over_time

#### Query sum_over_time

```
curl 'http://localhost:8428/api/v1/query' -d 'query=sum_over_time(myMeasurement_myField1)'
```
--> Returns "13" as it was added last

```
curl 'http://localhost:8428/api/v1/query' -d 'query=sum_over_time(myMeasurement_myField1)' -d 'start=-10m' -d 'step=1s'
```

#### Query range: sum_over_time myMeasurement_myField1 for last [10m]

```
curl 'http://localhost:8428/api/v1/query_range' -d 'query=sum_over_time(myMeasurement_myField1[10m])'
```
--> returns sum of values of myMeasurement_myField1 in the last 10m

#### Query range: sum_over_time up myMeasurement_myField1 for last [10m] and return values from "-10m" to now in resulution "1m"

```
curl 'http://localhost:8428/api/v1/query_range' -d 'query=sum_over_time(myMeasurement_myField1[10m])' -d 'start=-10m' -d 'step=1m'
```

--> returns sums of values of myMeasurement_myField1 in the last 10m "plotted" in frame now() -10m to now() in resolution 1m

### Query avg_over_time

#### Query range: avg_over_time myMeasurement_myField1 for last [10m]

```
curl 'http://localhost:8428/api/v1/query_range' -d 'query=avg_over_time(myMeasurement_myField1[10m])' 
```

#### Query range: avg_over_time up myMeasurement_myField1 for last [10m] and return values from "-10m" to now in resulution "1m"

```
curl 'http://localhost:8428/api/v1/query_range' -d 'query=avg_over_time(myMeasurement_myField1[10m])' -d 'start=-10m' -d 'step=1m'
```


### Query min_over_time

#### Query range: min_over_time myMeasurement_myField1 for last [10m]

```
curl 'http://localhost:8428/api/v1/query_range' -d 'query=min_over_time(myMeasurement_myField1[10m])' 
```

#### Query range: min_over_time up myMeasurement_myField1 for last [10m] and return values from "-10m" to now in resulution "1m"

```
curl 'http://localhost:8428/api/v1/query_range' -d 'query=min_over_time(myMeasurement_myField1[10m])' -d 'start=-10m' -d 'step=1m'
```

### Query max_over_time

#### Query range: max_over_time myMeasurement_myField1 for last [10m]

```
curl 'http://localhost:8428/api/v1/query_range' -d 'query=max_over_time(myMeasurement_myField1[10m])' 
```

#### Query range: max_over_time up myMeasurement_myField1 for last [10m] and return values from "-10m" to now in resulution "1m"

```
curl 'http://localhost:8428/api/v1/query_range' -d 'query=max_over_time(myMeasurement_myField1[10m])' -d 'start=-10m' -d 'step=1m'
```

Prio 3:
{
"type" : "A",
"selectCount" : 100,
//All Labels
}
{
"type" : "B",
"selectCount" : 100,
"labelValues": ["Sensor1", "Sensor2"]
//Specific Label value
}
{
"type" : "C",
"selectCount" : 100,
"labelValues": ["Sensor1", "Sensor2"]
//All Field Values of Records matching the label.
}



Prio 2:

{
"type" : "D",
"selectCount" : 100,
//Returns all Fields and their values in a RUN
}

{
"type" : "E",
"selectCount" : 100,
//Returns all Fields starting with a Prefix
}


Prio 1: 

{
"type" : "F",
"selectCount" : 100,
"key": "cpu" // or optional

//Returns currentValue of a {random|defined} field
}

{
"type" : "G",
"selectCount" : 100,
"aggregateType" : "SUM",
"key": "cpu" // or optional,
"startOffset": "P1H",
"resolution": "P1S"


//Returns Aggregated Type value of a {random|defined} field from StartOffset in Resuolution.
// if StartOffset=Default 1H & Resolution=Default 1Min
}


{
"minSelectInterval" : "P1H",
"type" : "{A,B,C}", A=LABEL;
"maxSelectInterval" : "P8H",
"selectCount" : 10,
"aggregateType" : "NONE"
}