package theawesomebox.com.app.awesomebox.apps.module.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

import theawesomebox.com.app.awesomebox.BuildConfig;
import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;
import theawesomebox.com.app.awesomebox.apps.module.ui.home.MainActivity;
import theawesomebox.com.app.awesomebox.apps.module.ui.login.LoginActivity;
import theawesomebox.com.app.awesomebox.apps.module.ui.views.ContentView;
import theawesomebox.com.app.awesomebox.apps.module.ui.views.MainView;
import theawesomebox.com.app.awesomebox.apps.module.ui.views.SplashView;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private SplashView mSplashView;

    private ViewGroup mMainView;
    private View mContentView;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);
        //mSplashView = (SplashView) findViewById(R.id.splash_view);

        // create the main view and it will handle the rest
        mMainView = new MainView(getApplicationContext());
        mSplashView = ((MainView) mMainView).getSplashView();
        setContentView(mMainView);

        // pretend like we are loading data
        startLoadingData();

    }


    private void startLoadingData() {
        // finish "loading data" in a random time between 1 and 3 seconds
        Random random = new Random();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoadingDataEnded();
            }
        }, 1000 + random.nextInt(2000));
    }

    private void onLoadingDataEnded() {
        Context context = getApplicationContext();
        // now that our data is loaded we can initialize the content view
        mContentView = new ContentView(context);
        // add the content view to the background
        mMainView.addView(mContentView, 0);

        // start the splash animation
        mSplashView.splashAndDisappear(new SplashView.ISplashListener() {
            @Override
            public void onStart() {
                // log the animation start event
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "splash started");
                }
            }

            @Override
            public void onUpdate(float completionFraction) {
                // log animation update events
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "splash at " + String.format("%.2f", (completionFraction * 100)) + "%");
                }
            }

            @Override
            public void onEnd() {
                // log the animation end event
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "splash ended");
                    if (SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_IS_LOGGED_IN, false))
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    else
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
                // free the view so that it turns into garbage
                mSplashView = null;
                ((MainView) mMainView).unsetSplashView();
            }
        });
    }
}
