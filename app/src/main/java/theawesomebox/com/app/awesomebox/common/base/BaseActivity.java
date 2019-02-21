package theawesomebox.com.app.awesomebox.common.base;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.net.HttpURLConnection;
import java.util.Locale;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.apps.business.OnContentChangeListener;
import theawesomebox.com.app.awesomebox.apps.business.OnNetworkTaskListener;
import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;
import theawesomebox.com.app.awesomebox.apps.network.AppNetworkTask;
import theawesomebox.com.app.awesomebox.apps.network.HttpResponseItem;
import theawesomebox.com.app.awesomebox.apps.network.NetworkChangeReceiver;
import theawesomebox.com.app.awesomebox.apps.network.NetworkUtil;
import theawesomebox.com.app.awesomebox.apps.network.NetworkUtils;
import theawesomebox.com.app.awesomebox.common.utils.AppUtils;
import theawesomebox.com.app.awesomebox.common.utils.Logger;
import theawesomebox.com.app.awesomebox.common.utils.ViewUtils;

/**
 * Class is responsible to manage all fragment transactions
 * broadcast receiver, view manager etc.
 */
abstract public class BaseActivity extends AppCompatActivity implements OnContentChangeListener, OnNetworkTaskListener {

    public int width = 0, height = 0;
    public DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    // region VARIABLESø
    private Toolbar toolbar;
    // endregion
    private CharSequence appTitle;
    private boolean allowedToExit = false;

    private NetworkChangeReceiver receiver;

    Dialog noInternet;
    // region NETWORK_RECEIVER
    private BroadcastReceiver networkConnectivityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            onConnectivityChanged();
        }
    };

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        //AppUtils.setStatusBarGradiant(this);
        super.setContentView(layoutResID);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        connectivityCheck();
    }


    private void connectivityCheck() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        receiver = new NetworkChangeReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //do something based on the intent's action
                String status = NetworkUtil.getConnectivityStatusString(context);
                if (status.matches("Wifi enabled") || status.matches("Mobile data enabled")) {
                    if (noInternet != null && noInternet.isShowing())
                        noInternet.dismiss();
                } else {
                    showRatingsDialog();
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerToggle != null)
                    mDrawerToggle.onOptionsItemSelected(item);
                int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
                if (backStackEntryCount > 1)
                    onRemoveCurrentFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onConnectivityChanged() {
        TextView tvNoInterConnectivity = (TextView) findViewById(R.id.tv_no_internet_connect);
        if (tvNoInterConnectivity != null) {
            if (NetworkUtils.hasNetworkConnection(BaseActivity.this, true))
                tvNoInterConnectivity.setVisibility(View.VISIBLE);
            else
                tvNoInterConnectivity.setVisibility(View.GONE);
        }
    }
    // endregion

    //region LIFE_CYCLE
    @Override
    protected void onStart() {
        super.onStart();
//        FlurryAgent.onStartSession(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount <= 1) {
            if (allowedToExit || !isTaskRoot())
                finish();
            else {
                allowedToExit = true;
                ViewUtils.showToast(BaseActivity.this, "Press again to exit", Toast.LENGTH_SHORT);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        allowedToExit = false;
                    }
                }, 1000);
                return;
            }
        }
        // we have more than 1 fragments in back stack
        if (backStackEntryCount > 1) {
            AppUtils.hideSoftKeyboard(BaseActivity.this);
            onRemoveCurrentFragment();
        } else
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getCurrentFragment() instanceof BaseFragment)
            getCurrentFragment().onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toolbar = null;
        if (noInternet != null && noInternet.isShowing())
            noInternet.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (receiver != null)
            unregisterReceiver(receiver);
//        FlurryAgent.onEndSession(getApplicationContext());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void setTitle(CharSequence title) {
        appTitle = title;
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null)
//            actionBar.setTitle(appTitle);
        if (toolbar != null)
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(appTitle);
    }

    public void setMyTitle(String title) {
        appTitle = title;
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null)
//            actionBar.setTitle(appTitle);
        if (toolbar != null)
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(appTitle);
    }


    // endregion

    public String getLogTag() {
        return getClass().getSimpleName();
    }

    // region CONTENT_CHANGE_LISTENER

    /**
     * Get {@link Toolbar} of {@link BaseActivity}
     *
     * @return Toolbar
     */
    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * Remove current fragment from {@link android.support.v4.app.FragmentTransaction}
     */
    @Override
    public void onRemoveCurrentFragment() {
        // some times FragmentActivity.getLoaderManager returns null
        try {
            AppUtils.hideSoftKeyboard(this);
            getSupportFragmentManager().popBackStackImmediate();
            getSupportFragmentManager().beginTransaction().commit();
        } catch (Exception e) {
            Logger.caughtException(e);
        }
    }

    /**
     * Remove given fragment from {@link android.support.v4.app.FragmentTransaction}
     *
     * @param fragment {@link Fragment}
     */
    @Override
    public void onRemoveCurrentFragment(Fragment fragment) {
        // some time FragmentActivity.getLoaderManager returns null
        try {
            if (fragment == null) return;
            View view = fragment.getView();
            if (view != null) {
                ((ViewGroup) view).removeAllViews();
                view.invalidate();
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragment).commit();
            AppUtils.hideSoftKeyboard(this);
        } catch (Exception e) {
            Logger.caughtException(e);
        }
    }

    /**
     * Add fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment  {@link Fragment}
     * @param contentId Id of {@link android.widget.FrameLayout}
     */
    @Override
    public void onAddFragment(Fragment fragment, int contentId) {
        onAddFragment(fragment, contentId, false);
    }

    /**
     * Add fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment   {@link Fragment}
     * @param addToStack true add else do not add
     */
    @Override
    public void onAddFragment(Fragment fragment, boolean addToStack) {
        onReplaceFragment(fragment, R.id.content_frame, addToStack);
    }

    /**
     * Add fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment       {@link Fragment}
     * @param contentId      Id of {@link android.widget.FrameLayout}
     * @param addToBackStack true add else do not add
     */
    @Override
    public void onAddFragment(Fragment fragment, int contentId, boolean addToBackStack) {
        if (fragment == null) return;
        String tag = fragment.getClass().getSimpleName().toLowerCase(Locale.getDefault());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(contentId, fragment, tag);
        if (addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    /**
     * Replace fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment  {@link Fragment}
     * @param contentId Id of {@link android.widget.FrameLayout}
     */
    @Override
    public void onReplaceFragment(Fragment fragment, int contentId) {
        onReplaceFragment(fragment, contentId, false);
    }

    /**
     * Replace fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment   {@link Fragment}
     * @param addToStack true add else do not add
     */
    @Override
    public void onReplaceFragment(Fragment fragment, boolean addToStack) {
        onReplaceFragment(fragment, R.id.content_frame, addToStack);
    }

    /**
     * Replace fragment in given {@link android.widget.FrameLayout}
     *
     * @param fragment   {@link Fragment}
     * @param contentId  Id of {@link android.widget.FrameLayout}
     * @param addToStack true add else do not add
     */
    @Override
    public void onReplaceFragment(Fragment fragment, int contentId, boolean addToStack) {
        if (fragment == null) return;
        String tag = fragment.getClass().getSimpleName().toLowerCase(Locale.getDefault());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.replace(contentId, fragment, tag);
        if (addToStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Get fragment from given id of {@link android.widget.FrameLayout}
     *
     * @param contentId Id of {@link android.widget.FrameLayout}
     * @return Fragment
     */
    @Override
    public Fragment getCurrentFragment(int contentId) {
        return getSupportFragmentManager().findFragmentById(contentId);
    }

    /**
     * Get Fragment from default {@link android.widget.FrameLayout}
     *
     * @return Fragment
     */
    @Override
    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content_frame);
    }

    @Override
    public void onClearBackStack() {
        try {
            FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() > 0) {
                FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
                manager.popBackStackImmediate(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        } catch (Exception e) {
            Logger.error("onClearBackStack", e.toString());
        }
    }

    /**
     * @param title title of {@link android.support.design.widget.NavigationView}
     */
    @Override
    public void setDrawerTitle(CharSequence title) {
        if (title != null && !title.toString().isEmpty())
            setTitle(title);
        else
            setTitle(getString(R.string.app_name));
    }

    /**
     * Get parent view of {@link BaseActivity}
     *
     * @return View
     */
    @Override
    public View getParentView() {
        View view = findViewById(android.R.id.content).getRootView();
        if (view == null)
            view = getWindow().getDecorView().findViewById(android.R.id.content);
        if (view == null)
            view = ((ViewGroup) this
                    .findViewById(android.R.id.content)).getChildAt(0);
        return view;
    }
    //endregion

    // region VIEW
    public void setActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setDrawerTitle(getString(R.string.app_name));
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }


    /**
     * Set action bar drawer toggle
     */
    public void setDrawerToggle() {


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.str_open, R.string.str_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ActivityCompat.invalidateOptionsMenu(BaseActivity.this);
                syncState();
                AppUtils.hideSoftKeyboard(BaseActivity.this);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                setTitle(appTitle);
                ActivityCompat.invalidateOptionsMenu(BaseActivity.this);
                syncState();
                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                // backStackCount == 1 we choose fragment from drawer
                if (backStackCount > 1)
                    // we have more than 2 fragments so we must show back button instead of hum burger icon
                    animDrawerIcon(backStackCount < 2);
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(GravityCompat.START);
                if (getSupportFragmentManager().getBackStackEntryCount() > 1)// && isDrawerOpen)
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                else //if (!isDrawerOpen)
                    mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    final public void setBackStackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                // int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                onDrawerEnabled(backStackCount == 1, backStackCount);
                changeTollBar();
            }
        });
    }

    private void onDrawerEnabled(boolean isEnabled, int backStackCount) {
        if (mDrawerToggle != null && backStackCount <= 2)
            animDrawerIcon(isEnabled);
    }

    private void animDrawerIcon(boolean isEnabled) {
        ValueAnimator anim = ValueAnimator.ofFloat(isEnabled ? 1 : 0, isEnabled ? 0 : 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                if (mDrawerLayout != null && mDrawerToggle != null)
                    mDrawerToggle.onDrawerSlide(mDrawerLayout, slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(500);
        anim.start();
    }

    private void changeTollBar() {
        Fragment oldFragment = getCurrentFragment();
        if (oldFragment instanceof BaseFragment) {
            BaseFragment fragment = (BaseFragment) oldFragment;
            // fragment.onChangeOptionsMenuVisibility(true);
            setTitle(fragment.getTitle());
        } else
            setTitle(getString(R.string.app_name));
    }
    // endregion

    // region NETWORK_OPERATIONS

    /**
     * @return true is connected else not
     */
    @Override
    final public boolean isNetworkConnected() {
        return NetworkUtils.hasNetworkConnection(BaseActivity.this, true);
    }

    /**
     * HTTP response call back from {@link AppNetworkTask}
     *
     * @param response {@link HttpResponseItem}
     */
    @Override
    public void onNetworkResponse(HttpResponseItem response) {
        boolean status = response.getResponseCode() == HttpURLConnection.HTTP_OK;
        if (status)
            onNetworkSuccess(response);
        else
            onNetworkError(response);
    }

    /**
     * HTTP network operation is successfully completed
     *
     * @param response {@link HttpResponseItem}
     */
    @Override
    public void onNetworkSuccess(HttpResponseItem response) {
        Logger.info(getLogTag(), "Network operation (" + response.getHttpRequestUrl() + ") successfully completed");
    }

    /**
     * For some reasons there is/are some error(s) in network operation
     *
     * @param response {@link HttpResponseItem}
     */
    @Override
    public void onNetworkError(HttpResponseItem response) {
        Logger.error(getLogTag(), response.getDefaultResponse() + "(network error)");
        if (response.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
            showSessionExpiredDialog();
        else
            showSnackBar(getString(R.string.error_message), Snackbar.LENGTH_SHORT);

    }

    /**
     * For some reasons network operation has been cancelled
     *
     * @param response {@link HttpResponseItem}
     */
    @Override
    final public void onNetworkCanceled(HttpResponseItem response) {
        Logger.error(getLogTag(), response.getDefaultResponse() + " (operation cancelled by user)");
    }
    // endregion

    /**
     * Show snackBar in current view
     *
     * @param message  message to be displayed
     * @param duration defined snackBar duration flags
     */
    public void showSnackBar(String message, int duration) {
        View view = getParentView();
        if (view == null)
            return;
        Snackbar.make(view, message, (duration <= 0 && duration >= -2 ? duration : Snackbar.LENGTH_SHORT)).show();
    }

    /**
     * Show snackBar in current view
     *
     * @param message message to be displayed
     */
    public void showSnackBar(String message) {
        showSnackBar(message, Snackbar.LENGTH_LONG);
    }

    /**
     * If server response code is unauthorized (session expired)
     */
    private void showSessionExpiredDialog() {
        SharedPreferenceManager.getInstance().clearPreferences();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Session expired");
        builder.setMessage("Your session got expired kindly sign in again.");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }

    /**
     * Create and return a waiting progress dialog
     *
     * @param cancelable is dialog cancelable
     * @return a new progress dialog
     */
    public ProgressDialog getProgressDialog(boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait!");
        dialog.setCancelable(cancelable);
        return dialog;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (getCurrentFragment() instanceof BaseFragment)
            getCurrentFragment().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showRatingsDialog() {
        noInternet = new Dialog(this);
        noInternet.requestWindowFeature(Window.FEATURE_NO_TITLE);
        noInternet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        noInternet.setContentView(R.layout.layout_no_internet);
        noInternet.setCancelable(false);
        noInternet.setCanceledOnTouchOutside(false);
        final Window window = noInternet.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        final RippleBackground rippleBackground = noInternet.findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        noInternet.show();

    }
}
