package com.pa3424.app.stabs1.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.pa3424.app.stabs1.activities.Setup2Activity;
import com.pa3424.app.stabs1.api.SyncFitBitWaterLevel;

import com.google.android.gms.maps.model.LatLng;
import com.pa3424.app.stabs1.activities.ChangeWaterGoalsActivity;
import com.pa3424.app.stabs1.activities.LoginActivity;
import com.pa3424.app.stabs1.activities.NewMainActivity;
import com.pa3424.app.stabs1.bleutils.MyBottleManager;
import com.pa3424.app.stabs1.data.day;
import com.pa3424.app.stabs1.data.drink;
import com.pa3424.app.stabs1.data.goal;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ali on 2/2/19.
 */
public class AppUtils {

    public static boolean isCustomGoalOff = true;
    public static int waterLevelMl = 1800;
    public static int weightKg = 75;
    public static int age = 30;
    public static int waterUn = 0;
    public static int weightUn = 0;
    public static int sex = 0;
    public static int actLvl = 1;

    public static int bottleBatteryLevel = -1;

    public static int getDayWaterLevel(Context ctx, Date date) {

        int index = getDayIndex(date);
        int sum = 0;

        if (index != -1) {
            sum = NewMainActivity.dayList.get(index).totalOunces;

//            for (drink mDrink : NewMainActivity.dayList.get(index).list) {
//                sum += mDrink.getOunces();
//            }
        }

        String units = getWaterUnit();
        if (units.equals("ml")) {
            return ozToMl(sum);
        }

        return sum;

    }

    public static int getDayIndex(Date date) {

        for (int i = 0; i<NewMainActivity.dayList.size(); i++) {

            if (DateUtils.isSameDay(date, NewMainActivity.dayList.get(i).getDate())) {
                return i;
            }
        }

        return -1;
    }

//    public static int getGoalWaterLevel(Context ctx) {
//
//        String units = getWaterUnit(ctx);
//        int waterOz = PreferenceManager.getDefaultSharedPreferences(ctx).getInt("water_goal", 60);
//
//        if (units.equals("ml")) {
//            return ozToMl(waterOz);
//        }
//
//        return waterOz;
//
//    }

    public static int getGoalWaterLevel(Context ctx, Date date) {

//        Log.e("reali", "getGoalWaterLevel " + date.toString());

        int index = getDayIndex(date);

        if (index == -1) {
            return findPreviousWaterGoal(ctx, date);
        }

        int waterGoalMl = NewMainActivity.dayList.get(index).water_goal_ml;

        if (waterGoalMl == 0) {
            waterGoalMl = ozToMl(60);
        }

        String units = getWaterUnit();
        if (units.equals("oz")) {
            return mlToOz(waterGoalMl);
        }

        return waterGoalMl;


//        if (units.equals("oz")) {
//            int waterGoalOz = mlToOz(waterGoalMl);
//
//            if (waterGoalOz > 0) {
//                return waterGoalOz;
//            } else {
//                return 60;
//            }
//        }
//
//        if (waterGoalMl > 0) {
//            return waterGoalMl;
//        } else {
//            return ozToMl(60);
//        }

    }

    private static int findPreviousWaterGoalMl(Context ctx, Date date) {

        for (int i = NewMainActivity.dayList.size()-1; i>=0; i--) {

            if (DateUtils.isDateLessThan(NewMainActivity.dayList.get(i).getDate(), date)) {

                return NewMainActivity.dayList.get(i).water_goal_ml;
            }
        }

        return ozToMl(60);
    }

    private static int findPreviousWaterGoal(Context ctx, Date date) {

        String units = getWaterUnit();
        if (units.equals("oz")) {
            return mlToOz(findPreviousWaterGoalMl(ctx, date));
        }

        return findPreviousWaterGoalMl(ctx, date);
    }

    private static List<goal> goalList = new ArrayList<>();

    public static void calculateWaterGoalForEachDay(Context ctx, List<goal> goalListLocal) {

        goalList.clear();
        goalList.addAll(goalListLocal);

        for (int i = NewMainActivity.dayList.size()-1; i>=0; i--) {
            NewMainActivity.dayList.get(i).water_goal_ml = getDayGoalMl_quick(ctx, NewMainActivity.dayList.get(i).getDate());
        }

        Date now = new Date();

        int i = getDayIndex(now);

        if (i == -1) {
            int todayGoalMl = getDayGoalMl(ctx, now);

            setDayGoal(todayGoalMl, now, ctx);
        }

    }

    private static int getDayGoalMl_quick(Context ctx, Date date) { //only to use in calculateWaterGoalForEachDay()

//        Log.e("reali", "getDayGoal " + date.toString());

        for (int i=goalList.size()-1; i>=0; i--) {

//            Log.e("reali", " - " + goalList.get(i).getDate().toString());

            if (DateUtils.isSameDay(goalList.get(i).getDate(), date) || DateUtils.isDateLessThan(goalList.get(i).getDate(), date)) {
//                Log.e("reali", "OK !!!!!!!!!!!!!!!! " + goalList.get(i).getWater_goal());

                return goalList.get(i).getWater_goal();
            } else {
                goalList.remove(i); //this optimization will work only if goals_list and drink_list are sorted by created and in for loop we going back (AS DONE NOW)
            }

        }

        return ozToMl(60);
    }

    private static int getDayGoalMl(Context ctx, Date date) {

//        Log.e("reali", "getDayGoal " + date.toString());

        for (int i=NewMainActivity.goalList.size()-1; i>=0; i--) {

//            Log.e("reali", " - " + NewMainActivity.goalList.get(i).getDate().toString());

            if (DateUtils.isSameDay(NewMainActivity.goalList.get(i).getDate(), date) || DateUtils.isDateLessThan(NewMainActivity.goalList.get(i).getDate(), date)) {
//                Log.e("reali", "OK !!!!!!!!!!!!!!!! " + NewMainActivity.goalList.get(i).getWater_goal());

                return NewMainActivity.goalList.get(i).getWater_goal();
            } else {
                NewMainActivity.goalList.remove(i); //this optimization will work only if goals_list and drink_list are sorted by created and in for loop we going back (AS DONE NOW)
            }

        }

        return ozToMl(60);
    }

    public static void addToLocalFromServer(int level, Date mDate, boolean isManual, String objId) {
        if (level != 0) {

            int i = getDayIndex(mDate);

            if (i == -1) {
                NewMainActivity.dayList.add(new day(mDate));

                i = getDayIndex(mDate);
            }

            NewMainActivity.dayList.get(i).list.add(new drink(level, isManual, mDate, objId, null));
            NewMainActivity.dayList.get(i).totalOunces += level;

        }
    }

    public static void addToLocal(Context ctx, int level, Date mDate, boolean isManual, String objId, String tempId) { //this one for calculating water goal if needed
        if (level != 0) {

            int i = getDayIndex(mDate);

            if (i == -1) {
                NewMainActivity.dayList.add(new day(mDate));

                i = getDayIndex(mDate);

                NewMainActivity.dayList.get(i).water_goal_ml = findPreviousWaterGoalMl(ctx, mDate);
            }

            int curUnLevel = level;

            String units = getWaterUnit();
            if (units.equals("ml")) {
                curUnLevel = ozToMl(level);
            }

            drink mDrink = new drink(curUnLevel, isManual, mDate, objId, tempId);

            NewMainActivity.dayList.get(i).list.add(mDrink);
            NewMainActivity.dayList.get(i).totalOunces += level;

//            return NewMainActivity.dayList.get(i).list.indexOf(mDrink);

        }

//        return -1;
    }

    public static void removeFromLocal(Date mDate, String objId) {

        int i = getDayIndex(mDate);

        if (i != -1) {

            for (int j=0; j<NewMainActivity.dayList.get(i).list.size(); j++) {
                drink d = NewMainActivity.dayList.get(i).list.get(j);
                if (d.getObjId() != null && d.getObjId().equals(objId)) {
                    NewMainActivity.dayList.get(i).totalOunces -= d.getOunces();
                    NewMainActivity.dayList.get(i).list.remove(j);
                    return;
                }
            }

        }

    }

    public static void removeTempFromLocal(Date mDate, String tempId) {

        int i = getDayIndex(mDate);

        if (i != -1) {

            for (int j=0; j<NewMainActivity.dayList.get(i).list.size(); j++) {
                drink d = NewMainActivity.dayList.get(i).list.get(j);
                if (d.getTempId() != null && d.getTempId().equals(tempId)) {
                    NewMainActivity.dayList.get(i).totalOunces -= d.getOunces();
                    NewMainActivity.dayList.get(i).list.remove(j);
                    return;
                }
            }

        }

    }

    public static String getWaterUnit() {
        return ChangeWaterGoalsActivity.drink_unit_arr[waterUn];
    }

    public static long getLastFitbitSyncDate(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getLong("last_fitbit_sync", 0l);
    }

    public static String getFitbitToken(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString("tok", ""); // TODO: 2/27/19 should store it on server
    }

    public static void setFitbitToken(String token, Context ctx) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("tok", token).commit();
    }

    public static void setFitbitUser(String user, Context ctx) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("user", user).commit();
    }

    public static boolean isSHealthLogged(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("s_health_logged", false);
    }

    public static boolean isGFitLogged(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("g_fit_logged", false);
    }

    public static void setSHealthLogged(boolean isLogged, Context ctx) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putBoolean("s_health_logged", isLogged).commit();
    }

    public static void setGFitLogged(boolean isLogged, Context ctx) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putBoolean("g_fit_logged", isLogged).commit();
    }

    public static String getFitbitUser(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString("user", "");
    }

    public static void setLastFitbitSyncDate(Context ctx) {
        Date date = new Date();
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putLong("last_fitbit_sync", date.getTime()).commit();
    }

    public static void setLastBottleLocation(Context ctx, double latitude, double longitude) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putFloat("last_latitude", (float)latitude).putFloat("last_longitude", (float)longitude).commit();
    }

    public static LatLng getLastBottleLocation(Context ctx) {

        double latitude = PreferenceManager.getDefaultSharedPreferences(ctx).getFloat("last_latitude", 0f);
        double longitude = PreferenceManager.getDefaultSharedPreferences(ctx).getFloat("last_longitude", 0f);

        if (latitude == 0f || longitude == 0f) {
            return null;
        }

        return new LatLng(latitude, longitude);
    }

    public static int ozToMl(double oz) {
        return Math.round((float)(oz * 29.57353));
    }

    public static float ozToL(double oz) {
        return (float)(oz * 0.02957353);
    }

    public static int mlToOz(double ml) {
        return Math.round((float)(ml / 29.57353));
    }

    public static int lbsToKg(double lbs) {
        return Math.round((float)(lbs * 0.45359237));
    }

    public static int kgToLbs(double kg) {
        return Math.round((float)(kg / 0.45359237));
    }

    public static void setDayGoal(int total_goal, Date date, Context ctx) {

        int i = getDayIndex(date);

        if (i == -1) {
            NewMainActivity.dayList.add(new day(date));

            i = getDayIndex(date);
        }

        NewMainActivity.dayList.get(i).water_goal_ml = total_goal;
    }

    public static void logout(Activity act) {
        ParseUser.logOut();
        PreferenceManager.getDefaultSharedPreferences(act).edit().clear().commit();

        act.startActivity(new Intent(act, LoginActivity.class));
        act.finish();
    }

    public static void syncTrackers(int level, Context ctx) {

        if (AppUtils.isInternetAvailable(ctx)) {
            // TODO: 10/20/20 try sync if not synced, save and sync later
        }

        new SyncFitBitWaterLevel(level, ctx).execute();

        if (AppUtils.isSHealthLogged(ctx)) {
            SHealthUtils.insertWaterIncome(AppUtils.ozToMl(level), ctx);
        }

        if (AppUtils.isGFitLogged(ctx)) {
            GoogleFitUtils.insertWaterIncome(AppUtils.ozToL(level), ctx);
        }

        //sync myfitnes
    }


    public static boolean isInternetAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void changeBottle(Activity concreteActivity) {

        SharedPreferences sharedPreferences = concreteActivity.getSharedPreferences("test", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(MyBottleManager.MAC_ADDR_STR).commit();

        MyBottleManager.mInstance.fix();

        Intent intent = new Intent(concreteActivity, Setup2Activity.class);
        concreteActivity.startActivity(intent);
        concreteActivity.finish();
    }

    public static void fix_ble_connection(Activity activity) {

        MyBottleManager.mInstance.fix();

        Intent intent = new Intent(activity, Setup2Activity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
