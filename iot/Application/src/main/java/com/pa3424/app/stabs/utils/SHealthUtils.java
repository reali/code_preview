package com.pa3424.app.stabs1.utils;

import android.content.Context;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthDataUnit;
import com.samsung.android.sdk.healthdata.HealthDeviceManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by ali on 10/31/19.
 */

public class SHealthUtils {

    private static HealthDataStore mStore;
    private static Context ctx;
    private static int waterLevel;

    public static void insertWaterIncome(int waterLevel1, Context ctx1) {

        waterLevel = waterLevel1;
        ctx = ctx1;

        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a HealthDataStore instance and set its listener
        mStore = new HealthDataStore(ctx, mConnectionListener);
        // Request the connection to the health data store
        mStore.connectService();

    }

    private static void insert() {
        HealthData baseData = new HealthData();
        baseData.putFloat(HealthConstants.WaterIntake.AMOUNT, waterLevel);
        baseData.putFloat(HealthConstants.WaterIntake.UNIT_AMOUNT, 250f);

        long start_time = new Date().getTime();
        int offset = TimeZone.getDefault().getOffset(start_time);

        Log.e("reali", "time " + new Date(start_time).toString() + " offset " + offset);

        baseData.putLong(HealthConstants.WaterIntake.START_TIME, start_time);
        baseData.putLong(HealthConstants.WaterIntake.TIME_OFFSET, offset);

        baseData.setSourceDevice(new HealthDeviceManager(mStore).getLocalDevice().getUuid());

        HealthDataResolver.InsertRequest i_request = new HealthDataResolver.InsertRequest.Builder()
                .setDataType(HealthConstants.WaterIntake.HEALTH_DATA_TYPE)
                .build();

        i_request.addHealthData(baseData);

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        resolver.insert(i_request).setResultListener(mInsertListener);

        Log.e("reali", "sent!");
    }

    private final static HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {

        @Override
        public void onConnected() {
            if (isPermissionAcquired()) {
//                readTodayWaterAmount();
                insert();
            } else {
                AppUtils.setSHealthLogged(false, ctx);
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d("reali", "Health data service is not available.");
        }

        @Override
        public void onDisconnected() {
            Log.d("reali", "Health data service is disconnected.");
        }
    };

    private static Set<HealthPermissionManager.PermissionKey> generatePermissionKeySet() {
        Set<HealthPermissionManager.PermissionKey> pmsKeySet = new HashSet<>();

        // Add the read and write permissions to Permission KeySet
        pmsKeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.WaterIntake.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        pmsKeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.WaterIntake.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.WRITE));
        return pmsKeySet;
    }

    private static boolean isPermissionAcquired() {

        HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);

        try {
            // Check whether the permissions that this application needs are acquired
            Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(generatePermissionKeySet());
            return !resultMap.values().contains(Boolean.FALSE);
        } catch (Exception e) {
            Log.e("reali", "Permission request fails.", e);
        }
        return false;
    }

    private static void readTodayWaterAmount() {

        Log.e("reali", "readTodayWaterAmount");

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        // Set time range from start time of today to the current time
        long startTime = getStartTimeOfToday();
        long endTime = startTime + ONE_DAY_IN_MILLIS;

        Log.e("reali", new Date(startTime).toString() + "-" + new Date(endTime).toString());

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.WaterIntake.HEALTH_DATA_TYPE)
                .setProperties(new String[] {HealthConstants.WaterIntake.AMOUNT})
                .setLocalTimeRange(HealthConstants.WaterIntake.START_TIME, HealthConstants.WaterIntake.TIME_OFFSET,
                        startTime, endTime)
                .build();

        try {
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e("reali", "Getting income water amount fails.", e);
        }

//        Log.e("reali", "readTodayWaterAmount done");
    }

    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

    private static HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> mListener = new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
        @Override
        public void onResult(HealthDataResolver.ReadResult healthDatas) {

            float count = 0f;
            Log.e("reali", "readTodayWaterAmount onResult");

            try {
                for (HealthData data : healthDatas) {
                    Log.e("reali", "_ " + data.getFloat(HealthConstants.WaterIntake.AMOUNT));
                    count += data.getFloat(HealthConstants.WaterIntake.AMOUNT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                healthDatas.close();
            }

            Log.e("reali", "s_health count - " + count + " - " + HealthDataUnit.MILLILITER.convertTo(count, HealthDataUnit.FLUID_OUNCE));
        }
    };

    private static HealthResultHolder.ResultListener mInsertListener = new HealthResultHolder.ResultListener<HealthResultHolder.BaseResult>() {
        @Override
        public void onResult(HealthResultHolder.BaseResult baseResult) {
            Log.e("reali", "mInsertListener onResult");

            if (baseResult.getStatus() == HealthResultHolder.BaseResult.STATUS_SUCCESSFUL) {
                Log.e("reali", "success");
                mStore.disconnectService();
            } else {
                Log.e("reali", "failed");
            }

        }
    };

    private static long getStartTimeOfToday() {
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

}
