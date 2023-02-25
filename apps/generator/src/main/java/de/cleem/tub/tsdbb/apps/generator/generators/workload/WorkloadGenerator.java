package de.cleem.tub.tsdbb.apps.generator.generators.workload;


import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGenerator;
import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGenerator;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGeneratorException;
import de.cleem.tub.tsdbb.commons.model.generator.RecordConfig;
import de.cleem.tub.tsdbb.commons.model.generator.GeneratorConfig;
import de.cleem.tub.tsdbb.commons.model.workload.KvPair;
import de.cleem.tub.tsdbb.commons.model.workload.Workload;
import de.cleem.tub.tsdbb.commons.model.workload.Record;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class WorkloadGenerator {
    private GeneratorConfig config;

    public WorkloadGenerator() {

        log.info("Constructing: " + this.getClass().getSimpleName());
        config = new GeneratorConfig();

    }

    public static WorkloadGenerator builder() {

        return new WorkloadGenerator();
    }

    public WorkloadGenerator config(final GeneratorConfig generatorConfig) {

        config = generatorConfig;

        return this;

    }

    private KvPair generateKvPair(final RecordConfig recordConfig) throws ValueGeneratorException, StringGeneratorException, KeyGeneratorException, WorkloadGeneratorException {

        if(recordConfig==null){
            throw new WorkloadGeneratorException("RecordConfig is NULL");
        }

        return KvPair.builder()
                .key(KeyGenerator.generate(recordConfig))
                .value(ValueGenerator.generate(recordConfig))
                .build();

    }

    private Record generateRecord(final int index) throws ValueGeneratorException, StringGeneratorException, KeyGeneratorException, WorkloadGeneratorException {


        if(index%100==0) {
            log.info("Generating Record " + index + " of " + config.getRecordCount());
        }

        if(config.getRecordConfigs()==null){
            throw new WorkloadGeneratorException("RecordConfigList is NULL");
        }
        if(config.getRecordConfigs().isEmpty()){
            throw new WorkloadGeneratorException("RecordConfigList is Empty");
        }

        final List<KvPair> kvPairList = new LinkedList<>();

        for (RecordConfig recordConfig : config.getRecordConfigs()) {

            kvPairList.add(generateKvPair(recordConfig));

        }


        return Record.builder()
                .recordId(UUID.randomUUID())
                .kvPairs(kvPairList)
                .build();

    }

    public Workload generate() throws ValueGeneratorException, StringGeneratorException, KeyGeneratorException, WorkloadGeneratorException {

        log.info("Generating Workload with " + config.getRecordCount() + " Records");

        if(config.getRecordCount()==null){
            throw new WorkloadGeneratorException("RecordCount is NULL");
        }

        if(config.getRecordCount()<=0){
            throw new WorkloadGeneratorException("RecordCount is <=0");
        }

        final LinkedList<Record> data = new LinkedList<>();

        for (int i = 1; i <= config.getRecordCount(); i++) {

            data.add(generateRecord(i));

        }

        final Workload workload = Workload.builder().build();
        workload.setRecords(data);

        return workload;

    }

}
