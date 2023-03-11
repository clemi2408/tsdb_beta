package de.cleem.tub.tsdbb.apps.generator.generators.key;




import de.cleem.tub.tsdbb.api.model.RecordConfig;
import de.cleem.tub.tsdbb.commons.random.strings.StringGenerator;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyGenerator {

    public static String generate(final RecordConfig recordConfig) throws StringGeneratorException, KeyGeneratorException {

        if(recordConfig==null){
            throw new KeyGeneratorException("Record config is NULL");
        }

        if (recordConfig.getKeyValue() != null) {

            log.debug("Using configured Key " + recordConfig.getKeyValue());

            return recordConfig.getKeyValue();
        }

        if(recordConfig.getMinKeyLength()==null){
            throw new KeyGeneratorException("Record config minKeyLength is NULL");
        }

        if(recordConfig.getMaxKeyLength()==null){
            throw new KeyGeneratorException("Record config maxKeyLength is NULL");
        }

        final String generatedString = StringGenerator.getRandomString(recordConfig.getMinKeyLength(), recordConfig.getMaxKeyLength());
        log.debug("Generated random Key '" + generatedString + "' with length between: " + recordConfig.getMinKeyLength() + "-" + recordConfig.getMaxKeyLength());

        return generatedString;


    }
}
