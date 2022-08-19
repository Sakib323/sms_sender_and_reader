package com.itsolution.sms_reader_demo_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

public class turn_on_mobile_data extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d("NetworkCheckReceiver", "NetworkCheckReceiver invoked...");

            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (!noConnectivity) {
                Intent intent1=new Intent(context,MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                Log.d("NetworkCheckReceiver", "connected");
                Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
            }
            else
            {

            }
        }
    }
}
