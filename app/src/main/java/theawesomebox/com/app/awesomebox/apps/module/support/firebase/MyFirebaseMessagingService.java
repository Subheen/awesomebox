package theawesomebox.com.app.awesomebox.apps.module.support.firebase;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import theawesomebox.com.app.awesomebox.R;

import theawesomebox.com.app.awesomebox.common.utils.AppConstants;


/**
 * Created by sabheengull on 11/09/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    String resourceId;
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "JobObject Payload: " + remoteMessage.getData().toString());

            try {
//                JSONObject resourceId= remoteMessage.get
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
//                Log.e("Converted : ", json.toString() );
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }


    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            Log.e("CHECK ", "notification");
            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
            Log.e("CHECK ", "notification");
        }
    }

    private void handleDataMessage(JSONObject json) {

        try {

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(AppConstants.NOTIFICATION_TYPE);
                pushNotification.putExtra(AppConstants.NOTIFICATION_TYPE, json.getJSONObject("default").getJSONObject("fcm").getString("type"));
               // pushNotification.putExtra(SocketService.BROADCAST_INTENT_DATA, json.toString());
                pushNotification.putExtra(AppConstants.JOB_ID, json.getJSONObject("default").getJSONObject("fcm").getString("data"));
                sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.showNotificationMessage(getResources().getString(R.string.app_name), json.getJSONObject("default").getJSONObject("fcm").getString("type"),
                        json.getJSONObject("default").getJSONObject("fcm").getString("alert"), "", json);


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
