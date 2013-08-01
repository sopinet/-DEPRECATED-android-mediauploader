package com.sopinet.mediauploader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ChangeConnectivity extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Activity act = (Activity) context;
        /* TODO: Config CLASS */
        Intent localIntent = new Intent().setClass(context, SendingActivity.class);
        if (UtilsHelper.isOnline(context)) {
        	HttpPostHelper.execNotify(context, localIntent);
        } else {
        	HttpPostHelper.cancelNotify(context, localIntent);
        }
    }

}