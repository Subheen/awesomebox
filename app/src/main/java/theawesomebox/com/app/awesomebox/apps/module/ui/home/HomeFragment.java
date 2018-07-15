package theawesomebox.com.app.awesomebox.apps.module.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;
import theawesomebox.com.app.awesomebox.apps.module.ui.login.LoginActivity;
import theawesomebox.com.app.awesomebox.apps.network.AppNetworkTask;
import theawesomebox.com.app.awesomebox.apps.network.HttpRequestItem;
import theawesomebox.com.app.awesomebox.apps.network.HttpResponseItem;
import theawesomebox.com.app.awesomebox.apps.network.NetworkUtils;
import theawesomebox.com.app.awesomebox.common.base.BaseFragment;
import theawesomebox.com.app.awesomebox.common.utils.AppConstants;
import theawesomebox.com.app.awesomebox.common.utils.AppUtils;
import theawesomebox.com.app.awesomebox.common.utils.Logger;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {


    private String URL_LOGGED_IN;
    private String USER_BACKUP_URL;
    private String URL_LOGOUT;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the row_backup_files for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindListeners(view);
        loggedInApi();
        backupDataApi();
    }

    private void bindListeners(View view) {
        view.findViewById(R.id.ll_bottom_home).setOnClickListener(this);
    }

    @Override
    public String getTitle() {
        return getString(R.string.home_menu);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_bottom_home:
                getReference().onReplaceFragment(new BackupDetailsFragment(), true);
                break;
        }
    }

    private void loggedInApi() {

        String userToken = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_TOKEN, "");

        if (AppUtils.ifNotNullEmpty(userToken)) {
            URL_LOGGED_IN = AppConstants.LOGGED_IN + userToken;
            HttpRequestItem requestItem = new HttpRequestItem(
                    AppConstants.getServerUrl(URL_LOGGED_IN));
            requestItem.setHttpRequestType(NetworkUtils.HTTP_GET);     //by default its GET

            AppNetworkTask appNetworkTask = new AppNetworkTask(getProgressDialog(false),
                    this);
            appNetworkTask.execute(requestItem);
        }
    }

    private void backupDataApi() {

        String userToken = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_TOKEN, "");
        int userId = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_ID, -1);

        if (userId != -1 && AppUtils.ifNotNullEmpty(userToken)) {
            USER_BACKUP_URL = AppConstants.BACKUP_HOME + userId + "/backup?token=" + userToken;
            HttpRequestItem requestItem = new HttpRequestItem(
                    AppConstants.getServerUrl(USER_BACKUP_URL));
            requestItem.setHttpRequestType(NetworkUtils.HTTP_GET);     //by default its GET

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
            Log.e("HOME RESPONSE ::", responseJson.toString());

            if (response.getHttpRequestUrl().equalsIgnoreCase(AppConstants.getServerUrl(URL_LOGGED_IN))) {
                if (!responseJson.isNull("data") && responseJson.getJSONObject("data").length() > 0) {

                    populateView(responseJson.getJSONObject("data"));
                }

            } else if (response.getHttpRequestUrl().equalsIgnoreCase(AppConstants.getServerUrl(USER_BACKUP_URL))) {

                if (responseJson.getBoolean("type")) {
                    if (!responseJson.isNull("data") && responseJson.getJSONObject("data").length() > 0) {
                        if (!responseJson.getJSONObject("data").isNull("path") &&
                                responseJson.getJSONObject("data").getJSONArray("path").length() > 0) {
                            JSONObject object = responseJson.getJSONObject("data").getJSONArray("path").getJSONObject(0);
                            if (object.isNull("last_backup") ||
                                    !AppUtils.ifNotNullEmpty(object.getString("last_backup"))
                                    || object.getString("last_backup").matches("0")) {

                                ((TextView) getView().findViewById(R.id.txt_last_backup)).setText("Backup never done");
                            } else {
                                ((TextView) getView().findViewById(R.id.txt_last_backup)).setText("Last Backup: " + object.getString("last_backup"));
                            }
                        }
                    }
                } else {
                    logoutApiCall();
                }

            } else if (response.getHttpRequestUrl().equalsIgnoreCase(AppConstants.getServerUrl(URL_LOGOUT))) {
                SharedPreferenceManager.getInstance().clearPreferences();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }

        } catch (JSONException e) {
            Logger.error(false, e);
        }
    }

    private void logoutApiCall() {
        String userToken = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_TOKEN, "");
        int userId = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_ID, -1);

        if (userId != -1 && AppUtils.ifNotNullEmpty(userToken)) {


            URL_LOGOUT = AppConstants.SEND_TOKEN_TO_SERVER + userId + "?token=" + userToken;
            HttpRequestItem requestItem = new HttpRequestItem(
                    AppConstants.getServerUrl(URL_LOGOUT));
            requestItem.setHttpRequestType(NetworkUtils.HTTP_PUT);     //by default its GET
//        requestItem.setHeaderParams(AppUtils.getHeaderParams());    // Add header params (Cookies)
            // Add post body parameters OR get query parameters here
            Map<String, Object> params = new HashMap<>();
            params.put("phoneToken", "123Qwe");

            requestItem.setParams(params);

            AppNetworkTask appNetworkTask = new AppNetworkTask(getProgressDialog(false),
                    this);
            appNetworkTask.execute(requestItem);
        }
    }

    private void populateView(JSONObject data) {

        if (getView() == null)
            return;

        try {
            ((TextView) getView().findViewById(R.id.txt_home_user_name)).setText("Hi, " + data.getString("usr_fname"));
            ((TextView) getView().findViewById(R.id.txt_user_type)).setText(data.getJSONObject("role").getString("role_name"));
            ((TextView) getView().findViewById(R.id.txt_user_email)).setText(data.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
