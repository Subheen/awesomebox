package theawesomebox.com.app.awesomebox.common.utils;

import android.util.Patterns;

public final class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return AppUtils.ifNotNullEmpty(email) &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
