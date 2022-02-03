package com.zitech.audio.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EnableNeverForgetReceiver extends BroadcastReceiver {

    public static final String ENABLE_NEVER_FORGET = "com.audio.ENABLE_NEVER_FORGET";

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e("reali", "LiveWaterLevelReceiver onReceive");
    }
}
