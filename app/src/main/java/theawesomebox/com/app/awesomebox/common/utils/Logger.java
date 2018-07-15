package theawesomebox.com.app.awesomebox.common.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import theawesomebox.com.app.awesomebox.BuildConfig;

/**
 * This is a wrapper class for {@link Log}. Main purpose is to centralized exception/log
 * catcher and if necessary send log/crash to crashlytics.
 */
public class Logger {

    /**
     * Show logs only in debug mode
     *
     * @param logCrash true log this crash to {@link}
     * @param e        {@link Exception}
     */
    public static void error(boolean logCrash, Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
//            if (logCrash)
//                Crashlytics.logException(e);
        }
    }

    /**
     * Show logs only in debug mode
     * Equivalent to {@link Log#e(String, String)}
     *
     * @param tag  tag name of log
     * @param text log text
     */
    public static void error(String tag, String text) {
        if (BuildConfig.DEBUG)
            Log.e(tag, text);
    }

    /**
     * Show logs only in debug mode
     *
     * @param tag tag name of log
     * @param e   {@link Exception}
     */
    public static void debug(String tag, Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
//            Crashlytics.logException(e);
        }
    }

    /**
     * Show logs only in debug mode
     * Equivalent to {@link Log#d(String, String)}
     *
     * @param tag  tag name of log
     * @param text log text
     */
    public static void debug(String tag, String text) {
        if (BuildConfig.DEBUG)
            Log.d(tag, text);
    }

    /**
     * Show logs only in debug mode
     *
     * @param tag tag name of log
     * @param e   {@link Exception}
     */
    public static void info(String tag, Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
            // Crashlytics.logException(e);
        }
    }

    /**
     * Show logs only in debug mode
     * Equivalent to {@link Log#i(String, String)}
     *
     * @param tag  tag name of log
     * @param text log text
     */
    public static void info(String tag, String text) {
        if (BuildConfig.DEBUG)
            Log.i(tag, text);
    }

    /**
     * Show logs only in debug mode
     *
     * @param tag tag name of log
     * @param e   {@link Exception}
     */
    public static void warn(String tag, Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
            Log.w(tag, e.toString());
        }
    }

    /**
     * Show logs only in debug mode
     * Equivalent to {@link Log#w(String, String)}
     *
     * @param tag  tag name of log
     * @param text log text
     */
    public static void warn(String tag, String text) {
        if (BuildConfig.DEBUG)
            Log.w(tag, text);
    }

    /**
     * Log Exception in {@link }
     *
     * @param cause {@link Exception}
     */
    public static void caughtException(Exception cause) {
        cause.printStackTrace();
//        Crashlytics.logException(cause);
    }

    /**
     * Get stacktrace of given exception
     *
     * @param e {@link Exception}
     * @return stacktrace
     */
    @NonNull
    private static String getStackTrace(Exception e) {
        String culprit = "";
        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement stackTrace : elements)
            culprit = stackTrace.toString() + "\n";
        return culprit;
    }
}
