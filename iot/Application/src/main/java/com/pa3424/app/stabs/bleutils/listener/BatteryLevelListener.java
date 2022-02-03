package com.pa3424.app.stabs1.bleutils.listener;

import com.idevicesinc.sweetblue.BleDevice;
import com.pa3424.app.stabs1.bleutils.MyBottleManager;
import com.pa3424.app.stabs1.utils.Log;

/**
 * Created by ali on 9/20/19.
 */

public class BatteryLevelListener implements BleDevice.ReadWriteListener {

    @Override
    public void onEvent(ReadWriteEvent e) {
        if( e.wasSuccess() ) {

            int perc = e.data()[0];
            MyBottleManager.mInstance.setBatteryLevel(perc);

            Log.e("reali", "Battery level is " + perc + "%");
        }
    }
}
