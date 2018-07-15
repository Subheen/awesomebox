package theawesomebox.com.app.awesomebox.common.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.apps.business.OnContentChangeListener;
import theawesomebox.com.app.awesomebox.apps.business.OnNetworkTaskListener;
import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;
import theawesomebox.com.app.awesomebox.apps.network.AppNetworkTask;
import theawesomebox.com.app.awesomebox.apps.network.HttpResponseItem;
import theawesomebox.com.app.awesomebox.apps.network.NetworkUtils;
import theawesomebox.com.app.awesomebox.common.utils.Logger;
import theawesomebox.com.app.awesomebox.common.utils.ViewUtils;


/***
 * This Fragment contains {@link WeakReference} of {@link OnContentChangeListener}.
 * Which we are using for Activity and fragment communication.
 */
@SuppressWarnings({"ConstantConditions", "NullableProblems"})
public abstract class BaseFragment extends Fragment implements OnNetworkTaskListener {

    // region CONTENT_CHANGE_LISTENER
    private WeakReference<OnContentChangeListener> reference = null;
    private Dialog dialog;
    // endregion

    // region ABSTRACT

    final protected OnContentChangeListener getReference() {
        return reference != null ? reference.get() : null;
    }
    // endregion

    /**
     * @return Title of given fragment
     */
    abstract public String getTitle();

    public void setTitle(String title) {
        if (getActivity() == null)
            return;
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null)
            baseActivity.setTitle(title);
    }

    // region LIFE_CYCLE
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            reference = new WeakReference<>((OnContentChangeListener) context);
        } catch (ClassCastException e) {
            Logger.caughtException(e);
            throw new ClassCastException(context.toString() + " must implement OnContentChangeListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Logger.error("onLowMemory", String.format("%s is running on low memory", getLogTag()));
    }
    // endregion

    @Override
    public void onDestroy() {
        super.onDestroy();
        reference.clear();
        reference = null;
        Logger.info("onDestroy", getLogTag());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    public void sendSearchTextToFrags(String searchText, int scriptType){}

    //region VIEW_HELPERS
    public View findViewById(int resourceId) {
        return getView().findViewById(resourceId);
    }

    public TextView findTextViewById(int resourceId) {
        return (TextView) findViewById(resourceId);
    }

    public EditText findEditTextById(int resourceId) {
        return (EditText) findViewById(resourceId);
    }

    public void setText(int resourceId, String text) {
        findTextViewById(resourceId).setText(text);
    }

    public Button findButtonById(int resourceId) {
        return (Button) findViewById(resourceId);
    }
    //endregion

    public LinearLayout findLinearLayoutById(int resourceId) {
        return (LinearLayout) findViewById(resourceId);
    }

    public RelativeLayout findRelativeLayoutById(int resourceId) {
        return (RelativeLayout) findViewById(resourceId);
    }

    // region ACTIVITY_VIEW_NULL
    final protected boolean isActivityNotNull() {
        return getActivity() != null;
    }

    final protected boolean isViewNull() {
        return getView() == null;
    }
    //endregion

    // region NETWORK_OPERATIONS

    final protected boolean isActivityAndViewNotNull() {
        return isActivityNotNull() && isAdded();
    }

    final public String getLogTag() {
        return getClass().getSimpleName();
    }

    /**
     * @return true is connected else not
     */
    @Override
    final public boolean isNetworkConnected() {
        return NetworkUtils.hasNetworkConnection(getActivity(), true);
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
    // endregion

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
        else if (getView() != null)
            ViewUtils.showSnackBar(getView(), getString(R.string.error_message), Snackbar.LENGTH_SHORT);
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

    /**
     * If server response code is unauthorized (session expired)
     */
    private void showSessionExpiredDialog() {
        SharedPreferenceManager.getInstance().clearPreferences();
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Session expired");
        builder.setMessage("Your session got expired kindly sign in again.");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (getActivity() != null) {
//                    getActivity().startActivity(new Intent(getActivity(), MobileEnterActivity.class));
                    getActivity().finish();
                }
            }
        });
        if (getView() != null && getActivity() != null) {
            dialog = builder.create();
            dialog.show();
        }
    }

    public void showSnackBar(String message) {
        if (getView() == null)
            return;
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Create and return a waiting progress dialog
     *
     * @param cancelable is dialog cancelable
     * @return a new progress dialog
     */
    public ProgressDialog getProgressDialog(boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please wait!");
        dialog.setCancelable(cancelable);
        return dialog;
    }
}
