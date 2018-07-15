package theawesomebox.com.app.awesomebox.apps.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = NetworkUtil.getConnectivityStatusString(context);

//        Toast.makeText(context, status, Toast.LENGTH_LONG).show();

//        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
//        context.getApplicationContext().registerReceiver(null, intentFilter);
    }
}