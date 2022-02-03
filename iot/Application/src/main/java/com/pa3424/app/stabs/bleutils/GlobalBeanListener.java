package com.pa3424.app.stabs1.bleutils;

import android.content.Context;
import android.content.Intent;

import com.pa3424.app.stabs1.parse.BottleEvents;
import com.pa3424.app.stabs1.receiver.BatteryLevelReceiver;
import com.pa3424.app.stabs1.receiver.LiveWaterLevelReceiver;
import com.pa3424.app.stabs1.receiver.NewWaterLevelReceiver;
import com.pa3424.app.stabs1.utils.AppUtils;
import com.pa3424.app.stabs1.utils.LocationUtils;
import com.pa3424.app.stabs1.utils.Log;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;

import static java.lang.Math.abs;

public class GlobalBeanListener implements MyBlueListener {
    private Context context;
    private int currentOunces;
    private int absoluteWaterReadingChange = 0;
    private final static int THRESHOLD_WATERLEVEL_CHANGE = 2;
//    private volatile int lastRecordedLevel = 0;
    private volatile boolean pauseDrinkCalculation = false;
    private long tStart = 0;
    private int previousRecordLiveOunces = 0;

    GlobalBeanListener(Context context) {
        this.context = context;
        tStart = System.currentTimeMillis();
    }

    public void AppendToSerialMonitor(String s) {
        final String temp;
        if (context != null) {
            temp = "GlobalBeanListener:" + context.toString() + s;
        } else {
            temp = "GlobalBeanListener:" + "null context" + s;
        }
        //Toast.makeText(context, temp, Toast.LENGTH_SHORT);
        Log.d("bluetooth", "appendToSerialMonitor: " + temp);
    }

//    public int getLastKnownLevel() {
//        return this.lastRecordedLevel;
//    }

    @Override
    public void onConnected() {
        AppendToSerialMonitor("Connected To Bean");

        LocationUtils.startListen(context);
    }

    @Override
    public void onConnectionFailed() {
        System.out.println("Bean connection failed");
        AppendToSerialMonitor("Connection failed");
//        SweetDiscoveredDeviceManager.getInstance().onDeviceStatusError("Connection to the bottle failed");
//        SweetDiscoveredDeviceManager.getInstance().reset();

    }

    @Override
    public void onDisconnected() {
        System.out.println("Bean disconnected");
        AppendToSerialMonitor("Disconnected");

        LocationUtils.stopListen();
    }

    @Override
    public void onSerialMessageReceived(int currentOunces, int consumed) {

        this.currentOunces = currentOunces;
//        lastRecordedLevel = currentOunces;

        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        long timeInSeconds = tEnd / 1000;
        double elapsedSeconds = tDelta / 1000.0;
//        final boolean METHOD_TIME_DELAY_BASED_MEASUREMENT = true;

        //<TimeStamp>:<EventName>:<WaterLevel>:<BatteryLevel>:<AccelormeterXAxis>:<AccelormeterYAxis>:<AccelormeterZAxis>:<Temperature>
        String broadcastFormat = String.format("%d:1:%s:100:0:0:0:29", timeInSeconds, currentOunces+"");

        UpdateWaterConsume(consumed);
        UpdateLiveBottleLevel(elapsedSeconds, broadcastFormat);
    }

    private void UpdateWaterConsume(int consumed) {

        if (consumed >= 1) {
            sendToCloudAndFitbit(consumed);
        }
    }

    private void UpdateLiveBottleLevel(double elapsedSeconds, String broadcastFormat) {
        // calculate the difference between current reported ounces from the bottle
        // and previously stored value. Currently the capacitance values on the bottle
        // jump up and down causing mis-recordings. We will ignore ounce changes
        // that are below the threshold. we are not loosing anything here unless when bottle goes
        // from 1 oz to 0 oz. We will tolerate this discrepancy for now.

        // Eventually when the water level change crosses the THRESHOLD things will get
        // caught up.
        final double THRESHOLD_LIVE_VIEW_UPDATE = 5;

        absoluteWaterReadingChange = abs(currentOunces - previousRecordLiveOunces);
        // only send bottle updates if the change is greater than 1 ounce
        if (absoluteWaterReadingChange > THRESHOLD_WATERLEVEL_CHANGE || elapsedSeconds > THRESHOLD_LIVE_VIEW_UPDATE) {
            AppendToSerialMonitor(broadcastFormat);
            previousRecordLiveOunces = currentOunces;

            // for live water level activity we are looking at the level of
            // water in the bottle. We dont need any calculations here.
            // we just report the live level

            sendLiveBottleWaterLevelBroadcast(currentOunces);
        }
    }

    private void sendToCloudAndFitbit(final int waterLevel) {

        ParseUser user = ParseUser.getCurrentUser();

        if (user != null) {

            final BottleEvents events = new BottleEvents();
            events.setWaterLevel(Integer.toString(waterLevel));
            events.setEventName("BottleLevelUpdate");
            events.setUserObjectId(user);
            events.setIsManual(false);
            events.setDate(new Date());

            if (AppUtils.isInternetAvailable(context)) {
                events.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        events.pinInBackground();
                        AppUtils.syncTrackers(waterLevel, context);
                    }
                });
            } else {
                events.pinInBackground("later");
            }

        }

        AppUtils.addToLocal(context, waterLevel, new Date(), false, null, null);
        sendNewWaterDrankBroadcast(waterLevel);

    }

    @Override
    public void onError() {
//        SweetDiscoveredDeviceManager.getInstance().onDeviceStatusError("There was a problem talking to the Bottle");

//        SweetDiscoveredDeviceManager.getInstance().reset();
        AppendToSerialMonitor("Bean Error");

        LocationUtils.stopListen();
    }

    public void onReadRemoteRssi(int rssi) {

    }

    private void sendLiveBottleWaterLevelBroadcast(int data) {
        Intent i = new Intent(LiveWaterLevelReceiver.LIVE_WATER_LEVEL);
        i.putExtra("data", data);

        if (context != null) {
            context.sendBroadcast(i);
        } else {
            Log.e("reali_broadcast_live", "context is null!!");
        }
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


    private void sendNewWaterDrankBroadcast(int data) {
        Intent i = new Intent(NewWaterLevelReceiver.NEW_WATER_LEVEL);
        i.putExtra("data", data);
        if (context != null) {
            context.sendBroadcast(i);
        } else {
            Log.e("reali_broadcast_new", "context is null!!");
        }
    }

    public void pauseDrinkCalculation() {
        pauseDrinkCalculation = true;
    }

    public void resumeDrinkCalculation() {
        pauseDrinkCalculation = false;
    }

}
