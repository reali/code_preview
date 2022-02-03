package com.secretpackage.inspector.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ali on 7/27/19.
 */

public class DateUtils {

    public static String fromDateToString(Date date) {

        if (date == null)
            return "";

        String pattern = "MM/dd/yyyy";

        SimpleDateFormat dateFromat_user = new SimpleDateFormat(pattern, Locale.US);

        return dateFromat_user.format(date);
    }

    public static String fromDateToRawString(Date date) {

        if (date == null)
            return "";

        String pattern = "yyyyMMdd";

        SimpleDateFormat dateFromat_user = new SimpleDateFormat(pattern, Locale.US);

        return dateFromat_user.format(date);
    }
    public static String fromRawStringToString(String str) {
        return fromDateToString(fromRawStringToDate(str));
    }

    public static Date fromStringToDate(String str) {

        if (str == null || str.isEmpty())
            return null;

        String pattern = "MM/dd/yyyy";

        SimpleDateFormat dateFromat_user = new SimpleDateFormat(pattern, Locale.US);

        Date date = null;
        try {
            date = dateFromat_user.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;

    }

    public static Date fromRawStringToDate(String str) {

        if (str == null || str.isEmpty())
            return null;

        String pattern = "MM/dd/yyyy";

        SimpleDateFormat dateFromat_user = new SimpleDateFormat(pattern, Locale.US);

        Date date = null;
        try {
            date = dateFromat_user.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;

    }

}
