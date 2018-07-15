package theawesomebox.com.app.awesomebox.apps.data.preferences;

public class PreferenceUtils {

    public static final String TOKEN_TYPE = "token_type";
    public static final String ACCESS_TOKEN = "access_token";

    // region AUTHENTICATION
    public static final String USER_ID = "enabler_id";
    // region fireBase
    public static final String FCM_TOKEN = "fcm_token";
    public static final String HAS_UPLOAD_TOKEN = "has_upload_token";
    // endregion
    public static final String BASE_URL = "url";
    static final String PREFERENCE_NAME = "e_medics";
    //end region
    static final int PRIVATE_MODE = 0;

    public static String getAuthorization() {
        SharedPreferenceManager manager = SharedPreferenceManager.getInstance();
        return manager.read(TOKEN_TYPE, null) + " " + manager.read(ACCESS_TOKEN, null);
    }

    /**
     * check if user is authenticated (Signed in )with the server
     *
     * @return
     */
    public static boolean isUserAuthenticated() {
        SharedPreferenceManager manager = SharedPreferenceManager.getInstance();
        return manager.read(ACCESS_TOKEN, null) != null;
    }
    // endregion
}
