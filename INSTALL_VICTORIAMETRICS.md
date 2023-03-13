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

### Check Health

### Write Data

```
curl -X POST \
'http://localhost:8428/write' \
-d 'census,location=klamath,scientist=anderson bees=23 1673176214'
```

### Delete Series

```
curl 'http://localhost:8428/api/v1/admin/tsdb/delete_series?match[]=census_bees'
```

### Query Series

```
curl -X GET \
'http://localhost:8428/api/v1/label/__name__/values' \
--data-urlencode 'match[]={__name__=~".+", run="victoria"}'
```

### Count

```
curl -X GET \
'http://localhost:8428/api/v1/series/count' \
--data-urlencode 'match[]={__name__=~".+", run="victoria"}'
```

### Export Series

```
curl -X POST \
http://localhost:8428/api/v1/export \
-d 'match[]={__name__=~"victoria_measurement_B27"}'
```

```
curl -X POST \
http://localhost:8428/api/v1/export \
-d 'match[]={__name__=~"victoria_measurement_B27"}' > filename.json
```

## Query Examples

### Query Bucket by Field

### Count Records

# COLLECT

count_values("count", q)

count({run="victoria"}) by (group_labels)

count({__name__=~"victoria_measurement_.*"})

