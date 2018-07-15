package theawesomebox.com.app.awesomebox.apps.module.ui.home;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.apps.data.models.backup.Path;
import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;
import theawesomebox.com.app.awesomebox.apps.network.AppNetworkTask;
import theawesomebox.com.app.awesomebox.apps.network.HttpRequestItem;
import theawesomebox.com.app.awesomebox.apps.network.HttpResponseItem;
import theawesomebox.com.app.awesomebox.apps.network.NetworkUtils;
import theawesomebox.com.app.awesomebox.common.base.BaseFragment;
import theawesomebox.com.app.awesomebox.common.utils.AppConstants;
import theawesomebox.com.app.awesomebox.common.utils.AppUtils;
import theawesomebox.com.app.awesomebox.common.utils.DateUtils;
import theawesomebox.com.app.awesomebox.common.utils.Logger;

/**
 * A simple {@link Fragment} subclass.
 */
public class BackupDetailsFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    BackupDataAdapter adapter;
    TextView mTextView;
    RelativeLayout mRootLayout;
    ToolTipsManager mToolTipsManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ToolTip.Builder builder;
    private boolean fromMain = false;
    private String USER_BACKUP_URL;

    String tooltipMessage;

    public BackupDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fromMain = getArguments().getBoolean("fromMain", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the row_backup_files for this fragment
        return inflater.inflate(R.layout.fragment_backup_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        initializeRecyclerView();
        bindListeners(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        runTimer();

    }

    private void runTimer() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            //your method here
                            Log.e("API CALLED:", "CALL SUCCESS");
                            backupDataApi();
                        } catch (Exception e) {
                            Log.e("Timer Exception: ", e.getMessage() + "  oooo");
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000); //execute in every 1 minute
    }

    private void bindListeners(View view) {
        view.findViewById(R.id.txt_bkup_status).setOnClickListener(this);
        mSwipeRefreshLayout.setRefreshing(true);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.postDelayed(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(false);

                // Fetching data from server

            }
        }, 2000);
    }

    private void bindViews(View view) {
        tooltipMessage = "Obtaining backup status from desktop, ensure desktop app is running";

        recyclerView = view.findViewById(R.id.recycler_view_backup_files);
        mToolTipsManager = new ToolTipsManager();
        mTextView = view.findViewById(R.id.txt_bkup_status);
        mRootLayout = view.findViewById(R.id.rel_backup_details);
        mToolTipsManager.findAndDismiss(mTextView);

        mToolTipsManager.findAndDismiss(mTextView);
        builder = new ToolTip.Builder(getActivity(), mTextView, mRootLayout, tooltipMessage, ToolTip.POSITION_ABOVE);
        builder.setAlign(ToolTip.ALIGN_CENTER);
        builder.setBackgroundColor(getResources().getColor(R.color.black));
        builder.setGravity(ToolTip.GRAVITY_CENTER);
        mToolTipsManager.show(builder.build());

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

    private void initializeRecyclerView() {

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        //recyclerView.addItemDecoration(new GridSpacingItemDecorator(2, AppUtils.dpToPx(5, getActivity()), true));

    }

    private void populateList(List<Path> pathList) {
        if (adapter == null) {
            adapter = new BackupDataAdapter(pathList, this, getActivity());
            recyclerView.setAdapter(adapter);
        } else {
//            adapter.addItems(null);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public String getTitle() {
        if (fromMain)
            return getString(R.string.backup_title);
        else
            return getString(R.string.home_menu);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_bkup_status:
                mToolTipsManager.findAndDismiss(mTextView);
                builder = new ToolTip.Builder(getActivity(), mTextView, mRootLayout, tooltipMessage, ToolTip.POSITION_ABOVE);
                builder.setAlign(ToolTip.ALIGN_CENTER);
                builder.setBackgroundColor(getResources().getColor(R.color.black));
                builder.setGravity(ToolTip.GRAVITY_CENTER);
                mToolTipsManager.show(builder.build());
                break;
        }
    }

    @Override
    public void onRefresh() {
        // Fetching data from server
        backupDataApi();
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
            Log.e("BACKUP RESPONSE ::", responseJson.toString());

            if (response.getHttpRequestUrl().equalsIgnoreCase(AppConstants.getServerUrl(USER_BACKUP_URL))) {

                if (responseJson.getBoolean("type")) {
                    if (!responseJson.isNull("data") && responseJson.getJSONObject("data").length() > 0) {
                        JSONObject results = responseJson.getJSONObject("data");
                        if (results != null && results.length() > 0) {

                            fillData(responseJson.getJSONObject("data"));
                            if (!responseJson.getJSONObject("data").isNull("path") &&
                                    responseJson.getJSONObject("data").getJSONArray("path").length() > 0) {
                                JSONArray object = responseJson.getJSONObject("data").getJSONArray("path");

                                List<Path> pathList = new Gson().fromJson(object.toString(),
                                        new TypeToken<List<Path>>() {
                                        }.getType());

                                populateList(pathList);

                                if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing())
                                    mSwipeRefreshLayout.setRefreshing(false);

                            }
                        }
                    }
                }

            }

        } catch (JSONException e) {
            Logger.error(false, e);
        }
    }

    private void fillData(JSONObject data) {
        try {
            String files = data.getString("folderCount") + " Directory, " + data.getString("fileCount") + " File";
            String date = data.getString("last_backup");

            long used = data.getJSONObject("sizeBytes").getLong("used");
            long allocated = data.getJSONObject("sizeBytes").getLong("allocated");

            Log.e("USED:", humanReadableByteCount(used, true));
            Log.e("ALLOCATED:", humanReadableByteCount(allocated, true));

            String usedS = humanReadableByteCount(used, true);
            String allocatedS = humanReadableByteCount(allocated, true);

            ((TextView) getView().findViewById(R.id.txt_file_counts)).setText(files);
            ((TextView) getView().findViewById(R.id.txt_backup_date)).setText(date);
            ((TextView) getView().findViewById(R.id.txt_backup_size)).setText(usedS + " used of " + allocatedS);
            ((TextView) getView().findViewById(R.id.txt_bkup_status)).performClick();

            String timeStampFromApi = data.getString("nextTime");

            processDateTime(timeStampFromApi);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void processDateTime(String timeStampFromApi) {

        String date = DateUtils.getCurrentDate(DateUtils.UTC_TIME_MILLIS_1);
        Log.e("API DATE:", timeStampFromApi);
        Log.e("Current DATE:", date);

        Date d1 = DateUtils.stringToDate(timeStampFromApi, DateUtils.UTC_TIME_MILLIS_1);
        Date d2 = DateUtils.stringToDate(date, DateUtils.UTC_TIME_MILLIS_1);

        printDifference(d1, d2);


    }

    //1 minute = 60 seconds
//1 hour = 60 x 60 = 3600
//1 day = 3600 x 24 = 86400
    public void printDifference(Date nextBackUpDate, Date currentDate) {
        //milliseconds
        long different = nextBackUpDate.getTime() - currentDate.getTime();

        System.out.println("startDate : " + nextBackUpDate);
        System.out.println("endDate : " + currentDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        if (elapsedSeconds < 0) {

            tooltipMessage = "Backup not set";
            ((TextView) getView().findViewById(R.id.txt_bkup_status)).performClick();
        } else {
            tooltipMessage = "NEXT BACKUP: " + elapsedDays + " Days :" + elapsedHours + " Hours : " + elapsedMinutes + " Minutes";

            mToolTipsManager.dismissAll();
            ((TextView) getView().findViewById(R.id.txt_bkup_status)).setText(tooltipMessage);
        }
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
