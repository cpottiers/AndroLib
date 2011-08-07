package com.cyrilpottiers.androlib.date;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateFormatter {
    /**
     * All minutes have this many milliseconds except the last minute of the day
     * on a day defined with
     * a leap second.
     */
    public static final long        MILLISECS_PER_MINUTE = 60 * 1000;

    /**
     * Number of milliseconds per hour, except when a leap second is inserted.
     */
    public static final long        MILLISECS_PER_HOUR   = 60 * MILLISECS_PER_MINUTE;

    /**
     * Number of leap seconds per day expect on <BR/>
     * 1. days when a leap second has been inserted, e.g. 1999 JAN 1. <BR/>
     * 2. Daylight-savings "spring forward" or "fall back" days.
     */
    protected static final long     MILLISECS_PER_DAY    = 24 * MILLISECS_PER_HOUR;

    /** format : yyyy-MM-ddTHH:mm:ssZ **/
    private static SimpleDateFormat format               = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static String getCurrentDate() {
        return getCurrentDate(format);
    }

    public static String getCurrentDate(SimpleDateFormat format) {
        return format.format(new Date(System.currentTimeMillis()));
    }

    public static String format(Date date) {
        return format(format, date);
    }

    public static String format(SimpleDateFormat format, Date date) {
        try {
            return format.format(date);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getDate(String sDate) {
        return getDate(format, sDate);
    }

    public static Date getDate(SimpleDateFormat format, String sDate) {
        try {
            return format.parse(sDate);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GregorianCalendar getCalendar(String sDate) {
        return getCalendar(format, sDate);
    }

    public static GregorianCalendar getCalendar(SimpleDateFormat format,
            String sDate) {
        try {
            Date date = format.parse(sDate);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            return cal;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getDiffMillis(Date end, Date start) {
        GregorianCalendar endCal = new GregorianCalendar();
        endCal.setTime(end);
        GregorianCalendar startCal = new GregorianCalendar();
        startCal.setTime(start);
        long endT = endCal.getTimeInMillis() + endCal.getTimeZone().getOffset(endCal.getTimeInMillis());
        long startT = startCal.getTimeInMillis() + startCal.getTimeZone().getOffset(startCal.getTimeInMillis());
        return endT - startT;
    }

    public static int getDiffDays(Date end, Date start) {
        return (int) (getDiffMillis(end, start) / MILLISECS_PER_DAY);
    }
}
