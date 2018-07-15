package theawesomebox.com.app.awesomebox.apps.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

final public class SharedPreferenceManager {

    public static final String KEY_NAME = "fname";
    public static final String KEY_ID = "_id";
    public static final String KEY_LNAME = "lname";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_COOKIE = "cookie";
    public static final String KEY_URL = "url";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_DOB = "dob";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CITY = "city";
    public static final String KEY_STATE = "state";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_POSTAL = "postal";
    public static final String KEY_IS_STRIPE_CUSTOMER = "isStripeCustomer";

    // project specific
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_TOKEN = "userToken";
    public static final String KEY_USER_ID = "userId";

    private static SharedPreferenceManager sharedPreferenceManager = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private SharedPreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PreferenceUtils.PREFERENCE_NAME, PreferenceUtils.PRIVATE_MODE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public static void setSingletonInstance(Context context) {
        synchronized (SharedPreferenceManager.class) {
            if (sharedPreferenceManager == null)
                sharedPreferenceManager = new SharedPreferenceManager(context);
            else
                throw new IllegalStateException("SharedPreferenceManager instance already exists.");
        }
    }

    public static SharedPreferenceManager getInstance() {
        return sharedPreferenceManager;
    }

    public void clearPreferences() {
        editor.clear();
        editor.commit();
    }

    public String read(String valueKey, String valueDefault) {
        return sharedPreferences.getString(valueKey, valueDefault);
    }

    public void save(String valueKey, String value) {
        editor.putString(valueKey, value);
        editor.commit();
    }

    public int read(String valueKey, int valueDefault) {
        return sharedPreferences.getInt(valueKey, valueDefault);
    }

    public void save(String valueKey, int value) {
        editor.putInt(valueKey, value);
        editor.commit();
    }

    public boolean read(String valueKey, boolean valueDefault) {
        return sharedPreferences.getBoolean(valueKey, valueDefault);
    }

    public void save(String valueKey, boolean value) {
        editor.putBoolean(valueKey, value);
        editor.commit();
    }

    public long read(String valueKey, long valueDefault) {
        return sharedPreferences.getLong(valueKey, valueDefault);
    }

    public void save(String valueKey, long value) {
        editor.putLong(valueKey, value);
        editor.commit();
    }

    public float read(String valueKey, float valueDefault) {
        return sharedPreferences.getFloat(valueKey, valueDefault);
    }

    public void save(String valueKey, float value) {
        editor.putFloat(valueKey, value);
        editor.commit();
    }

    public void storeUserInfo(String _id, String fname, String lname, String email, String cookie, String image, String phone, String gender, String dob, String address, String city, String state, String postalCode, boolean isStripeCustomer) {


        editor.putString(KEY_NAME, fname);
        editor.putString(KEY_ID, _id);
        editor.putString(KEY_LNAME, lname);
        editor.putString(KEY_COOKIE, cookie);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_URL, image);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_DOB, dob);
        editor.putString(KEY_STATE, state);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_POSTAL, postalCode);
        editor.putBoolean(KEY_IS_STRIPE_CUSTOMER, isStripeCustomer);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, sharedPreferences.getString(KEY_NAME, ""));
        user.put(KEY_LNAME, sharedPreferences.getString(KEY_LNAME, ""));
        user.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, ""));
        user.put(KEY_URL, sharedPreferences.getString(KEY_URL, ""));
        user.put(KEY_PHONE, sharedPreferences.getString(KEY_PHONE, ""));
        user.put(KEY_ADDRESS, sharedPreferences.getString(KEY_ADDRESS, ""));
        user.put(KEY_GENDER, sharedPreferences.getString(KEY_GENDER, ""));
        user.put(KEY_DOB, sharedPreferences.getString(KEY_DOB, ""));
        user.put(KEY_STATE, sharedPreferences.getString(KEY_STATE, ""));
        user.put(KEY_CITY, sharedPreferences.getString(KEY_CITY, ""));
        user.put(KEY_POSTAL, sharedPreferences.getString(KEY_POSTAL, ""));
        return user;
    }
}
