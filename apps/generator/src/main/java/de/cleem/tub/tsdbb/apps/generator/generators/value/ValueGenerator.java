package de.cleem.tub.tsdbb.apps.generator.generators.value;


import de.cleem.tub.tsdbb.api.model.GeneratorInsertConfig;
import de.cleem.tub.tsdbb.api.model.GeneratorInsertConfig.ValueTypeEnum;
import de.cleem.tub.tsdbb.commons.random.numbers.gauss.GaussGenerator;
import de.cleem.tub.tsdbb.commons.random.numbers.triangle.TriangleGenerator;
import de.cleem.tub.tsdbb.commons.random.numbers.uniform.UniformGenerator;
import de.cleem.tub.tsdbb.commons.random.strings.StringGenerator;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;

import java.util.ArrayList;
import java.util.List;

public class ValueGenerator {

    // Abstractions for specific numeric distribution Generators
    private static Number generateUniform(final GeneratorInsertConfig insertConfig, final ValueTypeEnum valueType) throws ValueGeneratorException {

        if (insertConfig == null) {
            throw new ValueGeneratorException("insertConfig is NULL");
        }
        if (valueType == null) {
            throw new ValueGeneratorException("valueType is NULL");
        }
        if (insertConfig.getMinValue() == null) {
            throw new ValueGeneratorException("No min value provided for uniform distribution");
        }
        if (insertConfig.getMaxValue() == null) {
            throw new ValueGeneratorException("No max value provided for uniform distribution");
        }

        if (valueType == ValueTypeEnum.DOUBLE) {

            return UniformGenerator.getDouble(insertConfig.getMinValue(), insertConfig.getMaxValue());


        } else if (valueType == ValueTypeEnum.INTEGER) {

            return UniformGenerator.getInteger(insertConfig.getMinValue().intValue(), insertConfig.getMaxValue().intValue());

        }

        throw new ValueGeneratorException("Can not generate uniform distributed value of type: " + valueType);

    }

    private static Number generateTriangle(final GeneratorInsertConfig insertConfig, final ValueTypeEnum valueType) throws ValueGeneratorException {

        if (insertConfig == null) {
            throw new ValueGeneratorException("insertConfig is NULL");
        }
        if (valueType == null) {
            throw new ValueGeneratorException("valueType is NULL");
        }
        if (insertConfig.getMinValue() == null) {
            throw new ValueGeneratorException("No min value provided for triangle distribution");
        }
        if (insertConfig.getMaxValue() == null) {
            throw new ValueGeneratorException("No max value provided for triangle distribution");
        }
        if (insertConfig.getTriangleSpike() == null) {
            throw new ValueGeneratorException("No spike value provided for triangle distribution");
        }

        if (valueType == ValueTypeEnum.DOUBLE) {
            return TriangleGenerator.getDouble(insertConfig.getMinValue(), insertConfig.getMaxValue(), insertConfig.getTriangleSpike());
        } else if (valueType == ValueTypeEnum.INTEGER) {
            return TriangleGenerator.getInteger(insertConfig.getMinValue(), insertConfig.getMaxValue(), insertConfig.getTriangleSpike());
        }

        throw new ValueGeneratorException("Can not generate triangle distributed value of type: " + valueType);

    }

    private static Number generateGauss(final GeneratorInsertConfig insertConfig, final ValueTypeEnum valueType) throws ValueGeneratorException {

        if (insertConfig == null) {
            throw new ValueGeneratorException("insertConfig is NULL");
        }
        if (valueType == null) {
            throw new ValueGeneratorException("valueType is NULL");
        }
        if (insertConfig.getGaussMiddle() == null) {
            throw new ValueGeneratorException("No middle value provided for gauss distribution");
        }
        if (insertConfig.getGaussRange() == null) {
            throw new ValueGeneratorException("No range value provided for gauss distribution");
        }

        if (valueType == ValueTypeEnum.DOUBLE) {
            return GaussGenerator.getDouble(insertConfig.getGaussMiddle(), insertConfig.getGaussRange());

        } else if (valueType == ValueTypeEnum.INTEGER) {
            return GaussGenerator.getInteger(insertConfig.getGaussMiddle(), insertConfig.getGaussRange());

        }

        throw new ValueGeneratorException("Can not generate Gauss distributed value of type: " + valueType);

    }

    // Abstractions for numeric generation
    private static Number generateDistributedValue(final GeneratorInsertConfig insertConfig, final ValueTypeEnum valueType) throws ValueGeneratorException {

        if (insertConfig == null) {
            throw new ValueGeneratorException("insertConfig is NULL");
        }
        if (valueType == null) {
            throw new ValueGeneratorException("valueType is NULL");
        }
        if (insertConfig.getValueDistribution() == null) {
            throw new ValueGeneratorException("No value distribution provided");
        }

        final GeneratorInsertConfig.ValueDistributionEnum valueDistribution = insertConfig.getValueDistribution();

        if (valueDistribution.equals(GeneratorInsertConfig.ValueDistributionEnum.UNIFORM)) {

            return generateUniform(insertConfig, valueType);

        } else if (valueDistribution.equals(GeneratorInsertConfig.ValueDistributionEnum.GAUSS)) {

            return generateGauss(insertConfig, valueType);

        } else if (valueDistribution.equals(GeneratorInsertConfig.ValueDistributionEnum.TRIANGLE)) {

            return generateTriangle(insertConfig, valueType);

        }

        throw new ValueGeneratorException("Can not generate value for distribution: " + valueDistribution);


    }

    // Abstractions for string generation
    private static List<String> generateStrings(final GeneratorInsertConfig insertConfig) throws ValueGeneratorException, StringGeneratorException {

        if (insertConfig == null) {
            throw new ValueGeneratorException("insertConfig is NULL");
        }

        if (insertConfig.getStringEnumValues() != null && insertConfig.getStringEnumValues().size() > 0) {

            return insertConfig.getStringEnumValues();

        }

        if (insertConfig.getMinStringValueLength() == null) {
            throw new ValueGeneratorException("No min string value length provided");
        }
        if (insertConfig.getMaxStringValueLength() == null) {
            throw new ValueGeneratorException("No max string value length provided");
        }

        final int minStringLength = insertConfig.getMinStringValueLength();
        final int maxStringLength = insertConfig.getMaxStringValueLength();

        if (insertConfig.getMinStringEnumValues() == null && insertConfig.getMaxStringEnumValues() == null) {

            return List.of(StringGenerator.getRandomString(minStringLength, maxStringLength));

        }

        if (insertConfig.getMinStringEnumValues() == null) {
            throw new ValueGeneratorException("minStringEnumValues is NULL");
        }
        if (insertConfig.getMaxStringEnumValues() == null) {
            throw new ValueGeneratorException("maxStringEnumValues is NULL");
        }

        final int minStringEnumValues = insertConfig.getMinStringEnumValues();
        final int maxStringEnumValues = insertConfig.getMaxStringEnumValues();

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
    public static List generate(final GeneratorInsertConfig insertConfig) throws ValueGeneratorException, StringGeneratorException {

        final GeneratorInsertConfig.ValueTypeEnum valueType = insertConfig.getValueType();

        if(valueType==null){
            throw new ValueGeneratorException("valueType is NULL");
        }

        if (valueType == ValueTypeEnum.STRING) {

            return generateStrings(insertConfig);

        } else if (valueType == ValueTypeEnum.INTEGER || valueType == ValueTypeEnum.DOUBLE) {

            return List.of(generateDistributedValue(insertConfig, valueType));

        }

        throw new ValueGeneratorException("Can not generate value for type: " + valueType);

    }


}
