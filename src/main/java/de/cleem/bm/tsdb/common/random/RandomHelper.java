package de.cleem.bm.tsdb.common.random;

import de.cleem.bm.tsdb.common.random.distributed.RandomGaussHelper;
import de.cleem.bm.tsdb.common.random.distributed.RandomTriangleHelper;
import de.cleem.bm.tsdb.common.random.distributed.RandomUniformHelper;
import de.cleem.bm.tsdb.model.config.datagenerator.Distribution;
import de.cleem.bm.tsdb.model.config.datagenerator.RecordConfig;

import java.nio.charset.Charset;
import java.util.Random;

public class RandomHelper {

    final static Random RANDOM = new Random();

    public static Number generateRandomValue(final RecordConfig recordConfig) throws RandomValueGenerationException {

        final Distribution distribution = recordConfig.getValueDistribution();

        if(distribution.equals(Distribution.UNIFORM)){

            if(recordConfig.getMinValue()==null){
                throw new RandomValueGenerationException("No min value provided for uniform distribution");
            }
            if(recordConfig.getMaxValue()==null){
                throw new RandomValueGenerationException("No max value provided for uniform distribution");
            }

            if(recordConfig.getValueType()==Double.class){
                return RandomUniformHelper.getDouble(recordConfig.getMinValue(),recordConfig.getMaxValue());
            }
            else if(recordConfig.getValueType()==Integer.class){
                return RandomUniformHelper.getInteger(recordConfig.getMinValue(),recordConfig.getMaxValue());
            }

        }
        if(distribution.equals(Distribution.TRIANGLE)){

            if(recordConfig.getMinValue()==null){
                throw new RandomValueGenerationException("No min value provided for triangle distribution");
            }
            if(recordConfig.getMaxValue()==null){
                throw new RandomValueGenerationException("No max value provided for triangle distribution");
            }
            if(recordConfig.getTriangleSpike()==null){
                throw new RandomValueGenerationException("No spike value provided for triangle distribution");
            }

            if(recordConfig.getValueType()==Double.class){
                return RandomTriangleHelper.getDouble(recordConfig.getMinValue(),recordConfig.getMaxValue(),recordConfig.getTriangleSpike());
            }
            else if(recordConfig.getValueType()==Integer.class){
                return RandomTriangleHelper.getInteger(recordConfig.getMinValue(),recordConfig.getMaxValue(),recordConfig.getTriangleSpike());
            }

        }
        if(distribution.equals(Distribution.GAUSS)){

            if(recordConfig.getGaussMiddle()==null){
                throw new RandomValueGenerationException("No middle value provided for gauss distribution");
            }
            if(recordConfig.getGaussRange()==null){
                throw new RandomValueGenerationException("No range value provided for gauss distribution");
            }

            if(recordConfig.getValueType()==Double.class){
                return RandomGaussHelper.getDouble(recordConfig.getGaussMiddle(), recordConfig.getGaussRange());
            }
            else if(recordConfig.getValueType()==Integer.class){
                return RandomGaussHelper.getInteger(recordConfig.getGaussMiddle(), recordConfig.getGaussRange());
            }

        }

        throw new RandomValueGenerationException("Can not generate value for type: "+recordConfig.getValueType().getSimpleName());
    }

    public static String getRandomString(final int minLength, final int maxLength){

        final int randomLength = RandomUniformHelper.getInteger(minLength,maxLength);
        return RandomHelper.getRandomString(randomLength);


    }

    public static String getRandomString(final int length)
    {

        int lengthUsed = length;

        byte[] array = new byte[256];
        RANDOM.nextBytes(array);

        final String randomString = new String(array, Charset.forName("UTF-8"));

        final StringBuffer randomStringBuffer = new StringBuffer();

        for (int k = 0; k < randomString.length(); k++) {

            char ch = randomString.charAt(k);

            if (((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9'))
                    && (lengthUsed > 0)) {

                randomStringBuffer.append(ch);
                lengthUsed--;
            }
        }

        return randomStringBuffer.toString();
    }
}
