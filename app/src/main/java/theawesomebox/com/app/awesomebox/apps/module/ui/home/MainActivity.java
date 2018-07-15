package theawesomebox.com.app.awesomebox.apps.module.ui.home;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;
import theawesomebox.com.app.awesomebox.apps.module.support.firebase.Config;
import theawesomebox.com.app.awesomebox.apps.module.ui.others.LogoutFragment;
import theawesomebox.com.app.awesomebox.apps.module.ui.others.PrivacyPolicyFragment;
import theawesomebox.com.app.awesomebox.apps.module.ui.others.SupportFragment;
import theawesomebox.com.app.awesomebox.apps.module.ui.scripts.ScriptsFragment;
import theawesomebox.com.app.awesomebox.apps.network.AppNetworkTask;
import theawesomebox.com.app.awesomebox.apps.network.HttpRequestItem;
import theawesomebox.com.app.awesomebox.apps.network.HttpResponseItem;
import theawesomebox.com.app.awesomebox.apps.network.NetworkUtils;
import theawesomebox.com.app.awesomebox.common.base.BaseActivity;
import theawesomebox.com.app.awesomebox.common.utils.AppConstants;
import theawesomebox.com.app.awesomebox.common.utils.AppUtils;
import theawesomebox.com.app.awesomebox.common.utils.Logger;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String URL_BADGE_RESET;
    private String URL_TOKEN_SET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionBar();
        setBackStackListener();

        populateView();
        setDrawerToggle();
        onReplaceFragment(new HomeFragment(), true);

    }

    private void populateView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                AppUtils.hideSoftKeyboard(MainActivity.this);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setCheckedItem(R.id.home);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(mConnection);
//        unregisterReceiver(receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        badgeCount();
        deviceToken();
    }

    private void badgeCount() {

        String userToken = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_TOKEN, "");
        int userId = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_ID, -1);

        if (userId != -1 && AppUtils.ifNotNullEmpty(userToken)) {
            URL_BADGE_RESET = AppConstants.RESET_BADGE + userId + "?token=" + userToken;
            HttpRequestItem requestItem = new HttpRequestItem(
                    AppConstants.getServerUrl(URL_BADGE_RESET));
            requestItem.setHttpRequestType(NetworkUtils.HTTP_PUT);     //by default its GET
//        requestItem.setHeaderParams(AppUtils.getHeaderParams());    // Add header params (Cookies)
            // Add post body parameters OR get query parameters here
            Map<String, Object> params = new HashMap<>();
            params.put("phoneBadge", 0);
            requestItem.setParams(params);
            AppNetworkTask appNetworkTask = new AppNetworkTask(getProgressDialog(false),
                    this);
            appNetworkTask.execute(requestItem);
        }
    }

    private void deviceToken() {

        String userToken = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_TOKEN, "");
        int userId = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_ID, -1);

        if (userId != -1 && AppUtils.ifNotNullEmpty(userToken)) {
            SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
            String token = pref.getString("regId", "");

            Log.e("TOKEN:", token);

            URL_TOKEN_SET = AppConstants.SEND_TOKEN_TO_SERVER + userId + "?token=" + userToken;
            HttpRequestItem requestItem = new HttpRequestItem(
                    AppConstants.getServerUrl(URL_TOKEN_SET));
            requestItem.setHttpRequestType(NetworkUtils.HTTP_PUT);     //by default its GET
//        requestItem.setHeaderParams(AppUtils.getHeaderParams());    // Add header params (Cookies)
            // Add post body parameters OR get query parameters here
            Map<String, Object> params = new HashMap<>();
            params.put("phoneToken", token);

            requestItem.setParams(params);

            AppNetworkTask appNetworkTask = new AppNetworkTask(getProgressDialog(false),
                    this);
            appNetworkTask.execute(requestItem);
        }
    }

    @Override
    public void onNetworkSuccess(HttpResponseItem response) {
        super.onNetworkSuccess(response);
        try {
            JSONObject responseJson = new JSONObject(response.getResponse());
            Log.e("MAIN RESPONSE :: ", responseJson.toString());

            if (response.getHttpRequestUrl().equalsIgnoreCase(AppConstants.getServerUrl(URL_BADGE_RESET))) {


            } else if (response.getHttpRequestUrl().equalsIgnoreCase(AppConstants.getServerUrl(URL_TOKEN_SET))) {

            }

        } catch (JSONException e) {
            Logger.error(false, e);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setCheckedItem(item.getItemId());
        switch (item.getItemId()) {
            case R.id.home:
                clearContentView();
                onReplaceFragment(new HomeFragment(), true);
                break;

            case R.id.script:
                clearContentView();
                onReplaceFragment(new ScriptsFragment(), true);
                break;

            case R.id.backup:
                clearContentView();
                BackupDetailsFragment backupDetailsFragment = new BackupDetailsFragment();
                Bundle args = new Bundle();
                args.putBoolean("fromMain", true);
                backupDetailsFragment.setArguments(args);
                onReplaceFragment(backupDetailsFragment, true);
                break;

            case R.id.privacy:
                clearContentView();
                onReplaceFragment(new PrivacyPolicyFragment(), true);
                break;

            case R.id.support:
                clearContentView();
                onReplaceFragment(new SupportFragment(), true);

                break;

            case R.id.logout:
                clearContentView();
                onReplaceFragment(new LogoutFragment(), true);
                break;

        }
        mDrawerLayout.closeDrawers();
        return true;
    }

    // Called to remove all previously loaded fragments from the backStack along with
    // clearing the content view that loads the fragments(For memory crashing issues)
    public void clearContentView() {
        onClearBackStack();
        ((ViewGroup) findViewById(R.id.content_frame)).removeAllViews();
    }


}
