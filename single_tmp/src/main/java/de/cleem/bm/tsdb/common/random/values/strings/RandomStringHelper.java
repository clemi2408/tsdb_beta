package de.cleem.bm.tsdb.common.random.values.strings;

import de.cleem.bm.tsdb.common.random.values.numbers.RandomUniformHelper;

import java.nio.charset.Charset;
import java.util.Random;

public class RandomStringHelper {

    final static Random RANDOM = new Random();

    public static String getRandomString(final int minLength, final int maxLength) {

        final int randomLength = RandomUniformHelper.getInteger(minLength, maxLength);
        return getRandomString(randomLength);


    }

    public static String getRandomString(final int length) {

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
