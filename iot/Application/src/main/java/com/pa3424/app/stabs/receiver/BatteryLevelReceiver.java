package com.pa3424.app.stabs1.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ali on 3/3/19.
 */
public class BatteryLevelReceiver extends BroadcastReceiver {

    public static final String BATTERY_LEVEL = "com.pa3424.app.stabs1.BATTERY_LEVEL";

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e("reali", "BatteryLevelReceiver onReceive");
    }
}
