package de.cleem.bm.tsdb.common.random;

import de.cleem.bm.tsdb.common.random.values.numbers.RandomGaussHelper;
import de.cleem.bm.tsdb.common.random.values.numbers.RandomTriangleHelper;
import de.cleem.bm.tsdb.common.random.values.numbers.RandomUniformHelper;
import de.cleem.bm.tsdb.common.random.values.strings.RandomStringHelper;
import de.cleem.bm.tsdb.model.config.datagenerator.Distribution;
import de.cleem.bm.tsdb.model.config.datagenerator.RecordConfig;
import de.cleem.bm.tsdb.model.config.datagenerator.ValueTypes;

public class ValueGenerationHelper {

    private static Number[] generateUniform(final RecordConfig recordConfig, final ValueTypes valueType) throws RandomValueGenerationException {

        if(recordConfig.getMinValue()==null){
            throw new RandomValueGenerationException("No min value provided for uniform distribution");
        }
        if(recordConfig.getMaxValue()==null){
            throw new RandomValueGenerationException("No max value provided for uniform distribution");
        }

        if(valueType == ValueTypes.DOUBLE){

            return new Number[]{RandomUniformHelper.getDouble(recordConfig.getMinValue(),recordConfig.getMaxValue())};


        }
        else if(valueType == ValueTypes.INTEGER){

            return new Number[]{RandomUniformHelper.getInteger(recordConfig.getMinValue(),recordConfig.getMaxValue())};

        }

        throw new RandomValueGenerationException("Can not generate uniform distributed value of type: "+valueType);

    }
    private static Number[] generateTriangle(final RecordConfig recordConfig, final ValueTypes valueType) throws RandomValueGenerationException {

        if(recordConfig.getMinValue()==null){
            throw new RandomValueGenerationException("No min value provided for triangle distribution");
        }
        if(recordConfig.getMaxValue()==null){
            throw new RandomValueGenerationException("No max value provided for triangle distribution");
        }
        if(recordConfig.getTriangleSpike()==null){
            throw new RandomValueGenerationException("No spike value provided for triangle distribution");
        }

        if(valueType == ValueTypes.DOUBLE){
            return new Number[]{RandomTriangleHelper.getDouble(recordConfig.getMinValue(),recordConfig.getMaxValue(),recordConfig.getTriangleSpike())};
        }
        else if(valueType == ValueTypes.INTEGER){
            return new Number[]{RandomTriangleHelper.getInteger(recordConfig.getMinValue(),recordConfig.getMaxValue(),recordConfig.getTriangleSpike())};
        }

        throw new RandomValueGenerationException("Can not generate triangle distributed value of type: "+valueType);

    }
    private static Number[] generateGauss(final RecordConfig recordConfig, final ValueTypes valueType) throws RandomValueGenerationException {

        if(recordConfig.getGaussMiddle()==null){
            throw new RandomValueGenerationException("No middle value provided for gauss distribution");
        }
        if(recordConfig.getGaussRange()==null){
            throw new RandomValueGenerationException("No range value provided for gauss distribution");
        }

        if(valueType == ValueTypes.DOUBLE){
            return new Number[]{RandomGaussHelper.getDouble(recordConfig.getGaussMiddle(), recordConfig.getGaussRange())};

        }
        else if(valueType == ValueTypes.INTEGER){
            return new Number[]{RandomGaussHelper.getInteger(recordConfig.getGaussMiddle(), recordConfig.getGaussRange())};

        }

        throw new RandomValueGenerationException("Can not generate Gauss distributed value of type: "+valueType);

    }
    private static Number[] generateDistributedValue(final RecordConfig recordConfig, final ValueTypes valueType) throws RandomValueGenerationException {

        if(recordConfig.getValueDistribution()==null){
            throw new RandomValueGenerationException("No value distribution provided");
        }

        final Distribution valueDistribution = recordConfig.getValueDistribution();

        if (valueDistribution.equals(Distribution.UNIFORM)) {

            return generateUniform(recordConfig,valueType);

        }
        else if (valueDistribution.equals(Distribution.GAUSS)) {

            return generateGauss(recordConfig,valueType);

        }
        else if (valueDistribution.equals(Distribution.TRIANGLE)) {

            return generateTriangle(recordConfig,valueType);

        }

        throw new RandomValueGenerationException("Can not generate value for distribution: "+valueDistribution);


    }
    private static String[] generateStrings(final RecordConfig recordConfig) throws RandomValueGenerationException {

        if(recordConfig.getStringEnumValues()!=null && recordConfig.getStringEnumValues().length>0){

            return recordConfig.getStringEnumValues();

        }

        if(recordConfig.getMinStringValueLength()==null){
            throw new RandomValueGenerationException("No min string value length provided");
        }
        if(recordConfig.getMaxStringValueLength()==null){
            throw new RandomValueGenerationException("No max string value length provided");
        }

        final int minStringLength = recordConfig.getMinStringValueLength();
        final int maxStringLength = recordConfig.getMaxStringValueLength();

        if(recordConfig.getMinStringEnumValues()==null && recordConfig.getMaxStringEnumValues()==null){

            return new String[]{RandomStringHelper.getRandomString(minStringLength,maxStringLength)};

        }

        final int minStringEnumValues = recordConfig.getMinStringEnumValues();
        final int maxStringEnumValues = recordConfig.getMaxStringEnumValues();

        if(minStringEnumValues>maxStringEnumValues){
            throw new RandomValueGenerationException("minStringEnumValues is greater maxStringEnumValues");
        }

        final int randomStringCount=RandomUniformHelper.getInteger(minStringEnumValues,maxStringEnumValues);

        if(randomStringCount<=0){
            throw new RandomValueGenerationException("randomStringCount must be greater than zero");
        }

        final String[] outputStrings = new String[randomStringCount];

        for(int i = 0; i < outputStrings.length; i++){

            outputStrings[i] = RandomStringHelper.getRandomString(minStringLength,maxStringLength);


        }

        return outputStrings;

    }

    public static Object[] generate(final RecordConfig recordConfig) throws RandomValueGenerationException {

        final ValueTypes valueType = ValueTypes.get(recordConfig.getValueType());

        if(valueType== ValueTypes.STRING) {

            return generateStrings(recordConfig);

        }
        else if(valueType== ValueTypes.INTEGER || valueType== ValueTypes.DOUBLE) {

            return generateDistributedValue(recordConfig,valueType);

        }

        throw new RandomValueGenerationException("Can not generate value for type: "+valueType);

    }


}
