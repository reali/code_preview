package com.pa3424.app.stabs1.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ali on 12/5/19.
 */
public class DateUtils {

    public static String fromDateToUserReadableString(Date date) {

        if (date == null)
            return "";

        String pattern = "";

        if (date.getMinutes() == 0) {
            pattern = "MMM d, yyyy @ h aaa";
        } else {
            pattern = "MMM d, yyyy @ h:mm aaa";
        }

        SimpleDateFormat dateFromat_user = new SimpleDateFormat(pattern, Locale.US);

        return dateFromat_user.format(date);
    }

    public static String fromDateToUserReadableStringFitbit(Date date) {

        if (date == null)
            return "";

        String pattern = "MM/dd/yy HH:mm";

        SimpleDateFormat dateFromat_user = new SimpleDateFormat(pattern, Locale.US);

        return dateFromat_user.format(date);
    }

    public static String fromDateToUserReadableHoursString(Date date) {

        if (date == null)
            return "";

        String pattern = "H:mm";

        SimpleDateFormat dateFromat_user = new SimpleDateFormat(pattern, Locale.US);

        return dateFromat_user.format(date);
    }

    public static String fromDateToUserReadableHoursString2(Date date) {

        if (date == null)
            return "";

        String pattern = "h:mm aaa";

        SimpleDateFormat dateFromat_user = new SimpleDateFormat(pattern, Locale.US);

        return dateFromat_user.format(date);
    }

    public static String getMonth(Calendar mc) {

        if (mc == null)
            return "";

        String pattern = "MMM";

        SimpleDateFormat dateFromat_user = new SimpleDateFormat(pattern, Locale.US);

        return dateFromat_user.format(mc.getTime());

    }

    public static String getDateMonth(Calendar mc) {

        if (mc == null)
            return "";

        String pattern = "dMMM";

        SimpleDateFormat dateFromat_user = new SimpleDateFormat(pattern, Locale.US);

        return dateFromat_user.format(mc.getTime());

    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isSameDay(Date date1, Date date2) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isDateLessThan(Date date1, Date date2) {
        return date1.getTime() < date2.getTime();
    }

    public static boolean isDayPast(Calendar cal1) {

        Calendar cal2 = Calendar.getInstance();

        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        if (cal1 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return cal1.getTimeInMillis() < cal2.getTimeInMillis();
    }

    public static String fromDateFitbitString(Date date) {

        if (date == null)
            return "";

        String pattern = "yyyy-MM-dd";

        SimpleDateFormat dateFromat_user = new SimpleDateFormat(pattern, Locale.US);

        return dateFromat_user.format(date);
    }

}
