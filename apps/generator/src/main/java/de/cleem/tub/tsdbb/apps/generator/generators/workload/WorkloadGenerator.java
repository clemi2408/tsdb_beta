package de.cleem.tub.tsdbb.apps.generator.generators.workload;


import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGenerator;
import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGenerator;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGeneratorException;
import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;
import de.cleem.tub.tsdbb.commons.duration.DurationException;
import de.cleem.tub.tsdbb.commons.duration.DurationHelper;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
public class WorkloadGenerator extends BaseClass {
    private GeneratorGenerateRequest generateRequest;

    private final Map<String,String> idToKeyMap = new HashMap<>();

    public static WorkloadGenerator builder() {

        return new WorkloadGenerator();
    }

    public WorkloadGenerator generateRequest(final GeneratorGenerateRequest generateRequest) {

        this.generateRequest = generateRequest;

        return this;

    }


    private KvPair generateKvPair(final GeneratorInsertConfig insertConfig) throws ValueGeneratorException, StringGeneratorException, KeyGeneratorException, WorkloadGeneratorException {

        if(insertConfig==null){
            throw new WorkloadGeneratorException("InsertConfig is NULL");
        }

        final KvPair kvPair = new KvPair();

        if(insertConfig.getIndividualKey()!=null && !insertConfig.getIndividualKey()){

            if(!idToKeyMap.containsKey(insertConfig.getId())){

                idToKeyMap.put(insertConfig.getId(),KeyGenerator.generate(insertConfig));

            }

            kvPair.setKey(idToKeyMap.get(insertConfig.getId()));

        }
        else{
            kvPair.setKey(KeyGenerator.generate(insertConfig));
        }


        kvPair.setValue(ValueGenerator.generate(insertConfig));

        return kvPair;

    }

    private Insert generateInsert(final int index, final OffsetDateTime timestamp) throws ValueGeneratorException, StringGeneratorException, KeyGeneratorException, WorkloadGeneratorException {


        if(index%100==0) {
            log.debug("Generating Insert " + index + " of " + generateRequest.getQueryConfig().getInsertQueryConfig().getInsertCount());
        }

        if(generateRequest.getInsertConfigs()==null){
            throw new WorkloadGeneratorException("InsertConfigList is NULL");
        }
        if(generateRequest.getInsertConfigs().isEmpty()){
            throw new WorkloadGeneratorException("InsertConfigList is Empty");
        }

        final List<KvPair> kvPairList = new LinkedList<>();

        for (GeneratorInsertConfig insertConfig : generateRequest.getInsertConfigs()) {

            kvPairList.add(generateKvPair(insertConfig));

        }


        return new Insert()
                .id(UUID.randomUUID())
                .timestamp(timestamp)
                .kvPairs(kvPairList);

    }

    public Workload generate() throws ValueGeneratorException, StringGeneratorException, KeyGeneratorException, WorkloadGeneratorException, DurationException {


        final GeneratorQueryConfig queryConfig = generateRequest.getQueryConfig();

        if(queryConfig==null){
            throw new WorkloadGeneratorException("queryConfig is NULL");
        }

        final GeneratorInsertQueryConfig insertQueryConfig = queryConfig.getInsertQueryConfig();

        if(insertQueryConfig==null){
            throw new WorkloadGeneratorException("insertQueryConfig is NULL");
        }

        final GeneratorSelectQueryConfig selectQueryConfig = queryConfig.getSelectQueryConfig();

        if(selectQueryConfig==null){
            throw new WorkloadGeneratorException("selectQueryConfig is NULL");
        }

        final Integer insertCount =  insertQueryConfig.getInsertCount();

        if(insertCount==null){
            throw new WorkloadGeneratorException("insertCount is NULL");

        }

        if(insertCount<=0){
            throw new WorkloadGeneratorException("insertCount is <=0");
        }

        log.info("Generating Workload with " + insertCount + " Inserts");


        return new Workload()
                .inserts(generateInserts(insertQueryConfig))
                .selects(generateSelects(selectQueryConfig));

    }

    private LinkedList<Select> generateSelects(final GeneratorSelectQueryConfig selectQueryConfig) {

        return null;

    }

    private LinkedList<Insert> generateInserts(final GeneratorInsertQueryConfig insertQueryConfig) throws DurationException, ValueGeneratorException, StringGeneratorException, WorkloadGeneratorException, KeyGeneratorException {

        final LinkedList<Insert> data = new LinkedList<>();


        OffsetDateTime timestamp = null;
        Duration duration = null;


        if(insertQueryConfig.getInsertInterval()!=null && insertQueryConfig.getStartOffsetDateTime()!=null){

            timestamp = insertQueryConfig.getStartOffsetDateTime();
            duration = DurationHelper.parseDuration(insertQueryConfig.getInsertInterval());

            log.info("Generating Insert Timestamps starting from: "+timestamp+ " with frequency "+duration);

        }


        for (int i = 1; i <= insertQueryConfig.getInsertCount(); i++) {

            data.add(generateInsert(i,timestamp));

            // Calculate next Timestamp
            if(timestamp!=null && duration!=null){

                timestamp=DurationHelper.addDuration(timestamp,duration);

            }

        }

        log.info("Generated Workload with " + data.size() + " Inserts");


        return data;

    }

}
