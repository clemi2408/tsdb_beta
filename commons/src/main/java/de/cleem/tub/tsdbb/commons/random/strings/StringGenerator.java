package de.cleem.tub.tsdbb.commons.random.strings;


import de.cleem.tub.tsdbb.commons.random.numbers.uniform.UniformGenerator;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class StringGenerator {

    final static Random RANDOM = new Random();

    public static String getRandomString(final int minLength, final int maxLength) throws StringGeneratorException {

        if(minLength<0){
            throw new StringGeneratorException("minLength must be >= 0");
        }
        if(maxLength<=0){
            throw new StringGeneratorException("maxLength must be > 0");

        }

        final int randomLength = UniformGenerator.getInteger(minLength, maxLength);
        return getRandomString(randomLength);

    }

    public static String getRandomString(final int length) throws StringGeneratorException {

        if(length<0){
            throw new StringGeneratorException("length is <0");
        }

        int lengthUsed = length;

        byte[] array = new byte[256];
        RANDOM.nextBytes(array);

        final String randomString = new String(array, StandardCharsets.UTF_8);

        final StringBuilder randomStringBuffer = new StringBuilder();

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
