package com.pa3424.app.stabs1.bleutils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.utils.BluetoothEnabler;
import com.idevicesinc.sweetblue.utils.Uuids;
import com.pa3424.app.stabs1.activities.IDeviceManagerEvents;
import com.pa3424.app.stabs1.bleutils.listener.BatteryLevelListener;
import com.pa3424.app.stabs1.bleutils.listener.ConnectOnInitializedListener;
import com.pa3424.app.stabs1.bleutils.listener.DiscoveryListener;
import com.pa3424.app.stabs1.bleutils.listener.SerialListener;
import com.pa3424.app.stabs1.receiver.BatteryLevelReceiver;
import com.pa3424.app.stabs1.receiver.DiscoveryReceiver;
import com.pa3424.app.stabs1.utils.AppUtils;
import com.pa3424.app.stabs1.utils.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ali on 9/20/19.
 */

public class MyBottleManager {

    public BleDevice myDevice;

    private BleManager bleManager;
    private DiscoveryListener discoveryListener;
    private GlobalBeanListener globalBeanListener;
    public int batteryLevel;
    public int waterLevel;
    private Context context;
    private IDeviceManagerEvents events;

    public static boolean started = false;

    public static final String SerialUuid = "a495ff11-c5b1-4b44-b512-1370f02d74de";
    public final static String MAC_ADDR_STR = "mac_addr";

    public static final MyBottleManager mInstance = new MyBottleManager();

    public void init(Context context, IDeviceManagerEvents events) {
        this.context = context;
        this.events = events;
        start();
    }

    private void start() {

        started = true;

        events.OnBluetoothTurnedOn();

        BluetoothEnabler.start(context);

        bleManager = BleManager.get(context);

        DiscoveryListener.allDevices = new ArrayList<>();

        globalBeanListener = new GlobalBeanListener(context);
        discoveryListener = new DiscoveryListener(context);
        bleManager.startScan(discoveryListener);

        events.OnBeginDiscovery();
    }

    public void getBatteryLevel() {
        BatteryLevelListener mBatteryLevelListener = new BatteryLevelListener();
        myDevice.readBatteryLevel(mBatteryLevelListener);
    }

    public void addSerialListener() {
        SerialListener mSerialListener = new SerialListener();

        if(!myDevice.isNotifyEnabled(Uuids.fromString(SerialUuid))) {
            myDevice.enableNotify(Uuids.fromString(SerialUuid), mSerialListener);
        }
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
        AppUtils.bottleBatteryLevel = batteryLevel;
        sendBottleBatteryLevelBroadcast(batteryLevel);
    }

    public void setWaterLevel(int waterLevel, int consumed) {
        this.waterLevel = waterLevel;
        globalBeanListener.onSerialMessageReceived(waterLevel, consumed);
    }

    private void sendBottleBatteryLevelBroadcast(int  percentage) {
        Intent i = new Intent(BatteryLevelReceiver.BATTERY_LEVEL);
        i.putExtra("perc", percentage);

        if (context != null) {
            context.sendBroadcast(i);
        } else {
            Log.e("reali_broadcast_battery", "context is null!!");
        }
    }

    public void connect(BleDevice device) {

        bleManager.stopScan();
        bleManager.unbondAll();
        bleManager.disconnectAll();

        SharedPreferences sharedPreferences = context.getSharedPreferences("test", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(MAC_ADDR_STR, device.getMacAddress().toString()).commit();

        ConnectOnInitializedListener mConnectInitListener = new ConnectOnInitializedListener(events, globalBeanListener);
        device.connect(mConnectInitListener);
    }

    public void setActivityEvent(IDeviceManagerEvents events) {
        this.events = events;
    }

    public void disconnected() {
        events.OnError("");
        globalBeanListener.onDisconnected();
    }

    public void fix() {

        if (!started)
            return;

        DiscoveryListener.allDevices.clear();
        sendDiscoveryBroadcast();

        bleManager.disconnectAll();

        myDevice = null;

        bleManager.undiscoverAll();

        if (!bleManager.isScanning()) {
            Log.e("reali", "was not Scanning");
            discoveryListener = new DiscoveryListener(context);
            bleManager.startScan(discoveryListener);
        } else {
            Log.e("reali", "was Scanning");
        }

    }

    public void pauseDrinkCalculation() {
        if (globalBeanListener != null) {
            globalBeanListener.pauseDrinkCalculation();
        }
    }
    public void resumeDrinkCalculation() {
        if (globalBeanListener != null) {
            globalBeanListener.resumeDrinkCalculation();
        }

    }

    public void sendDiscoveryBroadcast() {
        Intent i = new Intent(DiscoveryReceiver.DISCOVERY);
//        i.putExtra("data", data);
        if (context != null) {
            context.sendBroadcast(i);
        } else {
            Log.e("reali_broadcast_disc", "context is null!!");
        }
    }

}
