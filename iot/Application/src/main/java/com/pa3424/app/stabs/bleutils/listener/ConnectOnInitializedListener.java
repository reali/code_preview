package com.pa3424.app.stabs1.bleutils.listener;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceState;
import com.idevicesinc.sweetblue.DeviceStateListener;
import com.pa3424.app.stabs1.activities.IDeviceManagerEvents;
import com.pa3424.app.stabs1.bleutils.GlobalBeanListener;
import com.pa3424.app.stabs1.bleutils.MyBottleManager;
import com.pa3424.app.stabs1.utils.Log;

import java.util.Arrays;

/**
 * Created by ali on 9/20/19.
 */

public class ConnectOnInitializedListener implements BleDevice.StateListener {

    private IDeviceManagerEvents events;
    private GlobalBeanListener globalBeanListener;

    public ConnectOnInitializedListener(IDeviceManagerEvents events, GlobalBeanListener globalBeanListener) {
        this.events = events;
        this.globalBeanListener = globalBeanListener;
    }

    @Override
    public void onEvent(final StateEvent event) {

        if (event.didEnter(BleDeviceState.INITIALIZED)) {

            MyBottleManager.mInstance.myDevice = event.device();
            MyBottleManager.mInstance.sendDiscoveryBroadcast();

            events.OnConnected();
            globalBeanListener.onConnected();


            Log.e("reali", "insertWaterIncome " + event.device().getMacAddress() + " " +
                    event.device().getName_debug() + " " +
                    event.device().getName_native() + " " +
            Arrays.toString(event.device().getAdvertisedServices()));

            MyBottleManager.mInstance.myDevice.setListener_State(new DeviceStateListener() {
                @Override
                public void onEvent(StateEvent e) {
//                    Log.e("reali", "state " + e);
                    if (e.didEnter(BleDeviceState.DISCONNECTED)) {
                        Log.e("reali", "DISCONNECTED !!!! ");
                    }

                    if (e.didEnter(BleDeviceState.CONNECTED)) {
                        Log.e("reali", "CONNECTED !!!! ");
                    }

                    if (e.didExit(BleDeviceState.CONNECTED)) {
                        Log.e("reali", "NOT CONNECTED ANY MORE !!!! ");

                        MyBottleManager.mInstance.disconnected();
                        MyBottleManager.mInstance.fix();
                    }

                    if (e.didEnter(BleDeviceState.INITIALIZED)) {
                        Log.e("reali", "INITIALIZED !!!! ");
                    }
                }
            });

            Log.e("reali", event.device().getName_debug() + " just initialized!");

            MyBottleManager.mInstance.getBatteryLevel();
            MyBottleManager.mInstance.addSerialListener();

        }

    }

}
