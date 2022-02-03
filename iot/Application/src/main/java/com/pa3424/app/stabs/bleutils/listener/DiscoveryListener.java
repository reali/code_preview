package com.pa3424.app.stabs1.bleutils.listener;

import android.content.Context;
import android.content.SharedPreferences;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleManager;
import com.pa3424.app.stabs1.bleutils.MyBottleManager;
import com.pa3424.app.stabs1.utils.Log;

import java.util.List;

/**
 * Created by ali on 8/12/19.
 */

public class DiscoveryListener implements BleManager.DiscoveryListener {

    private Context mContext;
    public static List<BleDevice> allDevices;

    public DiscoveryListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onEvent(DiscoveryEvent event) {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("test", Context.MODE_PRIVATE);
        String mac = sharedPreferences.getString("mac_addr", "");

//        Log.e("reali", "mac " + mac);

        if (event.was(BleManager.DiscoveryListener.LifeCycle.DISCOVERED) ) {

            if (!mac.isEmpty()) {

                if (event.device().getMacAddress().equals(mac)) {

                    allDevices.add(event.device());
                    MyBottleManager.mInstance.sendDiscoveryBroadcast();

                    MyBottleManager.mInstance.connect(event.device());
                }

            } else {

                Log.e("reali", "-- " + event.device().getName_debug() + " " + event.device().printState());

//                if (event.device().getName_debug().startsWith("bean")) { //should be bttl
//                    allDevices.add(event.device());
//                    sendDiscoveryBroadcast();
//                }

                allDevices.add(event.device());
                MyBottleManager.mInstance.sendDiscoveryBroadcast();

                //temp!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                for (BleDevice de : allDevices) {
                    Log.e("reali", "fd " + de.getName_debug() + " " + de.printState());

                    if (de.getName_debug().toLowerCase().contains("bean") ||
                            de.getName_debug().toLowerCase().startsWith("bttl") ||
                            de.getName_debug().toLowerCase().startsWith("ag")) {
                        MyBottleManager.mInstance.connect(de);
                        break;
                    }

                }

                //temp!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                Log.e("reali", "list " + allDevices.size());
            }

//            if (!mac.isEmpty()) {
//
//                for (BleDevice de : mContext.bleManager.getDevices_List()) {
//                    if (de.getMacAddress().equals(mac)) {
//                        connect(de);
//                    }
//                }
//
//            } else {
//                allDevices.clear();
//
//                Log.e("reali", "____");
//
//                for (BleDevice de : mContext.bleManager.getDevices_List()) {
//                    Log.e("reali", " d " + de.getName_debug() + " " + de.printState());
//
//                    if (de.getName_debug().startsWith("bean")) { //should be bttl
//                        allDevices.add(de);
//                    }
//
//                }
//
////                Log.e("reali", "____");
////
////                for (BleDevice de : allDevices) {
////                    Log.e("reali", "fd " + de.getName_debug() + " " + de.printState());
////
////                    connect(de); //for now first one but should show to user list and select
////                    break;
////
////                }
////
////                Log.e("reali", "____");
//
//            }
        }

    }

}
