package theawesomebox.com.app.awesomebox.apps.network;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

import theawesomebox.com.app.awesomebox.apps.business.OnNetworkTaskListener;
import theawesomebox.com.app.awesomebox.common.utils.AppUtils;
import theawesomebox.com.app.awesomebox.common.utils.Logger;


public final class AppNetworkTask extends AsyncTask<HttpRequestItem, Void, HttpResponseItem> {

    private ProgressDialog dialog = null;
    private String httpRequestUrl = null;
    private WeakReference<OnNetworkTaskListener> onNetworkTaskListener = null;

    public AppNetworkTask(ProgressDialog dialog, @NonNull OnNetworkTaskListener taskListener) {
        this.dialog = dialog;
        this.onNetworkTaskListener = new WeakReference<>(taskListener);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (dialog != null) {
            dialog.setMessage("Please wait");
            dialog.show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        OnNetworkTaskListener listener = getNetworkTaskListener();
        if (listener != null)
            listener.onNetworkCanceled(getDefaultHttpResponse("Operation canceled by user"));
        clearResources();
    }

    @Override
    protected HttpResponseItem doInBackground(HttpRequestItem... params) {
        HttpRequestItem httpRequestItem = params[0];
        if (httpRequestItem == null)
            return getDefaultHttpResponse("http request item is null");

        // region INTERNET_CONNECTIVITY_CHECK
        OnNetworkTaskListener listener = getNetworkTaskListener();
        if (listener != null && !listener.isNetworkConnected()) {
            HttpResponseItem item = new HttpResponseItem(HttpURLConnection.HTTP_UNAVAILABLE, "http unavailable/not connected");
            item.setHttpRequestUrl(httpRequestItem.getHttpRequestUrl());
            return item;
        }
        // endregion

        // execute network request
        String networkResponse = NetworkUtils.executeNetworkRequest(httpRequestItem);
        // if we have some response from network executor service
        if (!AppUtils.ifNotNullEmpty(networkResponse))
            return getDefaultHttpResponse("network executor service returned nothing");
        try {
            JSONObject json = new JSONObject(networkResponse);
            if (json.has("response_code")) {
                int code = json.getInt("response_code");
                HttpResponseItem item = new HttpResponseItem();
                item.setResponseCode(code);
                item.setResponse(json.getString("data"));
                item.setHttpRequestUrl(httpRequestItem.getHttpRequestUrl());
                item.setHttpRequestType(httpRequestItem.getHttpRequestType());
                return item;
            } else
                Logger.error(getClass().getSimpleName(), "");
        } catch (JSONException e) {
            Logger.caughtException(e);
        }
        return getDefaultHttpResponse("Internal error");
    }

    @Override
    protected void onPostExecute(HttpResponseItem response) {
        super.onPostExecute(response);
        OnNetworkTaskListener listener = getNetworkTaskListener();
        if (response != null && listener != null)
            listener.onNetworkResponse(response);
        clearResources();
    }

    /**
     * Clear all resources
     */
    private void clearResources() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
        if (onNetworkTaskListener != null)
            onNetworkTaskListener.clear();
        onNetworkTaskListener = null;
    }

    private OnNetworkTaskListener getNetworkTaskListener() {
        if (onNetworkTaskListener != null)
            return onNetworkTaskListener.get();
        return null;
    }

    /**
     * Default {@link HttpResponseItem} for error
     *
     * @param error error string
     * @return HttpResponseItem
     */
    @NonNull
    private HttpResponseItem getDefaultHttpResponse(String error) {
        HttpResponseItem item = new HttpResponseItem(HttpURLConnection.HTTP_INTERNAL_ERROR, error);
        item.setHttpRequestUrl(httpRequestUrl);
        return item;
    }
}
