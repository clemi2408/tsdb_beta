package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb;

import de.cleem.tub.tsdbb.api.model.Insert;
import de.cleem.tub.tsdbb.apps.worker.adapters.InsertResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.testutil.base.BaseContainerTest;
import de.cleem.tub.tsdbb.apps.worker.adapters.victoriametrics.VictoriaMetricsAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TsdbAdapterTest extends BaseContainerTest {

    @AfterEach
    void tearDown(){
        endTest();
    }

    @ParameterizedTest
    @ValueSource(classes = { VictoriaMetricsAdapter.class, InfluxDbAdapter.class })
    void healthCheck(final Class adapterClass) throws TSDBAdapterException {

        startTest(adapterClass);

        assertTrue(tsdbAdapter.healthCheck(tsdbEndpoint));

    }

    @ParameterizedTest
    @ValueSource(classes = { VictoriaMetricsAdapter.class, InfluxDbAdapter.class })
    void setup(final Class adapterClass) throws TSDBAdapterException {

        startTest(adapterClass);

        assertTrue(tsdbAdapter.setup(workerSetupRequest));

    }

    @ParameterizedTest
    @ValueSource(classes = { InfluxDbAdapter.class })
    void createStorage(final Class adapterClass) {

        startTest(adapterClass);

        // Test is only used for InfluxDbAdapter
        //
        // Setup-Method in parent class already creates all required buckets.
        // Calling createStorage again will cause exception.
        // That's why the test is inverted.
        // This test recreates the bucket and error is expected.

        final TSDBAdapterException exception = Assertions.assertThrows(TSDBAdapterException.class, () -> {
            tsdbAdapter.createStorage(tsdbEndpoint);
        });

        assertTrue(exception.getMessage().contains("bucket with name testbucket already exists"));
    }

    @ParameterizedTest
    @ValueSource(classes = { VictoriaMetricsAdapter.class, InfluxDbAdapter.class })
    void close(final Class adapterClass) {

        startTest(adapterClass);

        assertTrue(tsdbAdapter.close());
    }

    @ParameterizedTest
    @ValueSource(classes = { VictoriaMetricsAdapter.class, InfluxDbAdapter.class })
    void cleanup(final Class adapterClass) throws TSDBAdapterException {

        startTest(adapterClass);

        assertTrue(tsdbAdapter.cleanup(tsdbEndpoint));

    }

    @ParameterizedTest
    @ValueSource(classes = { VictoriaMetricsAdapter.class, InfluxDbAdapter.class })
    void write(final Class adapterClass) throws TSDBAdapterException {

        startTest(adapterClass);

        final Insert insert = getInsert();

        final InsertResponse insertResponse = tsdbAdapter.write(insert, tsdbEndpoint);

        assertNotEquals(0, insertResponse.getRequestLength());
        assertEquals(insert.getId(), insertResponse.getInsert().getId());

    }

    /*

    //@Test
    void read() throws TSDBAdapterException {


    }

    @Test
    void getAllLabels() throws TSDBAdapterException {

        writeInserts(10);

        Select select = new Select();
        select.setStartValue("-1d");

        SelectResponse selectResponse = tsdbAdapter.getAllLabels(select, tsdbEndpoint);
        assertNotEquals(0, selectResponse.getResponseLength());

    }

    @Test
    void getSingleLabelValue() throws TSDBAdapterException {

        writeInserts(10);

        final Select select = getSelect();

        tsdbAdapter.getSingleLabelValue(select, tsdbEndpoint);

    }

    @Test
    void getMeasurementLabels() throws TSDBAdapterException {

        writeInserts(10);

        final Select select = new Select();
        select.setStartValue("-1d");
        select.setMeasurementName("INFLUX_measurement");

        tsdbAdapter.getMeasurementLabels(select, tsdbEndpoint);

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
    }*/
}