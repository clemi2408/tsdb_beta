package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb;

import de.cleem.tub.tsdbb.api.model.Insert;
import de.cleem.tub.tsdbb.api.model.Select;
import de.cleem.tub.tsdbb.apps.worker.adapters.InsertResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.SelectResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.base.adapter.victoria.BaseVictoriaTsdbAdapterTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VictoriaMetricsAdapterTest extends BaseVictoriaTsdbAdapterTest {

    @Test
    void setup() throws TSDBAdapterException {

        assertTrue(tsdbAdapter.setup(workerSetupRequest));

    }

    @Test
    void healthCheck() throws TSDBAdapterException {

        assertTrue(tsdbAdapter.healthCheck(tsdbEndpoint));

    }

    @Disabled
    void createStorage() {

        // tsdbAdapter.createStorage(tsdbEndpoint) not implemented for victoria

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

        final InsertResponse insertResponse = tsdbAdapter.write(insert, tsdbEndpoint);

        assertNotEquals(0, insertResponse.getRequestLength());
        assertEquals(insert.getId(), insertResponse.getInsert().getId());

    }

    //@Test
    void read() throws TSDBAdapterException {


    }

    @Test
    void getAllLabels() throws TSDBAdapterException, InterruptedException {

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
    }
}