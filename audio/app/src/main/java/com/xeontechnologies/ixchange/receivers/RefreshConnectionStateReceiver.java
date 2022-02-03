package com.zitech.audio.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RefreshConnectionStateReceiver extends BroadcastReceiver {

    public static final String REFRESH_CONNECTION_STATE = "com.audio.REFRESH_CONNECTION_STATE";

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e("reali", "LiveWaterLevelReceiver onReceive");
    }
}
