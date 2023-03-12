package de.cleem.tub.tsdbb.apps.generator.generators.workload;


import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGenerator;
import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGenerator;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGeneratorException;
import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class WorkloadGenerator extends BaseClass {
    private GeneratorGenerateRequest generateRequest;


    public static WorkloadGenerator builder() {

        return new WorkloadGenerator();
    }

    public WorkloadGenerator generateRequest(final GeneratorGenerateRequest generateRequest) {

        this.generateRequest = generateRequest;

        return this;

    }

    private KvPair generateKvPair(final GeneratorRecordConfig recordConfig) throws ValueGeneratorException, StringGeneratorException, KeyGeneratorException, WorkloadGeneratorException {

        if(recordConfig==null){
            throw new WorkloadGeneratorException("RecordConfig is NULL");
        }

        return new KvPair()
                .key(KeyGenerator.generate(recordConfig))
                .value(ValueGenerator.generate(recordConfig));

    }

    private Record generateRecord(final int index) throws ValueGeneratorException, StringGeneratorException, KeyGeneratorException, WorkloadGeneratorException {


        if(index%100==0) {
            log.debug("Generating Record " + index + " of " + generateRequest.getRecordCount());
        }

        if(generateRequest.getRecordConfigs()==null){
            throw new WorkloadGeneratorException("RecordConfigList is NULL");
        }
        if(generateRequest.getRecordConfigs().isEmpty()){
            throw new WorkloadGeneratorException("RecordConfigList is Empty");
        }

        final List<KvPair> kvPairList = new LinkedList<>();

        for (GeneratorRecordConfig recordConfig : generateRequest.getRecordConfigs()) {

            kvPairList.add(generateKvPair(recordConfig));

        }


        return new Record()
                .recordId(UUID.randomUUID())
                .kvPairs(kvPairList);

    }

    public Workload generate() throws ValueGeneratorException, StringGeneratorException, KeyGeneratorException, WorkloadGeneratorException {

        log.info("Generating Workload with " + generateRequest.getRecordCount() + " Records");

        if(generateRequest.getRecordCount()==null){
            throw new WorkloadGeneratorException("RecordCount is NULL");
        }

        if(generateRequest.getRecordCount()<=0){
            throw new WorkloadGeneratorException("RecordCount is <=0");
        }

        final LinkedList<Record> data = new LinkedList<>();

        for (int i = 1; i <= generateRequest.getRecordCount(); i++) {

            data.add(generateRecord(i));

        }

        log.info("Generated Workload with " + data.size() + " Records");

        return new Workload().records(data);

    }

}
