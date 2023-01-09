package de.cleem.bm.tsdb.common.random;

import java.nio.charset.Charset;
import java.util.Random;

public class RandomHelper {

    final static Random RANDOM = new Random();
    public static int getRandomIntInRange(final int min, final int max) {

        final int maxUsed = max+1;
        return RANDOM.nextInt(maxUsed - min) + min;

    }

    public static double getRandomDoubleInRange(final double min, final double max) {

        return RANDOM.nextDouble(max - min) + min;

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
