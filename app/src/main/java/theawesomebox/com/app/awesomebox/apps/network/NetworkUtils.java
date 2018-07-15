package theawesomebox.com.app.awesomebox.apps.network;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.annotation.WorkerThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;
import theawesomebox.com.app.awesomebox.common.utils.AppUtils;
import theawesomebox.com.app.awesomebox.common.utils.BitmapUtils;
import theawesomebox.com.app.awesomebox.common.utils.Logger;

import static android.content.Context.CONNECTIVITY_SERVICE;

public final class NetworkUtils {

    /**
     * HTTP_GET for {@link HttpURLConnection#setRequestMethod(String)}
     */
    public static final String HTTP_GET = "GET";
    /**
     * HTTP_POST for {@link HttpURLConnection#setRequestMethod(String)}
     */
    public static final String HTTP_POST = "POST";
    public static final String HTTP_PUT = "PUT";
    /**
     * HTTP_MULTIPART for {@link HttpURLConnection#setRequestMethod(String)}
     */
    public static final String HTTP_MULTIPART = "IMAGE";
    /**
     * HTTP timeout value
     */
    public static final int HTTP_TIMEOUT = 20000; // milli seconds
    /**
     * HTTP Multipart timeout value
     */
    public static final int HTTP_MULTIPART_TIMEOUT = 60000; // milli seconds
    /**
     * Max retries for given HTTP request
     */
    public static final int MAX_RETRY_CONNECTIONS = 2;
    /**
     * Charset for
     */
    public static final String CHARSET = "UTF-8";
    /**
     * Default error code for exception at any level/method while parsing/create HTTP response
     */
    private static final int HTTP_EXCEPTION = -1;

    /**
     * This method takes {@link HttpRequestItem} as a input.
     * Get below values from {@link HttpRequestItem}
     * 1.  HTTP_GET or HTTP_POST
     * 2.  Http service/api name via {@link HttpRequestItem#getHttpRequestUrl()}
     * 3.  Initialize http connection using {@link #initHttpURLConnection(HttpRequestItem)}
     * 4.  Read and return response from {@link HttpURLConnection} via {@link #getHttpURLConnectionResponse(HttpURLConnection)}
     *
     * @param params {@link HttpRequestItem}
     * @return network response string
     */
    @WorkerThread
    public static String executeNetworkRequest(final HttpRequestItem params) {
        final String TAG = "NetworkUtils.executeNetworkRequest";

        // region HTTP_TIMEOUT
        // Create ExecutorService from a cachedThreadPool and submit a Callable/Future reference.
        final ExecutorService executor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
        final Future<String> httpFuture = executor.submit(new Callable<String>() {

            @Override
            public String call() throws Exception {
                return processHttpRequest(params, 0);
            }
        });
        // endregion

        // get service/api name from HttpRequestItem
        String serviceName = params.getHttpRequestUrl();
        // extract api/service name
        serviceName = AppUtils.ifNotNullEmpty(serviceName) ? "\"" + serviceName.substring(serviceName.lastIndexOf("/") + 1, serviceName.length()) + "\"" : "";

        // default message
        String formattedResponse = getResponseMessage("Request time out", HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        try {
            Logger.info(TAG, "getting response using future for " + serviceName);
            // wait for maximum timeout for current request and read data/response from HttpURLConnection
            formattedResponse = httpFuture.get((params.getHttpRequestTimeout() == 0 ? HTTP_TIMEOUT : params.getHttpRequestTimeout())
                    , TimeUnit.MILLISECONDS);
            Logger.info(TAG, "we have http response using future for " + serviceName + " Yey");
        } catch (Exception e) {
            Logger.error(false, e);
        } finally {
            // cancel future task
            try {
                httpFuture.cancel(true);
            } catch (Exception e) {
                Logger.error(false, e);
            }
            // shutdown executor service
            try {
                executor.shutdownNow();
            } catch (Exception e) {
                Logger.error(false, e);
            }
        }
        return formattedResponse;
    }

    /**
     * For some (unknown) reasons we've {@link EOFException}, to overcome we are retrying current
     * http request for max {@link #MAX_RETRY_CONNECTIONS} times
     *
     * @param params     {@link HttpRequestItem}
     * @param retryCount http retry count
     * @return network response string
     */
    private static String processHttpRequest(HttpRequestItem params, int retryCount) {
        HttpURLConnection connection = null;
        final String TAG = "InternetUtils.processHttpRequest";
        // default/error response
        String response = "";
        try {
            Logger.info(TAG, "initializing HttpURLConnection");
            // initializing HttpURLConnection with given params and charset
            connection = initHttpURLConnection(params);

            Logger.info(TAG, "initialized http connection, now getting http response...");
            // read http response from HttpURLConnection#getInputStream and covert this stream into string
            response = getHttpURLConnectionResponse(connection);
            // Logger.info(TAG, "we have http response, Yey");
        } catch (Exception exception) {
            Logger.error(true, exception);
            // retry this whole request
            if (exception instanceof EOFException && retryCount <= MAX_RETRY_CONNECTIONS) {
                Logger.error(TAG, "Retries " + retryCount + ", " + exception.toString());
                disconnectHttpURLConnection(connection);
                connection = null;
                return processHttpRequest(params, retryCount + 1);
            }
            // there is some error either from server connectivity, HTTP response parsing, and/or input stream exception.
            // return with HTTP_EXCEPTION a
            response = getResponseMessage("Invalid HTTP response", HTTP_EXCEPTION);
        } finally {
            // all we'r doing is disconnecting HttpURLConnection
            disconnectHttpURLConnection(connection);
        }
        return response;
    }

    /**
     * Check for http data/response validity if its valid return formatted json response
     * otherwise check for some basic errors and return with HttpURLConnection error code
     * and custom error message.
     *
     * @param connection HttpURLConnection
     * @return formatted json response
     */
    private static String getHttpURLConnectionResponse(HttpURLConnection connection) throws IOException {
        String response;
        InputStream inputStream = null;
        String TAG = "NetworkUtils.getHttpURLConnectionResponse";
        Logger.info(TAG, "executing network request");
        try {
            Logger.info(TAG, "connecting HttpURLConnection");
            connection.connect();
            Logger.info(TAG, "connected HttpURLConnection");

            // read response from HttpURLConnection
            int responseCode = connection.getResponseCode();
            String cookie = "";
            if (connection.getHeaderFields().containsKey("set-cookie") &&
                    !connection.getHeaderField("set-cookie").equals(null) &&
                    !connection.getHeaderField("set-cookie").equals("")) {
                cookie = connection.getHeaderField("set-cookie");
                cookie = cookie.substring(0, cookie.indexOf(';'));
                Logger.info("Cookie", "" + connection.getHeaderField("set-cookie"));
                SharedPreferenceManager.getInstance().save(SharedPreferenceManager.KEY_COOKIE, cookie);
            }

            // if response code from one of below, consider them as error, return HttpURLConnection error code
            // and custom error message.

            // NOTE:
            // we can add as many error(s) here
            switch (responseCode) {
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    return getResponseMessage("No server found", HttpURLConnection.HTTP_INTERNAL_ERROR);
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                    return getResponseMessage("Request time out", HttpURLConnection.HTTP_CLIENT_TIMEOUT);
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    return getResponseMessage("Request time out", HttpURLConnection.HTTP_GATEWAY_TIMEOUT);
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    return getResponseMessage("Session Expired", HttpURLConnection.HTTP_UNAUTHORIZED);
            }

            Logger.info(TAG, "getting InputStream");
            inputStream = connection.getInputStream();

            Logger.info(TAG, "converting stream to readable format");
            response = convertStream(inputStream);

            // check for response nullity
            if (AppUtils.ifNotNullEmpty(response))
                response = getResponseMessage(response, HttpURLConnection.HTTP_OK);
            else
                response = getResponseMessage("No service response", HttpURLConnection.HTTP_LENGTH_REQUIRED);

        } /*catch (Exception e) {
            // there is some error either from server connectivity, HTTP response parsing, and/or input stream exception.
            // return with HTTP_EXCEPTION a
            response = getResponseMessage("Invalid HTTP response", HTTP_EXCEPTION);
            Logger.error(true, e);
        } */ finally {
            if (inputStream != null) {
                // close stream
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Logger.error(false, e);
                }
            }
        }
        return response;
    }

    /**
     * Create {@link HttpURLConnection} from {@link HttpRequestItem}.
     * Steps:
     * 1.   Read request type.
     * 2.   For HTTP_GET append params into given url using {@link #getQueryParams(Map, String)}
     * 3.   For HTTP_POST add params into {@link HttpURLConnection#getOutputStream()} via {@link DataOutputStream}
     *
     * @param params {@link HttpRequestItem}
     * @return HttpURLConnection
     * @throws IOException exception
     */
    @NonNull
    private static HttpURLConnection initHttpURLConnection(HttpRequestItem params) throws Exception {
        String TAG = "InternetUtils.initHttpURLConnection";

        boolean isGetRequest = isGetRequest(params);
        String queryParams = getQueryParams(params.getParams(), CHARSET);

        URL url;
        if (isGetRequest)
            url = new URL(params.getHttpRequestUrl() + (AppUtils.ifNotNullEmpty(queryParams) ? "?" + queryParams : ""));
        else
            url = new URL(params.getHttpRequestUrl());

        // create HttpURLConnection from given URL
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(HTTP_TIMEOUT);
        httpURLConnection.setReadTimeout(HTTP_TIMEOUT);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestProperty("Accept-Charset", CHARSET);
        // httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        setHeaderParams(httpURLConnection, params.getHeaderParams());

        if (isGetRequest)
            httpURLConnection.setRequestMethod(params.getHttpRequestType());
        else {
            if (params.getHttpRequestType().equalsIgnoreCase(NetworkUtils.HTTP_MULTIPART)) {
                addMultipartEntity(params, httpURLConnection);
            } else {
                //don't put a check of (null and empty) here ! if params of post req are empty then it will never ever set the req type to post !
                //if (AppUtils.ifNotNullEmpty(queryParams)) {
                httpURLConnection.setDoOutput(true);
                //here it is setting the request type to post
                httpURLConnection.setRequestMethod(params.getHttpRequestType());
                httpURLConnection.setRequestProperty("Content-Length", Integer.toString(queryParams.length()));
                DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.write(queryParams.getBytes(CHARSET));
                dataOutputStream.flush();
                dataOutputStream.close();
                //  } else
//                Logger.warn(TAG, "No request params for HTTP POST");
            }
        }
        return httpURLConnection;
    }

    private static void addMultipartEntity(HttpRequestItem item, HttpURLConnection connection) throws Exception {
        DataOutputStream outputStream = null;

        String twoHyphens = "--";
        String boundary = "*****";
        String lineEnd = "\r\n";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 2 * 1024 * 1024;

        File file = new File(item.getParams().get("path").toString());
        Logger.debug("File", "" + file.length() / (1024 * 1024));
        BitmapUtils.compressBitmap(file);
        Logger.debug("File", "" + file.length() / (1024 * 1024));
        FileInputStream fileInputStream = new FileInputStream(file);

        connection.setConnectTimeout(HTTP_MULTIPART_TIMEOUT);
        connection.setReadTimeout(HTTP_MULTIPART_TIMEOUT);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "image" + "\"; filename=\"" + "name" + "\"" + lineEnd);
        outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
        outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
        outputStream.writeBytes(lineEnd);

        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];

        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            outputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        outputStream.writeBytes(lineEnd);
        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        fileInputStream.close();
        outputStream.flush();
        outputStream.close();
    }

    private static void disconnectHttpURLConnection(HttpURLConnection connection) {
        try {
            if (connection != null)
                connection.disconnect();
            Logger.info("disconnectHttpURLConnection", "HttpURLConnection is disconnected");
        } catch (Exception e) {
            Logger.error(true, e);
        }
    }

    private static boolean isGetRequest(HttpRequestItem params) {
        return params.getHttpRequestType().equalsIgnoreCase(HTTP_GET);
    }

    /**
     * Create a default/generic response using response code and Http response string
     *
     * @param data         String response from {@link HttpURLConnection#getInputStream()}
     * @param responseCode {@link HttpURLConnection#getResponseCode()}
     * @return Formatted json response
     */
    private static String getResponseMessage(String data, int responseCode) {
        try {
            JSONObject json = new JSONObject();
            json.put("data", data);
            json.put("response_code", responseCode);
            return json.toString();
        } catch (JSONException e) {
            Logger.error(false, e);
        }
        return "";
    }

    /**
     * @param map     {@link java.util.HashMap} contains name-value pair of http prams like name=john doe
     * @param charset {@link URLEncoder}
     * @return formatted query params
     * @throws UnsupportedEncodingException Exception while parsing params
     */
    private static String getQueryParams(Map<String, Object> map, String charset) throws UnsupportedEncodingException {
        if (map != null && map.size() > 0) {
            StringBuilder params = new StringBuilder();
            for (Map.Entry<String, Object> entry : map.entrySet())
                params.append(String.format("%s=%s&", entry.getKey(),
//                        entry.getValue().toString()));
                        URLEncoder.encode(entry.getValue().toString(), charset)));
            params = new StringBuilder(params.substring(0, params.lastIndexOf("&")));
            return params.toString();
        } else
            return "";
    }

    /**
     * @param stream {@link HttpURLConnection#getInputStream()}
     * @return Formatted string response or NULL
     * @throws IOException Exception while parsing http response
     */
    private static String convertStream(InputStream stream) throws IOException {
        if (stream != null) {
            BufferedReader bufferedReader = null;
            String line;
            StringBuilder builder = new StringBuilder();
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                while ((line = bufferedReader.readLine()) != null)
                    builder.append(line).append("\n");
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    Logger.error(false, e);
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Logger.error(false, e);
                    }
                }
            }
            return builder.toString();
        } else
            return null;
    }

    /**
     * Add header params via {@link HttpURLConnection#addRequestProperty(String, String)}
     *
     * @param connection {@link HttpURLConnection}
     * @param map        {@link Map} name-value pair params
     * @throws UnsupportedEncodingException exception while parsing
     */
    private static void setHeaderParams(HttpURLConnection connection, Map<String, String> map) throws UnsupportedEncodingException {
        if (map != null && map.size() > 0)
            for (Map.Entry<String, String> entry : map.entrySet())
                connection.addRequestProperty(entry.getKey(), entry.getValue());
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean hasNetworkConnection(Context context, boolean includeMobile) {
        if (context == null) return false;
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (manager != null) {
            activeNetwork = manager.getActiveNetworkInfo();
        }
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return activeNetwork.isConnected();
            if (includeMobile && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return activeNetwork.isConnected();
        }
        return false;
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static int getConnectedNetwork(Context context) {
        if (context == null) return -1;
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (manager != null) {
            activeNetwork = manager.getActiveNetworkInfo();
        }
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnected())
                return ConnectivityManager.TYPE_WIFI;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return ConnectivityManager.TYPE_MOBILE;
        }
        return -1;
    }
}
