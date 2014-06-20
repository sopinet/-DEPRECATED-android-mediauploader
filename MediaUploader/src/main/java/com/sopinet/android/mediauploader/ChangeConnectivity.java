package com.sopinet.android.mediauploader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ChangeConnectivity extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent localIntent = new Intent().setClassName(MediaUploader.SENDINGCONTEXT, MediaUploader.SENDINGCLASS);
        if (UtilsHelper.isOnline(context)) {
        	HttpPostHelper.execNotify(context, localIntent);
        } else {
        	HttpPostHelper.cancelNotify(context, localIntent);
        }
    }

}