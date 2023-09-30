package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb;

import de.cleem.tub.tsdbb.api.model.Insert;
import de.cleem.tub.tsdbb.api.model.Select;
import de.cleem.tub.tsdbb.apps.worker.adapters.InsertResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.SelectResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.base.adapter.influx.BaseInfluxTsdbAdapterTest;
import de.cleem.tub.tsdbb.commons.examplejson.ExampleDataGenerator;
import de.cleem.tub.tsdbb.commons.http.HttpException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static de.cleem.tub.tsdbb.commons.examplejson.ExampleDataGenerator.getInsert;
import static org.junit.jupiter.api.Assertions.*;

.class InfluxDbAdapterTest extends BaseInfluxTsdbAdapterTest {

    @Test
    void setup() throws TSDBAdapterException {

        assertTrue(tsdbAdapter.setup(workerSetupRequest));

    }

    @Test
    void healthCheck() throws TSDBAdapterException {

        assertTrue(tsdbAdapter.healthCheck(tsdbEndpoint));

    }

    @Test
    void createStorage_error() {
        // Setup-Method in parent class creates all required buckets.
        // calling createStorage again will cause exception.
        // Thats why the test is inverted
        // This test recreates the bucket and error is expected.

        final HttpException exception = Assertions.assertThrows(HttpException.class, () -> {
           tsdbAdapter.createStorage(tsdbEndpoint);
        });

        assertTrue(exception.getMessage().contains("bucket with name testbucket already exists"));

    }

    @Test
    void close() {

        assertTrue(tsdbAdapter.close());
    }

    @Test
    void cleanup() throws TSDBAdapterException {

        assertTrue(tsdbAdapter.cleanup(tsdbEndpoint));

    }

    @Test
    void write() throws TSDBAdapterException {

        final Insert insert = getInsert();

        final InsertResponse insertResponse = tsdbAdapter.write(insert,tsdbEndpoint);

        assertNotEquals(0, insertResponse.getRequestLength());
        assertEquals(insert.getId(),insertResponse.getInsert().getId());

    }

    //@Test
    void read() throws TSDBAdapterException {




    }

    @Test
    void getAllLabels() throws TSDBAdapterException {

        Select select = new Select();
        select.setStartValue("-1d");

        SelectResponse selectResponse = tsdbAdapter.getAllLabels(select,tsdbEndpoint);
        assertNotEquals(0, selectResponse.getResponseLength());


    }

    @Test
    void getSingleLabelValue() throws TSDBAdapterException {

        writeInserts(10);

        final Select select = getSelect();

        tsdbAdapter.getSingleLabelValue(select,tsdbEndpoint);

    }

    @Test
    void getMeasurementLabels() throws TSDBAdapterException {

        writeInserts(10);

        final Select select = new Select();
        select.setStartValue("-1d");
        select.setMeasurementName("INFLUX_measurement");

        tsdbAdapter.getMeasurementLabels(select,tsdbEndpoint);

    }

    //@Test
    void countSeries() {
    }

    //@Test
    void getAllSeries() {
    }

    //@Test
    void getMeasurementSeries() {
    }

    //@Test
    void exportSeries() {
    }

    //@Test
    void getFieldValue() {
    }

    //@Test
    void getFieldValueSum() {
    }

    //@Test
    void getFieldValueAvg() {
    }

    //@Test
    void getFieldValueMin() {
    }

    //@Test
    void getFieldValueMax() {
    }
}