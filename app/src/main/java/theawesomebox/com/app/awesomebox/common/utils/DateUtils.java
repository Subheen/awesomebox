package theawesomebox.com.app.awesomebox.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.support.annotation.RequiresPermission;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    // region DATE_FORMATS
    public static final String EDIT_PROFILE_FORMAT = "yy-mm-dd";
    public static final String DATE_FORMAT_5 = "MMM dd, yyyy";
    public static final String DATE_FORMAT_7 = "MM-dd-yyyy";
    public static final String HOUR_FORMAT_1 = "h:mm a";
    public static final String CALENDAR_DEFAULT_FORMAT = "EE MMM dd HH:mm:ss z yyyy";
    public static final String CAMERA_FORMAT = "yyyyMMdd_HHmmss";
    public static final String FILE_NAME = "yyyyMMdd_HHmmssSSS";
    //public static final String JOB_FORMAT = "dd-MMM-yyyy HH:mm:ss";
    public static final String JOB_FORMAT = "dd-MM-yyyy hh:mm a";
    public static final String JOB_FORMAT_SEND = "DD-MM-yyyyhh:mma";
    public static final String UTC_TIME = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String UTC_TIME_MILLIS_1 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    // endregion

    public static Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        return calendar;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate(String format) {
        Calendar c = getCalendar();
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(c.getTime());
    }

    public static String convertDate(long dateInMilliseconds, String dateFormat) {
        return (new SimpleDateFormat(dateFormat, Locale.getDefault()).format(new Date(dateInMilliseconds)));
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        if (dateInMilliseconds == null)
            dateInMilliseconds = "0";
        if (dateInMilliseconds.isEmpty())
            dateInMilliseconds = "0";
        return (new SimpleDateFormat(dateFormat, Locale.getDefault()).format(new Date(dateStringToLong(dateInMilliseconds))));
    }

    public static long dateStringToLong(String dateInMilliseconds) {
        if (dateInMilliseconds == null)
            dateInMilliseconds = "0";
        if (dateInMilliseconds.isEmpty())
            dateInMilliseconds = "0";
        return (Long.parseLong(dateInMilliseconds.replaceAll("[^\\d.]", "")));
    }

    public static Calendar dateMilliSecondsToCalendar(long milliSeconds) {
        Calendar calendar = DateUtils.getCalendar();
        calendar.setTimeInMillis(milliSeconds);
        return calendar;
    }

    public static String oneFormatToAnother(String date, String oldFormat, String newFormat) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat(oldFormat,
                    Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat(newFormat,
                    Locale.getDefault());
            Date d = originalFormat.parse(date);
            return targetFormat.format(d);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date stringToDate(String date, String format) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat(format, Locale.getDefault());
            return originalFormat.parse(date);
        } catch (Exception e) {
            Logger.caughtException(e);
            return null;
        }
    }

    public static boolean isFirstDateGreater(Date date1, Date date2) {
        long milliSeconds1 = date1.getTime();
        long milliSeconds2 = date2.getTime();
        return milliSeconds1 > milliSeconds2;
    }

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    @SuppressWarnings({"MissingPermission"})
    public static void addScheduleToCalendar(Context context, String calTitle, String calLocation, String description,
                                             int reminder, Calendar startTime, Calendar endTime) {
        long calID = 1;
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startTime.getTimeInMillis());
        values.put(Events.DTEND, endTime.getTimeInMillis());
        values.put(Events.TITLE, calTitle);
        values.put(Events.EVENT_LOCATION, calLocation);
        values.put(Events.DESCRIPTION, description);
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        Uri uri = resolver.insert(Events.CONTENT_URI, values);
        if (reminder >= 0 && uri != null) {
            long eventID = Long.parseLong(uri.getLastPathSegment());
            ContentValues reminders = new ContentValues();
            reminders.put(Reminders.EVENT_ID, eventID);
            reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
            reminders.put(Reminders.MINUTES, reminder);
            resolver.insert(Reminders.CONTENT_URI, reminders);
            Logger.info("addScheduleToCalendar", "LastPathSegment: " + uri.getLastPathSegment());
        }
    }

    @SuppressWarnings("WrongConstant")
    public static String howMuchTimePastFromNow(long milliSeconds) {
        if (milliSeconds < 0)
            return "N/A";

        long milliSecondsCurrent = DateUtils.getCalendar().getTimeInMillis();
        long diff = milliSecondsCurrent - milliSeconds;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = hours / 30;
        long years = months / 12;

        Calendar systemCalendar = DateUtils.getCalendar();
        systemCalendar.setTimeInMillis(milliSeconds);
        Calendar current = DateUtils.getCalendar();
        current.setTimeInMillis(milliSecondsCurrent);

        if (systemCalendar.get(Calendar.DATE) + 1 == current.get(Calendar.DATE))
            return ("Yesterday at " + convertDate(milliSeconds, HOUR_FORMAT_1));
        if (years > 0) {
            return (convertDate(milliSeconds, DATE_FORMAT_5) + " at " + convertDate(milliSeconds, HOUR_FORMAT_1));
        } else if (months > 0) {
            // return (months + " Months Ago");
            return (convertDate(milliSeconds, DATE_FORMAT_5) + " at " + convertDate(milliSeconds, HOUR_FORMAT_1));
        } else if (days > 0) {
            // return (days + " Days Ago");
            return (convertDate(milliSeconds, DATE_FORMAT_5) + " at " + convertDate(milliSeconds, HOUR_FORMAT_1));
        } else if (hours > 0)
            return (hours + (hours == 1 ? " hour " : " hours ") + "ago");
        else if (minutes > 0)
            return (minutes + (minutes == 1 ? " minute " : " minutes ") + "ago");
        else if (seconds > 5)
            return (seconds + " seconds ago");
        else
            return ("Just now");
    }
}
