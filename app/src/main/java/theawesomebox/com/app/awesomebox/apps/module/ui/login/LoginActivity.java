package theawesomebox.com.app.awesomebox.apps.module.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import theawesomebox.com.app.awesomebox.MyApplication;
import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.apps.data.preferences.SharedPreferenceManager;
import theawesomebox.com.app.awesomebox.apps.module.support.AppController;
import theawesomebox.com.app.awesomebox.apps.module.ui.home.MainActivity;
import theawesomebox.com.app.awesomebox.apps.network.AppNetworkTask;
import theawesomebox.com.app.awesomebox.apps.network.HttpRequestItem;
import theawesomebox.com.app.awesomebox.apps.network.HttpResponseItem;
import theawesomebox.com.app.awesomebox.apps.network.NetworkUtils;
import theawesomebox.com.app.awesomebox.common.base.BaseActivity;
import theawesomebox.com.app.awesomebox.common.utils.AppConstants;
import theawesomebox.com.app.awesomebox.common.utils.AppUtils;
import theawesomebox.com.app.awesomebox.common.utils.Logger;

public class LoginActivity extends BaseActivity implements View.OnClickListener {


    final String SiteKey = "6Lerp1UUAAAAAINiPA4AzVg4zODsQ-wX_Xpccs70";
    final String SecretKey = "6Lerp1UUAAAAAHhJs-_QKr8v3lwf7_xubs6qpKct";


    //second approach

    // TODO - replace the SITE KEY with yours
    private static final String SAFETY_NET_API_SITE_KEY = "6Lerp1UUAAAAAINiPA4AzVg4zODsQ-wX_Xpccs70";

    // TODO - replace the SERVER URL with yours
    private static final String URL_VERIFY_ON_SERVER = "https://awesomeboxtech.com/";

    String userResponseToken;
    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bundListeners();
    }

    private void bundListeners() {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.txt_sign_up).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                reCaptcha();
                break;
            case R.id.txt_sign_up:
                openSignUpPage();
                break;
        }
    }

    private void openSignUpPage() {
        String url = "https://awesomeboxtech.com/";
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void reCaptcha() {
//        SafetyNet.SafetyNetApi.verifyWithRecaptcha(mGoogleApiClient, SiteKey)
//                .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
//                    @Override
//                    public void onResult(SafetyNetApi.RecaptchaTokenResult result) {
//                        Status status = result.getStatus();
//
//                        if ((status != null) && status.isSuccess()) {
//
//                            Log.e("CAPTCHA:", "isSuccess()\n");
//                            // Indicates communication with reCAPTCHA service was
//                            // successful. Use result.getTokenResult() to get the
//                            // user response token if the user has completed
//                            // the CAPTCHA.
//
//                            if (!result.getTokenResult().isEmpty()) {
//                                Log.e("CAPTCHA:", "!result.getTokenResult().isEmpty()");
//                                // User response token must be validated using the
//                                // reCAPTCHA site verify API.
//                            } else {
//                                Log.e("CAPTCHA:", "result.getTokenResult().isEmpty()");
//                            }
//                        } else {
//
//                            Log.e("MY_APP_TAG", "Error occurred " +
//                                    "when communicating with the reCAPTCHA service.");
//
//                            // Use status.getStatusCode() to determine the exact
//                            // error code. Use this code in conjunction with the
//                            // information in the "Handling communication errors"
//                            // section of this document to take appropriate action
//                            // in your app.
//                        }
//                    }
//                });

        //TODO
        SafetyNet.getClient(this).verifyWithRecaptcha(SiteKey)
                .addOnSuccessListener(this,
                        new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                // Indicates communication with reCAPTCHA service was
                                // successful.
                                userResponseToken = response.getTokenResult();
                                if (!userResponseToken.isEmpty()) {
                                    // Validate the user response token using the
                                    // reCAPTCHA siteverify API.
                                    // new SendPostRequest().execute();

                                    Log.e("CAPTCHA TOKEN: ", userResponseToken);

                                    loginApiCall(userResponseToken);
                                    //  sendRequest();
                                }
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.e(TAG, "Error: " + CommonStatusCodes
                                    .getStatusCodeString(statusCode));
                        } else {
                            // A different, unknown type of error occurred.
                            Log.e(TAG, "Error: " + e.getMessage());
                        }
                    }
                });
    }

    private void loginApiCall(String userResponseToken) {
        EditText edtEmail = findViewById(R.id.edt_login_email);
        EditText edtPass = findViewById(R.id.edt_login_pass);

        String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();

        if (!AppUtils.isEmailValid(email)) {
            Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
            return;
        } else {


            HttpRequestItem requestItem = new HttpRequestItem(
                    AppConstants.getServerUrl(AppConstants.LOGIN));
            requestItem.setHttpRequestType(NetworkUtils.HTTP_POST);     //by default its GET
//        requestItem.setHeaderParams(AppUtils.getHeaderParams());    // Add header params (Cookies)
            // Add post body parameters OR get query parameters here
            Map<String, Object> params = new HashMap<>();
            params.put("email", email);
            params.put("password", pass);
            params.put("deviceType", "android");
            params.put("response", userResponseToken);

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
            Log.e("SIGN IN:::::", responseJson.toString());

            if (response.getHttpRequestUrl().equalsIgnoreCase(AppConstants.getServerUrl(AppConstants.LOGIN))) {
                if (!responseJson.isNull("data") && responseJson.getJSONObject("data").length() > 0) {

                    String userToken = responseJson.getJSONObject("data").getString("token");
                    int userId = responseJson.getJSONObject("data").getInt("userid");
                    Log.e("USER TOKEN:", "@@@@@ TOKEN = " + userToken);
                    Log.e("USER ID:", "@@@@@ ID = " + userId);

                    SharedPreferenceManager.getInstance().save(SharedPreferenceManager.KEY_IS_LOGGED_IN, true);
                    SharedPreferenceManager.getInstance().save(SharedPreferenceManager.KEY_USER_TOKEN, userToken);
                    SharedPreferenceManager.getInstance().save(SharedPreferenceManager.KEY_USER_ID, userId);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

            }

        } catch (JSONException e) {
            Logger.error(false, e);
        }
    }

    public void sendRequest() {

        // String url = "https://www.google.com/recaptcha/api/siteverify";
        String url = "https://awesomeboxtech.com/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            //Toast.makeText(MainActivity.this, obj.getString("success"), Toast.LENGTH_LONG).show();
                            if (obj.getString("success").equals("true")) {
                                moveNewActivity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("secret", SecretKey);
                params.put("response", userResponseToken);
                return params;
            }
        };
        AppController.getInstance(this).addToRequestQueue(stringRequest);


    }

    public void moveNewActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //second approach

    public void validateCaptcha() {

        // Showing reCAPTCHA dialog
        SafetyNet.getClient(this).verifyWithRecaptcha(SAFETY_NET_API_SITE_KEY)
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        Log.d(TAG, "onSuccess");

                        if (!response.getTokenResult().isEmpty()) {

                            // Received captcha token
                            // This token still needs to be validated on the server
                            // using the SECRET key
                            verifyTokenOnServer(response.getTokenResult());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.d(TAG, "Error message: " +
                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            Log.d(TAG, "Unknown type of error: " + e.getMessage());
                        }
                    }
                });
    }

    /**
     * Verifying the captcha token on the server
     * Post param: recaptcha-response
     * Server makes call to https://www.google.com/recaptcha/api/siteverify
     * with SECRET Key and Captcha token
     */
    public void verifyTokenOnServer(final String token) {
        Log.d(TAG, "Captcha Token" + token);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_VERIFY_ON_SERVER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");

                    if (success) {
                        // Congrats! captcha verified successfully on server
                        // TODO - submit the feedback to your server
                        Toast.makeText(getApplicationContext(), "SECCESS", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("recaptcha-response", token);

                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(strReq);
    }
}
