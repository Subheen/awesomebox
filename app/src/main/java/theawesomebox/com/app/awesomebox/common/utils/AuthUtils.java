package theawesomebox.com.app.awesomebox.common.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresPermission;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for authentication related methods
 */
public class AuthUtils {

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        if (AppUtils.ifNotNullEmpty(email)) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches())
                isValid = true;
        }
        return isValid;
    }

    public static boolean isNameValid(String name) {
        boolean isValid = false;
        if (AppUtils.ifNotNullEmpty(name)) {
            // String expression = "^[.a-zA-Z \\\\s](2, )$";
            String expression = "^[a-zA-Z. ](\\s?[a-zA-Z. ]){2,}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(name);
            if (matcher.matches())
                isValid = true;
        }
        return isValid;
    }

    public static boolean isPasswordValid(String password) {
        boolean isValid = false;
        if (AppUtils.ifNotNullEmpty(password)) {
            //Pattern pattern = Pattern.compile(PASSWORD_REGEX, Pattern.CASE_INSENSITIVE);
            //Matcher matcher = pattern.matcher(password);
            //if (matcher.matches())
            //    isValid = true;
            if (password.length() >= 8)
                isValid = true;
        }
        return isValid;
    }

    @RequiresPermission(android.Manifest.permission.GET_ACCOUNTS)
    public static String getDefaultEmailAddress(Context context) {
        String possibleEmail = "";
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmail = account.name;
            }
        }
        return possibleEmail;
    }

    @RequiresPermission(android.Manifest.permission.GET_ACCOUNTS)
    public static String[] getEmailAddresses(Context context) {
        List<String> emailAddresses = new ArrayList<>();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches())
                emailAddresses.add(account.name);
        }
        HashSet<String> stringHashSet = new HashSet<>(emailAddresses);// create hash sets for unique email ids
        List<String> newList = new ArrayList<>(stringHashSet); //wrap them back to list string
        return newList.toArray(new String[newList.size()]); // return an array of string that has unique ids
        //return emailAddresses.toArray(new String[emailAddresses.size()]);
    }

    public static boolean isAppInstalled(Context context, String appName) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(appName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.caughtException(e);
            return false;
        }
    }

    public static void showMemoryUsage(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            Logger.info("MEMORY_PERCENTAGE", ((float) mi.availMem / mi.totalMem) + "%");
        Logger.info("MEMORY_MB", mi.availMem / 1048576L + " MB");
    }

    public static void garbageCollector() {
        try {
            System.runFinalization();
            Runtime.getRuntime().gc();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
