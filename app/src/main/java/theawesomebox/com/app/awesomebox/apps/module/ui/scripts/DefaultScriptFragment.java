package theawesomebox.com.app.awesomebox.apps.module.ui.scripts;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.apps.data.models.scripts.Result;
import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;
import theawesomebox.com.app.awesomebox.apps.network.AppNetworkTask;
import theawesomebox.com.app.awesomebox.apps.network.HttpRequestItem;
import theawesomebox.com.app.awesomebox.apps.network.HttpResponseItem;
import theawesomebox.com.app.awesomebox.apps.network.NetworkUtils;
import theawesomebox.com.app.awesomebox.common.base.BaseFragment;
import theawesomebox.com.app.awesomebox.common.utils.AppConstants;
import theawesomebox.com.app.awesomebox.common.utils.AppUtils;
import theawesomebox.com.app.awesomebox.common.utils.ErrorMessages;
import theawesomebox.com.app.awesomebox.common.utils.Logger;

/**
 * A simple {@link Fragment} subclass.
 */
public class DefaultScriptFragment extends BaseFragment {

    RecyclerView recyclerView;
    ScriptsAdapter adapter;
    String scriptType;
    private String URL_GET_SCRIPT;
    private String searchText = "";
    private String URL_RUN_SCRIPT;
    String runScriptType;

    ProgressBar progressBar;
    TextView textCounter;
    private Handler handler = new Handler();
    private int progressStatus = 0;

    public DefaultScriptFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            scriptType = getArguments().getString("scriptType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_script, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) getView().findViewById(R.id.progressbar);

        setProgressBar();
        initializeRecyclerView();
        getScripts();
    }

    private void initializeRecyclerView() {

        recyclerView = getView().findViewById(R.id.recycler_view_script_default);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        //recyclerView.addItemDecoration(new GridSpacingItemDecorator(2, AppUtils.dpToPx(5, getActivity()), true));

    }


    public void sendSearchTextToFrags(String searchText, final int i) {
        this.searchText = searchText;
        Log.e("Overrided fun: ", "search text = " + searchText + " - search index ->" + i);


        switch (i) {
            case 0:
                scriptType = "default";
                getScripts();
                break;
            case 1:
                scriptType = "favorite";
                getScripts();
                break;
            case 2:
                scriptType = "optimize";
                getScripts();
                break;

            case 3:
                scriptType = "security";
                getScripts();
                break;
            case 4:
                scriptType = "health";
                getScripts();
                break;
            case 5:
                scriptType = "network";
                getScripts();
                break;
            case 6:
                scriptType = "connection";
                getScripts();
                break;
        }

    }

    private void populateList(final List<Result> scriptList) {
        //      if (adapter == null) {
        adapter = new ScriptsAdapter(scriptList, this, getActivity(), scriptType, new ClickListener() {
            @Override
            public void onPositionClicked(View view, int position) {

                switch (view.getId()) {
                    case R.id.btn_run_script:
                        // Toast.makeText(getActivity(), "Script run at index: " + position, Toast.LENGTH_SHORT).show();
                        String scriptName = scriptList.get(position).getScriptName();
                        int scriptId = scriptList.get(position).getScriptid();
                        runScriptType = scriptList.get(position).getScriptName();

                        runScriptApiCall(scriptId, scriptName);
                        break;
                }
            }

            @Override
            public void onLongClicked(int position) {

            }
        });
        recyclerView.setAdapter(adapter);

    }

    private void runScriptApiCall(int scriptId, String scriptName) {
        String userToken = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_TOKEN, "");


        if (AppUtils.ifNotNullEmpty(userToken)) {
            URL_RUN_SCRIPT = AppConstants.RUN_SCRIPTS;

            HttpRequestItem requestItem = new HttpRequestItem(
                    AppConstants.getServerUrl(URL_RUN_SCRIPT));

            requestItem.setHttpRequestType(NetworkUtils.HTTP_POST);     //by default its GET

            Map<String, Object> params = new HashMap<>();
            params.put("script_id", scriptId);
            params.put("script_name", scriptName);
            params.put("token", userToken);

            requestItem.setParams(params);

            Log.e("RUN SCRIPT PARAMS: ", params.toString());

            AppNetworkTask appNetworkTask = new AppNetworkTask(getProgressDialog(false),
                    this);
            appNetworkTask.execute(requestItem);
        }
    }

    @Override
    public String getTitle() {
        return null;
    }

    private void getScripts() {

        String userToken = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_TOKEN, "");
        int userId = SharedPreferenceManager.getInstance().read(SharedPreferenceManager.KEY_USER_ID, -1);

        if (userId != -1 && AppUtils.ifNotNullEmpty(userToken)) {

            URL_GET_SCRIPT = AppConstants.SCRIPTS + scriptType + "&search_text=" + searchText;
            HttpRequestItem requestItem = new HttpRequestItem(
                    AppConstants.getServerUrl(URL_GET_SCRIPT));
            requestItem.setHttpRequestType(NetworkUtils.HTTP_GET);     //by default its GET

            Map<String, Object> params = new HashMap<>();


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
            Log.e("SCRIPTS RESPONSE :: ", responseJson.toString());

            if (response.getHttpRequestUrl().equalsIgnoreCase(AppConstants.getServerUrl(URL_GET_SCRIPT))) {

                JSONArray results = responseJson.getJSONArray("result");
                if (results != null && results.length() > 0) {
                    List<Result> scriptList = new Gson().fromJson(results.toString(),
                            new TypeToken<List<Result>>() {
                            }.getType());

                    populateList(scriptList);
                }
            } else if (response.getHttpRequestUrl().equalsIgnoreCase(AppConstants.getServerUrl(URL_RUN_SCRIPT))) {

                if (responseJson.getBoolean("type")) {

                    if (responseJson.getJSONObject("data").getBoolean("status")) {
                        ErrorMessages.showPromptForTitleAndMessage("Success!", "Task " + runScriptType + " completed successfully.", getActivity());
                    } else {
                        ErrorMessages.showPromptForTitleAndMessage("Sorry!", "Task " + runScriptType + " failed to complete.", getActivity());
                    }

                } else {
                    ErrorMessages.showPromptForTitleAndMessage("Sorry!", "Your connected device is offline.", getActivity());
                }

            }

        } catch (JSONException e) {
            Logger.error(false, e);
        }
    }

    private void setProgressBar() {
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 5;
                    // Update the progress bar and display the
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
