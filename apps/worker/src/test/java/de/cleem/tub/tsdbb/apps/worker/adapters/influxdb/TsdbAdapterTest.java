package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb;

import de.cleem.tub.tsdbb.api.model.Insert;
import de.cleem.tub.tsdbb.api.model.Select;
import de.cleem.tub.tsdbb.apps.worker.adapters.InsertResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.SelectResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.testutil.base.BaseContainerTest;
import de.cleem.tub.tsdbb.apps.worker.adapters.victoriametrics.VictoriaMetricsAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TsdbAdapterTest extends BaseContainerTest {

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_healthCheck_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);

        assertTrue(adapter.healthCheck(endpoint));

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_setup_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);

        assertTrue(adapter.setup(workerSetupRequest));

    }

    @ParameterizedTest
    @ValueSource(classes = {InfluxDbAdapter.class})
    void test_createStorage_error(final Class<?> adapterClass) {

        initialize(adapterClass);

        // Test is only used for InfluxDbAdapter
        //
        // Setup-Method in parent class already creates all required buckets.
        // Calling createStorage again will cause exception.
        // That's why the test is inverted.
        // This test recreates the bucket and error is expected.

        final TSDBAdapterException exception = Assertions.assertThrows(TSDBAdapterException.class, () -> {
            adapter.createStorage(endpoint);
        });

        assertTrue(exception.getMessage().contains("bucket with name testbucket already exists"));

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_close_ok(final Class<?> adapterClass) {

        initialize(adapterClass);

        assertTrue(adapter.close());
    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_cleanup_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);

        assertTrue(adapter.cleanup(endpoint));

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_write_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);

        final Insert insert = getInsert();

        final InsertResponse insertResponse = adapter.write(insert, endpoint);

        assertNotEquals(0, insertResponse.getRequestLength());
        assertEquals(insert.getId(), insertResponse.getInsert().getId());

    }

    /*
    //@Test
    void test_read_ok(final Class<?> adapterClass) throws TSDBAdapterException {

    }
    */

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getAllLabels_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();

        SelectResponse selectResponse = adapter.getAllLabels(select, endpoint);
        assertNotEquals(0, selectResponse.getResponseLength());

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getSingleLabelValue_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setLabelName(DEFAULT_LABEL_NAME);

        adapter.getSingleLabelValue(select, endpoint);

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getMeasurementLabels_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());

        adapter.getMeasurementLabels(select, endpoint);

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_countSeries_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();

        adapter.countSeries(select,endpoint);

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getAllSeries_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();

        adapter.getAllSeries(select,endpoint);

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getMeasurementSeries_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());

        adapter.getMeasurementSeries(select,endpoint);

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_exportSeries_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);

        /// TODO: AHHH INFLUX WTF

        adapter.exportSeries(select,endpoint);

    }


    /*

    //@Test
    void test_getFieldValue_ok() {
    }

    //@Test
    void test_getFieldValueSum_ok() {
    }

    //@Test
    void test_getFieldValueAvg_ok() {
    }

    //@Test
    void test_getFieldValueMin_ok() {
    }

    //@Test
    void test_getFieldValueMax_ok() {
    }*/
}