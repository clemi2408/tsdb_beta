package de.cleem.tub.tsdbb.commons.date;

import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper extends BaseClass {

    private static final String FILE_TIMESTAMP_PATTERN = "yyyyMMddHHmmSS";

    public static String formatDate(final Date date, final String pattern) throws DateException {

        if (date == null) {
            throw new DateException("Date to format is NULL");
        } else if (pattern == null) {
            throw new DateException("Pattern to format Date is NULL");
        }

        return new SimpleDateFormat(pattern).format(date);

    }

    public static String getFormattedDateString(final Date date) throws DateException {

        return formatDate(date, FILE_TIMESTAMP_PATTERN);

    }

    public static String getCurrentFormattedDateString() throws DateException {
        return getFormattedDateString(new Date());
    }

    public static long[] getDateDifference(final Date endDate, final Date startDate) throws DateException {

        // http://technojeeves.com/joomla/index.php/free/71-difference-between-two-dates-in-java

        if (startDate == null) {
            throw new DateException("Start date is NULL");
        }
        if (endDate == null) {
            throw new DateException("Stop date is NULL");
        }

        final long[] result = new long[5];
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTime(endDate);

        final long t1 = cal.getTimeInMillis();
        cal.setTime(startDate);

        long diff = Math.abs(cal.getTimeInMillis() - t1);
        final int ONE_DAY = 1000 * 60 * 60 * 24;
        final int ONE_HOUR = ONE_DAY / 24;
        final int ONE_MINUTE = ONE_HOUR / 60;
        final int ONE_SECOND = ONE_MINUTE / 60;

        final long d = diff / ONE_DAY;
        diff %= ONE_DAY;

        final long h = diff / ONE_HOUR;
        diff %= ONE_HOUR;

        final long m = diff / ONE_MINUTE;
        diff %= ONE_MINUTE;

        final long s = diff / ONE_SECOND;
        final long ms = diff % ONE_SECOND;
        result[0] = d;
        result[1] = h;
        result[2] = m;
        result[3] = s;
        result[4] = ms;

        return result;
    }

    public static String getDateDifferenceString(final Date endDate, final Date startDate) throws DateException {

        final long[] diffs = getDateDifference(endDate,startDate);

        if(diffs.length!=5){
            throw new DateException("Can not format Date Difference - invalid array length");
        }

        return  diffs[0] + " Days " +
                diffs[1] + " Hours " +
                diffs[2] + " Minutes " +
                diffs[3] + " Seconds " +
                diffs[4] + " Milliseconds";

    }


}
