package com.pa3424.app.stabs1.bleutils.listener;

import com.idevicesinc.sweetblue.BleDevice;
import com.pa3424.app.stabs1.bleutils.MyBottleManager;
import com.pa3424.app.stabs1.utils.Log;

/**
 * Created by ali on 9/20/19.
 */

public class SerialListener implements BleDevice.ReadWriteListener {

    @Override
    public void onEvent(ReadWriteEvent e) {

        if (e.type() == Type.ENABLING_NOTIFICATION) {
            Log.e("reali", "ENABLING_NOTIFICATION - serial - onEvent - " + e.toString());
        }

        if (e.type() == Type.NOTIFICATION) {
            try {

//                String str_rtre = new String(rawData, "UTF-8");
//                Log.e("reali", "arr " + rawData.length + " " + str_rtre);

                byte[] rawData = e.data();
                byte[] bytes_str;

                if (rawData.length == 12) {
                    bytes_str = new byte[]{rawData[5], rawData[6], rawData[7], rawData[8], rawData[9]};
                } else if (rawData.length == 11) {
                    bytes_str = new byte[]{rawData[5], rawData[6], rawData[7], rawData[8]};
                } else if (rawData.length == 10) {
                    bytes_str = new byte[]{rawData[5], rawData[6], rawData[7]};
                } else {
                    bytes_str = new byte[]{rawData[5], rawData[6], rawData[7]}; // !!!
                }

                String str = new String(bytes_str);
//                Log.e("reali", "!!!! " + str_str);

                String[] str_arr = str.split(":");

                if (str_arr.length > 1) {

                    Log.e("reali", "!!!! volume " + str_arr[0] + " consumed " + str_arr[1]);

                    int volume = Integer.parseInt(str_arr[0]);
                    int consumed = Integer.parseInt(str_arr[1]);

                    try {
                        MyBottleManager.mInstance.setWaterLevel(volume, consumed);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }

            } catch (Exception ex) {
            }

        }
    }
}
