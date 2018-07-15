package theawesomebox.com.app.awesomebox.apps.module.ui.others;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;
import theawesomebox.com.app.awesomebox.apps.module.ui.home.MainActivity;
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
public class LogoutFragment extends BaseFragment implements View.OnClickListener {

    private String URL_LOGOUT;

    public LogoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the row_backup_files for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
    }

    private void bindViews(View view) {

        view.findViewById(R.id.btn_logout).setOnClickListener(this);
    }

    @Override
    public String getTitle() {
        return getString(R.string.logout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:
                logoutApiCall();
                break;
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

    @Override
    public void onNetworkSuccess(HttpResponseItem response) {
        super.onNetworkSuccess(response);
        try {
            JSONObject responseJson = new JSONObject(response.getResponse());
            Log.e("LOGOUT RESPONSE ::", responseJson.toString());

            if (response.getHttpRequestUrl().equalsIgnoreCase(AppConstants.getServerUrl(URL_LOGOUT))) {
                SharedPreferenceManager.getInstance().clearPreferences();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }

        } catch (JSONException e) {
            Logger.error(false, e);
        }
    }
}
