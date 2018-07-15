package theawesomebox.com.app.awesomebox;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCore;
import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;
import theawesomebox.com.app.awesomebox.common.utils.Configuration;
import theawesomebox.com.app.awesomebox.common.utils.FontsOverride;


public class MyApplication extends MultiDexApplication {


    public static final String TAG = MyApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    private static MyApplication mInstance;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
        SharedPreferenceManager.setSingletonInstance(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
//        initFlurryAgent();
        initRushOrm();
//        //  custom fonts
        FontsOverride.setDefaultFont(this, "MONOSPACE", "helvetica.ttf");
    }



    private void initFlurryAgent() {
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(getApplicationContext(), Configuration.FLURRY_API_KEY);
    }

    private void initRushOrm() {
        AndroidInitializeConfig config = new AndroidInitializeConfig(getApplicationContext());
        config.setClasses(getRushClasses());
        RushCore.initialize(config);
    }

    /**
     * @return List of classes extends with Rush
     */
    private List<Class<? extends Rush>> getRushClasses() {
        List<Class<? extends Rush>> classes = new ArrayList<>();
        return classes;
    }

    //capthca



    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
