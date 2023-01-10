package de.cleem.bm.tsdb.common.date;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class DateHelper {

    private static final String FILE_TIMESTAMP_PATTERN = "yyyyMMddHHmmSS";

    public static String formatDate(final Date date, final String pattern) throws DateException {

        if(date==null){
            throw new DateException("Date to format is NULL");
        }
        else if(pattern==null){
            throw new DateException("Pattern to format Date is NULL");
        }

        return new SimpleDateFormat(pattern).format(date);

    }

    public static String getTimestamp(final Date date) throws DateException {

        return formatDate(date, FILE_TIMESTAMP_PATTERN);

    }

    public static String getTimestamp() throws DateException {
        return getTimestamp(new Date());
    }




}
