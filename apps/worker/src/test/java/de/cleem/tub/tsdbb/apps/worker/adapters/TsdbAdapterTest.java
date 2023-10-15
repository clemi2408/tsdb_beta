package de.cleem.tub.tsdbb.apps.worker.adapters;

import de.cleem.tub.tsdbb.api.model.Insert;
import de.cleem.tub.tsdbb.api.model.Select;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.InfluxDbAdapter;
import de.cleem.tub.tsdbb.apps.worker.adapters.testutil.base.BaseContainerTest;
import de.cleem.tub.tsdbb.apps.worker.adapters.testutil.base.RepeatedSelect;
import de.cleem.tub.tsdbb.apps.worker.adapters.testutil.base.RepeatedSelect.SelectCall;
import de.cleem.tub.tsdbb.apps.worker.adapters.victoriametrics.VictoriaMetricsAdapter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
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

        final SelectResponse selectResponse = adapter.getSingleLabelValue(select, endpoint);

        assertNotEquals(0, selectResponse.getResponseLength());

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getMeasurementLabels_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());

        final SelectResponse selectResponse = adapter.getMeasurementLabels(select, endpoint);

        assertNotEquals(0, selectResponse.getResponseLength());

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_countSeries_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();

        final SelectResponse selectResponse = adapter.countSeries(select, endpoint);

        assertNotEquals(0, selectResponse.getResponseLength());

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getAllSeries_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();

        final SelectResponse selectResponse = adapter.getAllSeries(select, endpoint);
        assertNotEquals(0, selectResponse.getResponseLength());

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getMeasurementSeries_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());

        final SelectResponse selectResponse = adapter.getMeasurementSeries(select, endpoint);
        assertNotEquals(0, selectResponse.getResponseLength());

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_exportSeries_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);

        final SelectResponse selectResponse = adapter.exportSeries(select, endpoint);

        assertNotEquals(0, selectResponse.getResponseLength());


    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getFieldValue_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);

        // It seems like victoria needs some time to make data available :(
        if (adapterClass == VictoriaMetricsAdapter.class) {
            sleep(30000);
        }

        final SelectResponse selectResponse = adapter.getFieldValue(select, endpoint);

        assertNotEquals(0, selectResponse.getResponseLength());

    }

    @Test
    void test_victoria_getFieldValue_availability() throws TSDBAdapterException {

        initialize(VictoriaMetricsAdapter.class);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);

        new RepeatedSelect(120,1,"\"seriesFetched\": \"1\"")
                .execute(new SelectCall() {
                        @Override
                        public SelectResponse select() throws TSDBAdapterException {
                            return adapter.getFieldValue(select, endpoint);
                        }
                    }
                );

    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getFieldValueSum_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);
        select.setStepValue("1m");

        // It seems like victoria needs some time to make data available :(
        if (adapterClass == VictoriaMetricsAdapter.class) {
            sleep(120000);
        }

        final SelectResponse selectResponse = adapter.getFieldValueSum(select, endpoint);

        assertNotEquals(0, selectResponse.getResponseLength());

    }
    @Test
    void test_victoria_getFieldValueSum_availability() throws TSDBAdapterException {

        initialize(VictoriaMetricsAdapter.class);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);
        select.setStepValue("1m");

        new RepeatedSelect(120,1,"\"seriesFetched\": \"1\"")
                .execute(new SelectCall() {
                             @Override
                             public SelectResponse select() throws TSDBAdapterException {
                                 return adapter.getFieldValueSum(select,endpoint);
                             }
                         }
                );
    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getFieldValueAvg_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);
        select.setStepValue("1m");

        // It seems like victoria needs some time to make data available :(
        if (adapterClass == VictoriaMetricsAdapter.class) {
            sleep(120000);
        }

        final SelectResponse selectResponse = adapter.getFieldValueAvg(select, endpoint);

        assertNotEquals(0, selectResponse.getResponseLength());

    }

    @Test
    void test_victoria_getFieldValueAvg_availability() throws TSDBAdapterException {

        initialize(VictoriaMetricsAdapter.class);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);
        select.setStepValue("1m");

        new RepeatedSelect(120,1,"\"seriesFetched\": \"1\"")
                .execute(new SelectCall() {
                             @Override
                             public SelectResponse select() throws TSDBAdapterException {
                                 return adapter.getFieldValueAvg(select,endpoint);
                             }
                         }
                );
    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getFieldValueMin_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);
        select.setStepValue("1m");

        // It seems like victoria needs some time to make data available :(
        if (adapterClass == VictoriaMetricsAdapter.class) {
            sleep(120000);
        }

        final SelectResponse selectResponse = adapter.getFieldValueMin(select, endpoint);

        assertNotEquals(0, selectResponse.getResponseLength());

    }
    @Test
    void test_victoria_getFieldValueMin_availability() throws TSDBAdapterException {

        initialize(VictoriaMetricsAdapter.class);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);
        select.setStepValue("1m");

        new RepeatedSelect(120,1,"\"seriesFetched\": \"1\"")
                .execute(new SelectCall() {
                             @Override
                             public SelectResponse select() throws TSDBAdapterException {
                                 return adapter.getFieldValueMin(select,endpoint);
                             }
                         }
                );
    }

    @ParameterizedTest
    @ValueSource(classes = {VictoriaMetricsAdapter.class, InfluxDbAdapter.class})
    void test_getFieldValueMax_ok(final Class<?> adapterClass) throws TSDBAdapterException {

        initialize(adapterClass);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);
        select.setStepValue("1m");

        // It seems like victoria needs some time to make data available :(
        if (adapterClass == VictoriaMetricsAdapter.class) {
            sleep(120000);
        }

        final SelectResponse selectResponse = adapter.getFieldValueMax(select, endpoint);

        assertNotEquals(0, selectResponse.getResponseLength());

    }
    @Test
    void test_victoria_getFieldValueMax_availability() throws TSDBAdapterException {

        initialize(VictoriaMetricsAdapter.class);
        insert();

        final Select select = getSelect();
        select.setMeasurementName(getMeasurementName());
        select.setFieldName(DEFAULT_FIELD_NAME);
        select.setStepValue("1m");

        new RepeatedSelect(120,1,"\"seriesFetched\": \"1\"")
                .execute(new SelectCall() {
                             @Override
                             public SelectResponse select() throws TSDBAdapterException {
                                 return adapter.getFieldValueMax(select,endpoint);
                             }
                         }
                );
    }

}