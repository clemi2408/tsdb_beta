package de.cleem.tub.tsdbb.apps.generator.generators.value;


import de.cleem.tub.tsdbb.api.model.GeneratorRecordConfig;
import de.cleem.tub.tsdbb.api.model.GeneratorRecordConfig.ValueTypeEnum;
import de.cleem.tub.tsdbb.commons.random.numbers.gauss.GaussGenerator;
import de.cleem.tub.tsdbb.commons.random.numbers.triangle.TriangleGenerator;
import de.cleem.tub.tsdbb.commons.random.numbers.uniform.UniformGenerator;
import de.cleem.tub.tsdbb.commons.random.strings.StringGenerator;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;

import java.util.ArrayList;
import java.util.List;

public class ValueGenerator {

    // Abstractions for specific numeric distribution Generators
    private static Number generateUniform(final GeneratorRecordConfig recordConfig, final ValueTypeEnum valueType) throws ValueGeneratorException {

        if (recordConfig == null) {
            throw new ValueGeneratorException("RecordConfig is NULL");
        }
        if (valueType == null) {
            throw new ValueGeneratorException("valueType is NULL");
        }
        if (recordConfig.getMinValue() == null) {
            throw new ValueGeneratorException("No min value provided for uniform distribution");
        }
        if (recordConfig.getMaxValue() == null) {
            throw new ValueGeneratorException("No max value provided for uniform distribution");
        }

        if (valueType == ValueTypeEnum.DOUBLE) {

            return UniformGenerator.getDouble(recordConfig.getMinValue(), recordConfig.getMaxValue());


        } else if (valueType == ValueTypeEnum.INTEGER) {

            return UniformGenerator.getInteger(recordConfig.getMinValue().intValue(), recordConfig.getMaxValue().intValue());

        }

        throw new ValueGeneratorException("Can not generate uniform distributed value of type: " + valueType);

    }

    private static Number generateTriangle(final GeneratorRecordConfig recordConfig, final ValueTypeEnum valueType) throws ValueGeneratorException {

        if (recordConfig == null) {
            throw new ValueGeneratorException("RecordConfig is NULL");
        }
        if (valueType == null) {
            throw new ValueGeneratorException("valueType is NULL");
        }
        if (recordConfig.getMinValue() == null) {
            throw new ValueGeneratorException("No min value provided for triangle distribution");
        }
        if (recordConfig.getMaxValue() == null) {
            throw new ValueGeneratorException("No max value provided for triangle distribution");
        }
        if (recordConfig.getTriangleSpike() == null) {
            throw new ValueGeneratorException("No spike value provided for triangle distribution");
        }

        if (valueType == ValueTypeEnum.DOUBLE) {
            return TriangleGenerator.getDouble(recordConfig.getMinValue(), recordConfig.getMaxValue(), recordConfig.getTriangleSpike());
        } else if (valueType == ValueTypeEnum.INTEGER) {
            return TriangleGenerator.getInteger(recordConfig.getMinValue(), recordConfig.getMaxValue(), recordConfig.getTriangleSpike());
        }

        throw new ValueGeneratorException("Can not generate triangle distributed value of type: " + valueType);

    }

    private static Number generateGauss(final GeneratorRecordConfig recordConfig, final ValueTypeEnum valueType) throws ValueGeneratorException {

        if (recordConfig == null) {
            throw new ValueGeneratorException("RecordConfig is NULL");
        }
        if (valueType == null) {
            throw new ValueGeneratorException("valueType is NULL");
        }
        if (recordConfig.getGaussMiddle() == null) {
            throw new ValueGeneratorException("No middle value provided for gauss distribution");
        }
        if (recordConfig.getGaussRange() == null) {
            throw new ValueGeneratorException("No range value provided for gauss distribution");
        }

        if (valueType == ValueTypeEnum.DOUBLE) {
            return GaussGenerator.getDouble(recordConfig.getGaussMiddle(), recordConfig.getGaussRange());

        } else if (valueType == ValueTypeEnum.INTEGER) {
            return GaussGenerator.getInteger(recordConfig.getGaussMiddle(), recordConfig.getGaussRange());

        }

        throw new ValueGeneratorException("Can not generate Gauss distributed value of type: " + valueType);

    }

    // Abstractions for numeric generation
    private static Number generateDistributedValue(final GeneratorRecordConfig recordConfig, final ValueTypeEnum valueType) throws ValueGeneratorException {

        if (recordConfig == null) {
            throw new ValueGeneratorException("RecordConfig is NULL");
        }
        if (valueType == null) {
            throw new ValueGeneratorException("valueType is NULL");
        }
        if (recordConfig.getValueDistribution() == null) {
            throw new ValueGeneratorException("No value distribution provided");
        }

        final GeneratorRecordConfig.ValueDistributionEnum valueDistribution = recordConfig.getValueDistribution();

        if (valueDistribution.equals(GeneratorRecordConfig.ValueDistributionEnum.UNIFORM)) {

            return generateUniform(recordConfig, valueType);

        } else if (valueDistribution.equals(GeneratorRecordConfig.ValueDistributionEnum.GAUSS)) {

            return generateGauss(recordConfig, valueType);

        } else if (valueDistribution.equals(GeneratorRecordConfig.ValueDistributionEnum.TRIANGLE)) {

            return generateTriangle(recordConfig, valueType);

        }

        throw new ValueGeneratorException("Can not generate value for distribution: " + valueDistribution);


    }

    // Abstractions for string generation
    private static List<String> generateStrings(final GeneratorRecordConfig recordConfig) throws ValueGeneratorException, StringGeneratorException {

        if (recordConfig == null) {
            throw new ValueGeneratorException("RecordConfig is NULL");
        }

        if (recordConfig.getStringEnumValues() != null && recordConfig.getStringEnumValues().size() > 0) {

            return recordConfig.getStringEnumValues();

        }

        if (recordConfig.getMinStringValueLength() == null) {
            throw new ValueGeneratorException("No min string value length provided");
        }
        if (recordConfig.getMaxStringValueLength() == null) {
            throw new ValueGeneratorException("No max string value length provided");
        }

        final int minStringLength = recordConfig.getMinStringValueLength();
        final int maxStringLength = recordConfig.getMaxStringValueLength();

        if (recordConfig.getMinStringEnumValues() == null && recordConfig.getMaxStringEnumValues() == null) {

            return List.of(StringGenerator.getRandomString(minStringLength, maxStringLength));

        }

        if (recordConfig.getMinStringEnumValues() == null) {
            throw new ValueGeneratorException("minStringEnumValues is NULL");
        }
        if (recordConfig.getMaxStringEnumValues() == null) {
            throw new ValueGeneratorException("maxStringEnumValues is NULL");
        }

        final int minStringEnumValues = recordConfig.getMinStringEnumValues();
        final int maxStringEnumValues = recordConfig.getMaxStringEnumValues();

        if (minStringEnumValues > maxStringEnumValues) {
            throw new ValueGeneratorException("minStringEnumValues is greater maxStringEnumValues");
        }

        final int randomStringCount = UniformGenerator.getInteger(minStringEnumValues, maxStringEnumValues);

        if (randomStringCount <= 0) {
            throw new ValueGeneratorException("randomStringCount must be greater than zero");
        }

        final List<String> outputStrings = new ArrayList<>();

        for (int i = 0; i < randomStringCount; i++) {

            outputStrings.add(StringGenerator.getRandomString(minStringLength, maxStringLength));


        }

        return outputStrings;

    }

    // Abstraction for generation
    @SuppressWarnings("rawtypes")
    public static List generate(final GeneratorRecordConfig recordConfig) throws ValueGeneratorException, StringGeneratorException {

        final GeneratorRecordConfig.ValueTypeEnum valueType = recordConfig.getValueType();

        if(valueType==null){
            throw new ValueGeneratorException("valueType is NULL");
        }

        if (valueType == ValueTypeEnum.STRING) {

            return generateStrings(recordConfig);

        } else if (valueType == ValueTypeEnum.INTEGER || valueType == ValueTypeEnum.DOUBLE) {

            return List.of(generateDistributedValue(recordConfig, valueType));

        }

        throw new ValueGeneratorException("Can not generate value for type: " + valueType);

    }


}
