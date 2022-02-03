package com.zitech.audio.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RefreshBatteryLevelReceiver extends BroadcastReceiver {

    public static final String REFRESH_BATTERY_LEVEL = "com.audio.REFRESH_BATTERY_LEVEL";

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e("reali", "LiveWaterLevelReceiver onReceive");
    }
}
