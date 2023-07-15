# Install InfluxDB 2.61

- Show how to install InfluxDB

See more: https://docs.influxdata.com/influxdb/v2.6/install/

## Install Database

Install InfluxDB via brew:

```
brew install influxdb
```

Binaries are installed to: `/usr/local/Cellar/influxdb/2.7.1/bin`
To run execute:

```
cd /usr/local/Cellar/influxdb/2.7.1/bin
./influxd
```

Database WebInterface is available via: `http://localhost:8086`

## Install CLI

Install InfluxDB-CLI

```
brew install influxdb-cli
```

Binaries are installed to: `/usr/local/Cellar/influxdb-cli/2.7.3/bin`

## Initial database setup

To configure InfluxDB Daemon change to cli bin folder and execute:

```
cd /usr/local/Cellar/influxdb-cli/2.7.3/bin
influx setup
```

Then answer the questions, the following values are used:

- Username: admin
- Password: adminadmin
- Organisation: testorg
- Primary Bucket Name: bucket
- Retention Period: 24

```
> Welcome to InfluxDB 2.0!
Please type your primary username: admin
Please type your password **********
Please type your password again **********
Please type your primary organization name testorg
Please type your primary bucket name bucket
Please type your retention period in hours, or 0 for infinite 24
Setup with these parameters?
  Username:          admin
  Organization:      testorg
  Bucket:            bucket
  Retention Period:  24h0m0s
 Yes
```

## Create Operator Auth Token

To create Operator Token run:

```
cd /usr/local/Cellar/influxdb-cli/2.6.1/bin
influx auth create \
  --operator
```

e.g. `s-kr1M8PTAYyylFNE8KJ2bG2foRf0c2_c9ECxOT1VbMeLK15bBT9xsUCglGKqcVSEZBPqrtKdVR5IpcA1L-aTA==`

The response contains the token.
Example:

```
ID			Description	Token					User Name	User ID			Permissions
0a9308067f303000			YcqeMSEqzrvDsPevnsPA9fuES38RU-R9_y9UZ2gefEYaNJfVdUOVdj5NvrPpwNnmVuGoZSsqZ_jxnKx9dhdHYw==	admin		0a8f348ab0c6d000	[read:/authorizations write:/authorizations read:/buckets write:/buckets read:/dashboards write:/dashboards read:/orgs write:/orgs read:/sources write:/sources read:/tasks write:/tasks read:/telegrafs write:/telegrafs read:/users write:/users read:/variables write:/variables read:/scrapers write:/scrapers read:/secrets write:/secrets read:/labels write:/labels read:/views write:/views read:/documents write:/documents read:/notificationRules write:/notificationRules read:/notificationEndpoints write:/notificationEndpoints read:/checks write:/checks read:/dbrp write:/dbrp read:/notebooks write:/notebooks read:/annotations write:/annotations read:/remotes write:/remotes read:/replications write:/replications]
```

## CURL examples

https://docs.influxdata.com/influxdb/cloud/api/

export INFLUX_TOKEN=YcqeMSEqzrvDsPevnsPA9fuES38RU-R9_y9UZ2gefEYaNJfVdUOVdj5NvrPpwNnmVuGoZSsqZ_jxnKx9dhdHYw==
export INFLUX_ORG=testorg
export INFLUX_ORG_ID=22a4a65dfebd3452
export INFLUX_BUCKET=testbucket12345
export INFLUX_BUCKET_ID=b9132e431c712bfa

### Check Health

```
curl "http://localhost:8086/health"
```

Response code OK: 200
Response code ERROR: 503

### Orgs

#### List Orgs

```
curl "http://localhost:8086/api/v2/orgs" \
--header "Authorization: Token ${INFLUX_TOKEN}"
```

### Buckets

#### List Buckets

```
curl "http://localhost:8086/api/v2/buckets" \
--header "Authorization: Token ${INFLUX_TOKEN}"
```

#### Create Bucket

```
curl --request POST \
	"http://localhost:8086/api/v2/buckets" \
	--header "Authorization: Token ${INFLUX_TOKEN}" \
  --header "Content-type: application/json" \
  --data '{
    "orgID": "'"${INFLUX_ORG_ID}"'",
    "name": "'"${INFLUX_BUCKET}"'"
  }'
```

#### Delete Bucket

```
curl --request DELETE "http://localhost:8086/api/v2/buckets/${INFLUX_BUCKET_ID}" \
  --header "Authorization: Token ${INFLUX_TOKEN}" \
  --header 'Accept: application/json'
```

### Write Data

```
curl -v --request POST \
"http://localhost:8086/api/v2/write?org=${INFLUX_ORG}&bucket=${INFLUX_BUCKET}&precision=s" \
  --header "Authorization: Token ${INFLUX_TOKEN}" \
  --data-binary 'testtesttest1,sensor_id=TLM0201 temperature=73.97038159354763,humidity=35.23103248356096,co=0.48445310567793615 1673369771'
```

## FLUX Query Examples

https://docs.influxdata.com/influxdb/v2.6/query-data/flux/

### Query Bucket by Field

```
from(bucket: "testbucket")
|> range(start: -1h)
|> filter(fn: (r) => r["_field"] == "TMG")
```

### Count Inserts in Bucket

```
from(bucket: "testbucket")
|> range(start: -1h)
|> count()
```

