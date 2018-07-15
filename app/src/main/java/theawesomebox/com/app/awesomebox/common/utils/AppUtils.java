package theawesomebox.com.app.awesomebox.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import theawesomebox.com.app.awesomebox.apps.data.preferences.PreferenceUtils;
import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.webkit.URLUtil.isNetworkUrl;

/**
 * Contains utility methods used in application
 */
public class AppUtils {
    /**
     * for rounding of the double to specific decimal places
     * @param value
     * @param places
     * @return
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static Map<String, String> getHeaderBodyParam() {
        SharedPreferenceManager manager = SharedPreferenceManager.getInstance();
        Map<String, String> param = new HashMap<>();
        param.put("Authorization", "Bearer " + manager.read(PreferenceUtils.ACCESS_TOKEN, ""));
//        param.put("Authorization", "Bearer "+"59385952fff41a5a5773dbdfec4260242e6aa165");
        return param;
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null)
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Activity activity) {
        if (activity == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            view.requestFocus();
            inputMethodManager.showSoftInput(view, 0);
        }
    }

    public static boolean validateUri(Uri uri) {
        if (uri == null)
            return false;
        else {
            String path = uri.getPath();
            return !(uri.equals(Uri.EMPTY) || path == null || path.equals("null"));
        }
    }

    public static boolean ifNotNullEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static String toTitleCase(String givenString) {
        if (!AppUtils.ifNotNullEmpty(givenString) || givenString.equalsIgnoreCase(" "))
            return "";
        else {
            String[] arr = givenString.trim().split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (String anArr : arr)
                sb.append(Character.toUpperCase(anArr.charAt(0)))
                        .append(anArr.substring(1)).append(" ");
            return sb.toString().trim();
        }
    }

    public static boolean validateEmptyEditText(EditText et) {
        return (et.getText().toString().equals("")) ? false : true;
    }

    public static boolean validateEmptyString(String string) {
        return (string.equals("")) ? false : true;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        if (ifNotNullEmpty(email)) {
            try {
                String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(email);
                if (matcher.matches())
                    isValid = true;
            } catch (Exception e) {
                Logger.error("isEmailValid", e.toString());
            }
        }
        return isValid;
    }

    public static String preparePathForPicture(String path, String imageType) {
        String preparedLink;
        if (isNetworkUrl(path)) {
            String[] arr = path.split("\\.(?=[^\\.]+$)");
            preparedLink = arr[0] + imageType + ".jpg";
        } else
            preparedLink = path;
        return preparedLink;
    }

    public static Map<String, String> getHeaderParams() {
        Map<String, String> map = new HashMap<>();
        map.put("cookie", SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_COOKIE, ""));  // Add required header parameters (Cookies) here
        Log.e("COOKIE", map.get("cookie"));
        return map;
    }

    /**
     * Converting dp to pixel
     */
    public static int dpToPx(int dp, Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



}
