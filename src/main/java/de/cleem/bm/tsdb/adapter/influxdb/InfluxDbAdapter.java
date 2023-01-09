package de.cleem.bm.tsdb.adapter.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.Organization;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.UnauthorizedException;
import com.influxdb.exceptions.UnprocessableEntityException;
import de.cleem.bm.tsdb.adapter.common.TSDBAdapterConfig;
import de.cleem.bm.tsdb.adapter.common.TSDBAdapterException;
import de.cleem.bm.tsdb.adapter.common.TSDBAdapterIF;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class InfluxDbAdapter implements TSDBAdapterIF {

    private static final String MEASUREMENT_NAME= "influx-measurement";

    private InfluxDBClient client;

    private String organisationId;

    private InfluxDbAdapterConfig config;




    @Override
    public void createStorage() throws TSDBAdapterException {


        config.setBucketId(createStorage(config.getBucket()));

    }

    @Override
    public void close() {

        client.close();

    }

    @Override
    public void cleanup() throws TSDBAdapterException {

        if (client == null) {
            throw new TSDBAdapterException("Error closing connection - client is NULL - " + getConnectionInfo());
        }

        if(config.getBucketId()!=null) {
            deleteStorage(config.getBucketId());
        }

    }

    @Override
    public void write(final HashMap<String, Number> record) throws TSDBAdapterException {

        if (config.getBucketId() == null) {
            throw new TSDBAdapterException("Can not write to Storage - bucketId is NULL - "+getConnectionInfo());
        }

        if (record == null) {
            throw new TSDBAdapterException("Can not write to Storage - record is NULL - "+getConnectionInfo());
        }

        if (record.size() == 0) {
            throw new TSDBAdapterException("Can not write to Storage - record is Empty - "+getConnectionInfo());
        }

        if (client == null) {
            throw new TSDBAdapterException("Can not write to Storage " + config.getBucketId() + " - client is NULL - "+getConnectionInfo());
        }

        final Point point = Point.measurement(MEASUREMENT_NAME)
                .time(Instant.now().toEpochMilli(), WritePrecision.MS);

        for(String key : record.keySet()){

            point.addField(key, record.get(key));

        }


        try {
            client.getWriteApiBlocking().writePoint(point);
        }
        catch (Exception e){
            throw new TSDBAdapterException(e);

        }

        log.info("Wrote Data: "+config.getBucketId()+" - "+getConnectionInfo()+" - "+record.toString());

    }

    ////
    private String getConnectionInfo() {

        final StringBuffer infoBuffer = new StringBuffer();

        if(config.getInfluxDbUrl()!=null){
            infoBuffer.append(config.getInfluxDbUrl());
        }
        if(config.getOrganisation()!=null){
            infoBuffer.append(" Organisation: "+config.getOrganisation());
        }
        if(organisationId!=null){
            infoBuffer.append(" ("+organisationId+")");
        }
        if(config.getBucket()!=null){
            infoBuffer.append(" Bucket: "+config.getBucket());
        }
        if(config.getBucketId()!=null){
            infoBuffer.append(" ("+config.getBucketId()+")");
        }


        return infoBuffer.toString();
    }

    public void setup(final TSDBAdapterConfig tsdbAdapterConfig) throws TSDBAdapterException {

        if(!(tsdbAdapterConfig instanceof InfluxDbAdapterConfig)){

            throw new TSDBAdapterException("Setup error - tsdbConfig is not instance of InfluxDbAdapterConfig - " + getConnectionInfo());

        }

        config = (InfluxDbAdapterConfig) tsdbAdapterConfig;


        final InfluxDBClientOptions.Builder clientOptionsBuilder = InfluxDBClientOptions.builder();

        clientOptionsBuilder.url(config.getInfluxDbUrl());
        clientOptionsBuilder.authenticate(config.getUsername(), config.getPassword().toCharArray());
        clientOptionsBuilder.org(config.getOrganisation());

        if(config.getBucketId()!=null){
            clientOptionsBuilder.bucket(config.getBucket());
        }

        client = InfluxDBClientFactory.create(clientOptionsBuilder.build());

        try {

            organisationId = getOrgId(config.getOrganisation());


        } catch (UnauthorizedException e) {
            throw new TSDBAdapterException(e);
        }

    }

    private String createStorage(final String storageName) throws TSDBAdapterException {

        log.info("Creating Storage: "+storageName +" - "+getConnectionInfo());

        if (storageName == null) {
            throw new TSDBAdapterException("Can not create Storage - storageName is NULL - "+getConnectionInfo());
        }

        if (client == null) {
            throw new TSDBAdapterException("Can not create Storage " + storageName + " - client is NULL - "+getConnectionInfo());
        }

        final Bucket newBucket = new Bucket();
        newBucket.setName(storageName);
        newBucket.setOrgID(organisationId);

        Bucket createdBucket = null;
        try {
            createdBucket = client.getBucketsApi().createBucket(newBucket);

            if (createdBucket == null) {
                throw new TSDBAdapterException("Error creating Storage " + storageName + " - "+getConnectionInfo());
            }

            log.info("Created Storage " + storageName + " ("+createdBucket.getId()+") - "+getConnectionInfo());

            return createdBucket.getId();


        } catch (UnprocessableEntityException e) {

            throw new TSDBAdapterException(e);

        }


    }

    private void deleteStorage(final String storageId) throws TSDBAdapterException {

        log.info("Deleting Storage: "+storageId+" - "+getConnectionInfo());

        if (storageId == null) {
            throw new TSDBAdapterException("Can not delete Storage - storageId is NULL - "+getConnectionInfo());
        }

        if (client == null) {
            throw new TSDBAdapterException("Can not delete Storage " + storageId + " - client is NULL - "+getConnectionInfo());
        }


        client.getBucketsApi().deleteBucket(storageId);

    }

    private String getOrgId(final String organisationName) throws TSDBAdapterException {

        if (client == null) {
            throw new TSDBAdapterException("Can not lookup orgId for name " + organisationName + " - client is NULL - "+getConnectionInfo());
        }

        List<Organization> organisations = null;
        try {
            organisations = client.getOrganizationsApi().findOrganizations();
        } catch (UnauthorizedException e) {
            throw new TSDBAdapterException(e);
        }


        if (organisations != null) {

            for (Organization currentOrg : organisations) {

                if (currentOrg.getName().equals(organisationName)) {

                    return currentOrg.getId();

                }

            }

        }

        throw new TSDBAdapterException("Can not lookup orgId for name " + organisationName);

    }

    /////



}
