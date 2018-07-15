package theawesomebox.com.app.awesomebox.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ViewUtils {

    public static void showToast(Context context, String text, int time) {
        if (context == null)
            return;
        Toast.makeText(context, text, time).show();
    }

    public static void showSnackBar(View view, String text, int duration) {
        if (view != null && AppUtils.ifNotNullEmpty(text))
            Snackbar.make(view, text, duration).show();
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = context.getResources().getDimensionPixelSize(resourceId);
        return result;
    }

    public static void hackPickerFormatter(NumberPicker picker) {
        try {
            Method method = picker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(picker, true);
        } catch (NoSuchMethodException | IllegalAccessException |
                InvocationTargetException | IllegalArgumentException e) {
            Logger.caughtException(e);
        }
        Field f;
        try {
            f = NumberPicker.class.getDeclaredField("mInputText");
            f.setAccessible(true);
            EditText inputText = (EditText) f.get(picker);
            inputText.setFilters(new InputFilter[0]);
        } catch (Exception e) {
            Logger.caughtException(e);
        }
    }

    public static void setDividerColor(NumberPicker picker, int color) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException | IllegalAccessException |
                        Resources.NotFoundException e) {
                    Logger.caughtException(e);
                }
                break;
            }
        }
    }

    public static Drawable updateColorFiler(Context context, int color, int resourceId) {
        final Drawable drawable = ContextCompat.getDrawable(context, resourceId);
        if (drawable != null)
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }

    public static float convertDpToPixel(float dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static float convertPixelsToDp(float px, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int getListViewItemHeight(ListView listView, int items) {
        final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        ListAdapter adapter = listView.getAdapter();
        int grossElementHeight = 0;
        for (int i = 0; i < items; i++) {
            View childView = adapter.getView(i, null, listView);
            childView.measure(UNBOUNDED, UNBOUNDED);
            grossElementHeight += childView.getMeasuredHeight();
        }
        return grossElementHeight;
    }

    public static String addAlphaRGB(String originalColor, double alpha) {
        long alphaFixed = Math.round(alpha * 255);
        String alphaHex = Long.toHexString(alphaFixed);
        if (alphaHex.length() == 1)
            alphaHex = "0" + alphaHex;
        return originalColor.replace("#", "#" + alphaHex);
    }

    public static void changeScreenOrientation(Activity activity, boolean isPortrait) {
        if (isPortrait)
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        else activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    public static void removePickerException(NumberPicker picker) {
        // prevent crash don't know why
        picker.setSaveFromParentEnabled(false);
        picker.setSaveEnabled(false);
    }

    public static String getResourceColor(Context context, int resId) {
        String color = context.getString(resId);
        if (color.length() == 7)
            return color;
        else
            return "#" + color.substring(3, color.length());
    }
}
