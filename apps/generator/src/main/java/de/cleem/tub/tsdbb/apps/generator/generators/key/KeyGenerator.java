package de.cleem.tub.tsdbb.apps.generator.generators.key;




import de.cleem.tub.tsdbb.api.model.GeneratorInsertConfig;
import de.cleem.tub.tsdbb.commons.random.strings.StringGenerator;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyGenerator {

    public static String generate(final GeneratorInsertConfig insertConfig) throws StringGeneratorException, KeyGeneratorException {

        if(insertConfig==null){
            throw new KeyGeneratorException("Insert config is NULL");
        }

        if (insertConfig.getKeyValue() != null) {

            log.debug("Using configured Key " + insertConfig.getKeyValue());

            return insertConfig.getKeyValue();
        }

        if(insertConfig.getMinKeyLength()==null){
            throw new KeyGeneratorException("Insert config minKeyLength is NULL");
        }

        if(insertConfig.getMaxKeyLength()==null){
            throw new KeyGeneratorException("Insert config maxKeyLength is NULL");
        }

        final String generatedString = StringGenerator.getRandomString(insertConfig.getMinKeyLength(), insertConfig.getMaxKeyLength());
        log.debug("Generated random Key '" + generatedString + "' with length between: " + insertConfig.getMinKeyLength() + "-" + insertConfig.getMaxKeyLength());

        return generatedString;


    }
}
