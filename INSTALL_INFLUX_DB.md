# Install InfluxDB 2.61

- Show how to install InfluxDB

See more: https://docs.influxdata.com/influxdb/v2.6/install/

## Install Database

### Debian

Update the System:

```
sudo apt update
```

Install repository key:

```
curl -fsSL https://repos.influxdata.com/influxdata-archive_compat.key|sudo gpg --dearmor -o /etc/apt/trusted.gpg.d/influxdata.gpg
```

Create sources list file:

```
echo 'deb [signed-by=/etc/apt/trusted.gpg.d/influxdata.gpg] https://repos.influxdata.com/debian stable main' | sudo tee /etc/apt/sources.list.d/influxdata.list
```

Update the System again:

```
sudo apt update
```

Install influxdb2 package

```
sudo apt install influxdb2
```

the database installation directory is `/usr/bin`
To run execute:

```
cd /usr/bin
./influxd
```

or use ubuntu services:

```
systemctl start influxdb
systemctl stop influxdb

systemctl enable influxdb
systemctl disable influxdb
```

### MacOS

Install InfluxDB via brew:

```
brew install influxdb
```

the database installation directory is: `/usr/local/Cellar/influxdb/2.7.1/bin`
To run execute:

```
cd /usr/local/Cellar/influxdb/2.7.1/bin
./influxd
```

Database WebInterface is available via: `http://localhost:8086`

## Install CLI

### Debian

```
apt install influxdb-client
```
the cli installation directory is `/usr/bin`

### MacOs

Install InfluxDB-CLI

```
brew install influxdb-cli
```

the cli installation directory is `/usr/local/Cellar/influxdb-cli/2.7.3/bin`

## Initial database setup

To configure InfluxDB Daemon must be running.
Start the application from the database installation directory first.

Switch to the cli installation directory and run:
```
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

## Create Operator auth token

To create operator auth token InfluxDB Daemon must be running.
Start the application from the database installation directory first.

To create Operator change to cli installation folder:

```
influx auth create \
  --operator
```

e.g. `s-kr1M8PTAYyylFNE8KJ2bG2foRf0c2_c9ECxOT1VbMeLK15bBT9xsUCglGKqcVSEZBPqrtKdVR5IpcA1L-aTA==`
e.g. `4E0csSz-_v96RE0ijgUlx2aU5aZ3psvTtXfmTUrTGZxFLKpEkG5eYq9xJW7L2tb79d7Luqk69yJsEWdsb5wmKg==`
The response contains the token.
Example:

```
ID			Description	Token					User Name	User ID			Permissions
0a9308067f303000			YcqeMSEqzrvDsPevnsPA9fuES38RU-R9_y9UZ2gefEYaNJfVdUOVdj5NvrPpwNnmVuGoZSsqZ_jxnKx9dhdHYw==	admin		0a8f348ab0c6d000	[read:/authorizations write:/authorizations read:/buckets write:/buckets read:/dashboards write:/dashboards read:/orgs write:/orgs read:/sources write:/sources read:/tasks write:/tasks read:/telegrafs write:/telegrafs read:/users write:/users read:/variables write:/variables read:/scrapers write:/scrapers read:/secrets write:/secrets read:/labels write:/labels read:/views write:/views read:/documents write:/documents read:/notificationRules write:/notificationRules read:/notificationEndpoints write:/notificationEndpoints read:/checks write:/checks read:/dbrp write:/dbrp read:/notebooks write:/notebooks read:/annotations write:/annotations read:/remotes write:/remotes read:/replications write:/replications]
```

