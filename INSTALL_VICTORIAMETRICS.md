# Install VictoriaMetrics v1.85.3

- Show how to install VictoriaMetrics

See more: https://docs.influxdata.com/influxdb/v2.6/install/

## Install Database

Releases: https://github.com/VictoriaMetrics/VictoriaMetrics/releases

To download execute:

```
curl "https://objects.githubusercontent.com/github-production-release-asset-2e65be/150954997/45a141b4-1cbc-43e7-b1c0-d709e1d40fb5?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIWNJYAX4CSVEH53A%2F20230110%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20230110T203237Z&X-Amz-Expires=300&X-Amz-Signature=86153743822f63b9437846ba58c3f451532a28eb64edee8a591f2d9a269b655a&X-Amz-SignedHeaders=host&actor_id=0&key_id=0&repo_id=150954997&response-content-disposition=attachment%3B%20filename%3Dvictoria-metrics-darwin-amd64-v1.85.3.tar.gz&response-content-type=application%2Foctet-stream" --output vm.tar.gz
tar -xzvf vm.tar.gz
```

To run execute:

```
./victoria-metrics-prod
```

Database WebInterface is available via: `http://127.0.0.1:8428/vmui/`

## CURL examples

### Create Data without timestamp

```
curl -d 'myMeasurement,myLabelKey1=myLabelValue1 myField1=20,myField2=1.23' -X POST 'http://localhost:8428/write'
curl -d 'myMeasurement,myLabelKey1=myLabelValue1 myField1=10,myField2=0.23' -X POST 'http://localhost:8428/write'
```

### Create Data with timestamp

```
curl -d 'myMeasurement,myLabelKey1=myLabelValue1 myField1=123,myField2=1.23 1673176214' -X POST 'http://localhost:8428/write' \
curl -d 'myMeasurement,myLabelKey1=myLabelValue1 myField1=13,myField2=0.23 1673176214' -X POST 'http://localhost:8428/write'
```

## Get all Labels

```
curl -g 'http://localhost:8428/api/v1/labels'
```
---> returns "myLabelKey1"

## Get single Label values

```
curl -G 'http://localhost:8428/api/v1/label/myLabelKey1/values'
```
---> returns "myLabelValue1"

## Get all Label values field values

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

## Count Series

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

## Get all Series (one Series per field)

```
curl -G 'http://localhost:8428/api/v1/series' -d 'match[]={__name__=~".*"}'
```
--> returns "myMeasurement_myField1" and "myMeasurement_myField2"

## Get all Series with prefix "myMeasurement" (one Series per field)

```
curl -G 'http://localhost:8428/api/v1/series' -d 'match[]={__name__=~"myMeasurement.*"}'
```
--> returns "myMeasurement_myField1" and "myMeasurement_myField2"

## Get a Series

```
curl -G 'http://localhost:8428/api/v1/series?match[]=myMeasurement_myField1'
```

## Query Series value

```
curl 'http://localhost:8428/api/v1/query' -d 'query=myMeasurement_myField1'
```

## Export Series values

```
curl -X POST 'http://localhost:8428/api/v1/export' -d 'match[]={__name__=~"myMeasurement_myField1"}'
```

## Delete Series

```
curl 'http://localhost:8428/api/v1/admin/tsdb/delete_series?match[]=myMeasurement_myField1'
```

## Query latest value?

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
