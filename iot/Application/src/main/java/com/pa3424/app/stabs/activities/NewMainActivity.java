package com.pa3424.app.stabs1.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;


import com.pa3424.app.stabs1.bleutils.MyBottleManager;
import com.pa3424.app.stabs1.fragments.DayGraphNewFragment;
import com.pa3424.app.stabs1.fragments.LiveBottleFragment;
import com.pa3424.app.stabs1.fragments.MonthGraphFragment;
import com.pa3424.app.stabs1.fragments.SettingsFragment;
import com.pa3424.app.stabs1.fragments.WeekGraphFragment;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pa3424.app.stabs1.R;
import com.pa3424.app.stabs1.adapter.DayFragmentPagerAdapter;
import com.pa3424.app.stabs1.data.day;
import com.pa3424.app.stabs1.data.goal;
import com.pa3424.app.stabs1.parse.BottleEvents;
import com.pa3424.app.stabs1.parse.Daily;
import com.pa3424.app.stabs1.parse.Goals;
import com.pa3424.app.stabs1.utils.AppUtils;
import com.pa3424.app.stabs1.utils.Log;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewMainActivity extends AppCompatActivity implements IDeviceManagerEvents {

    private static final String LOG_TAG = "reali_graphs";

    public static boolean isShowedReminder = false;

    private ParseUser parseUser;

    private static final int daysCount = 7;
    private static final int monthsCount = 9;

    private static List<Fragment> myFragmentList;

    public static boolean updateWaterData = false;

    private static final int DAY_TAB = 1;
    private static final int HISTORY_TAB = 2;
    private static final int LIVE_TAB = 3;
    private static final int SETTINGS_TAB = 4;

    private static int cur_tab = 0;

    private static DayFragmentPagerAdapter myFragmentPagerAdapter;

    @BindView(R.id.tv_daily) TextView tv_daily;
//    @BindView(R.id.tv_history) TextView tv_history;
//    @BindView(R.id.tv_live) TextView tv_live;
//    @BindView(R.id.tv_settings) TextView tv_settings;

    @BindView(R.id.pager)  ViewPager viewPager;

    @BindView(R.id.tab_day) View tab_daily;
    @BindView(R.id.tab_history) View tab_history;
    @BindView(R.id.tab_live) View tab_live;
    @BindView(R.id.tab_settings) View tab_settings;

    private static View inactive_daily, inactive_history, inactive_live, inactive_settings, history_view;

//    @BindView(R.id.inactive_daily) View inactive_daily;
//    @BindView(R.id.inactive_history) View inactive_history;
//    @BindView(R.id.inactive_live) View inactive_live;
//    @BindView(R.id.inactive_settings) View inactive_settings;

//    @BindView(R.id.history_view) View history_view;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.iv_cross) ImageView iv_close_no_sonnection;
    @BindView(R.id.iv_fix) ImageView iv_fix_bluetooth;
    @BindView(R.id.no_connection) FrameLayout no_connection;

    @BindView(R.id.week_tab) FrameLayout week_tab;
    @BindView(R.id.week_subtab) FrameLayout week_subtab;
    @BindView(R.id.month_tab) FrameLayout month_tab;
    @BindView(R.id.month_subtab) FrameLayout month_subtab;

    @BindView(R.id.tv_week) TextView tv_week;
    @BindView(R.id.tv_month) TextView tv_month;

    private static boolean isWeekSelected = true;

    static String[] list_dates;

    private Calendar mCal;
    public static List<day> dayList = new ArrayList<>();
    public static List<goal> goalList = new ArrayList<>();
    private static FragmentManager fragMan;

    ParseQuery.CachePolicy mPolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphs_new);
        ButterKnife.bind(this);

        inactive_daily = findViewById(R.id.inactive_daily);
        inactive_history = findViewById(R.id.inactive_history);
        inactive_live = findViewById(R.id.inactive_live);
        inactive_settings = findViewById(R.id.inactive_settings);

        history_view = findViewById(R.id.history_view);

        if (!isShowedReminder) {
            final Handler h = new Handler();
            h.postDelayed(new Runnable()
            {

                @Override
                public void run() {
                    Intent intent = new Intent(NewMainActivity.this, AlertActivity.class);
                    startActivity(intent);
                }
            }, 1000);
        }

        mCal = new GregorianCalendar();
        fragMan = getSupportFragmentManager();

        iv_close_no_sonnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                no_connection.setVisibility(View.GONE);
            }
        });

        iv_fix_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.fix_ble_connection(NewMainActivity.this);
            }
        });

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("skip", false)) {
                no_connection.setVisibility(View.VISIBLE);
            }
        }

        myFragmentList = new ArrayList<>();

        myFragmentPagerAdapter = new DayFragmentPagerAdapter(getSupportFragmentManager(), myFragmentList);
        viewPager.setAdapter(myFragmentPagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                checkToday(position);
            }
        });

        week_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isWeekSelected) {
                    setWeekSelected();
                }
            }
        });

        month_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWeekSelected) {
                    setMonthSelected();
                }
            }
        });

        parseUser = ParseUser.getCurrentUser();

        if (parseUser != null) {
            getDataFromServer();
        } else {

            addFakeLocalData();

            openDayGraph(mCal);
            setTabsListeners();
        }

        MyBottleManager.mInstance.setActivityEvent(this);

    }

    private void addFakeLocalData() {
        Calendar cal = new GregorianCalendar();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -7); //go to week ago

        Calendar oneDayCal;

        int lvl = 0;
        boolean isManual = false;

        Random random = new Random();

        for (int i=0; i<7; i++) {

            oneDayCal = (Calendar) cal.clone();

            for (int j=0; j<10; j++) {

                if (random.nextBoolean()) {

                    lvl = random.nextInt(20);
                    isManual = false;

                    AppUtils.addToLocalFromServer(lvl, oneDayCal.getTime(), isManual, "");

                }

                oneDayCal.add(Calendar.HOUR_OF_DAY, 2);

            }

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        AppUtils.calculateWaterGoalForEachDay(NewMainActivity.this, goalList);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (updateWaterData) { //if water goal_view was changed

            int index = viewPager.getCurrentItem();

            if (cur_tab == DAY_TAB) {
                openDayGraph(mCal);
            } else if (cur_tab == HISTORY_TAB) {
                openHistoryTab(mCal);
            } else if (cur_tab == LIVE_TAB) {
                openLiveTab();
            } else if (cur_tab == SETTINGS_TAB) {
                openSettingsTab();
            }

            viewPager.setCurrentItem(index, false);

            updateWaterData = false;
        }
    }

    private Date weekAgo, toToday;
    private Calendar weekAgoCal;

    public void getDataFromServer() {

        Log.e(LOG_TAG, "getDataFromServer");
        progressBar.setVisibility(View.VISIBLE);

        Calendar cal = new GregorianCalendar();

        cal.set(Calendar.DAY_OF_MONTH, 1); //go to 1st day of same month
        cal.add(Calendar.MONTH, -9); //to get data 9 month before

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date from = cal.getTime();

        cal = new GregorianCalendar();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -7); //go to week ago

        weekAgo = cal.getTime();
        weekAgoCal = (Calendar) cal.clone();

        cal.add(Calendar.DAY_OF_MONTH, 8);

        toToday = cal.getTime();

//        Log.e("reali", "from " + from.toString() + " to " + weekAgo.toString());
//        Log.e("reali", "from " + weekAgo.toString() + " to " + toToday.toString());

        // getGoalsHistory() => getDailyDrinks() => getLastWeekDrinks() !!!!!!!!!!!!!!

        getGoalsHistory(from, toToday);

    }

    private void getLastWeekDrinks(final Date weekAgo, Date toToday) {

        ParseQuery<BottleEvents> query = ParseQuery.getQuery("BottleEvents");

        query.setLimit(1000);

        query.whereEqualTo("UserObjectId", parseUser); //!!!!!!!
        query.whereEqualTo("EventName", "BottleLevelUpdate");

        query.whereGreaterThanOrEqualTo("date", weekAgo);
        query.whereLessThan("date", toToday);
        query.orderByAscending("date");

//        query.fromLocalDatastore()
        boolean isNetwork = false;

        if (AppUtils.isInternetAvailable(this)) {
//            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            isNetwork = true;
            query.fromNetwork();
        } else {
//            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
            query.fromLocalDatastore();
        }

        final boolean finalIsNetwork = isNetwork;
        query.findInBackground(new FindCallback<BottleEvents>() {
            public void done(final List<BottleEvents> todayList, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    Log.e("lastweek", "Error " + e.getMessage());

                    progressBar.setVisibility(View.GONE);
                    YesNoAlertActivity.showOkAlert(NewMainActivity.this, "Sync Error, try again.");

                } else {
                    Log.e("lastweek", "done " + todayList.size());

                    if (finalIsNetwork) {
                        ParseObject.unpinAllInBackground("drinks", new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                ParseObject.pinAllInBackground("drinks", todayList);
                            }
                        });
                    }

                    if (todayList.size() == 1000) {
                        Log.e("lastweek", "reached 1k limit");
                        // TODO: 1/13/19  reached limit so we should get more info
                    }

                    if (todayList.isEmpty()) {

                        Calendar oneDayCal;

                        int lvl = 0;
                        boolean isManual = false;

                        Random random = new Random();

                        for (int i=0; i<7; i++) {

                            oneDayCal = (Calendar) weekAgoCal.clone();

                            for (int j=0; j<10; j++) {

                                if (random.nextBoolean()) {

                                    lvl = random.nextInt(20);
                                    isManual = false;

                                    BottleEvents events = new BottleEvents();
                                    events.setWaterLevel(Integer.toString(lvl));
                                    events.setEventName("BottleLevelUpdate");
                                    events.setUserObjectId(parseUser);
                                    events.setIsManual(isManual);
                                    events.setDate(oneDayCal.getTime());

//                                    final int finalLvl = lvl;
//                                    final Context ctx = NewMainActivity.this;

                                    events.saveEventually();

                                    AppUtils.addToLocalFromServer(lvl, oneDayCal.getTime(), isManual, "");

                                }

                                oneDayCal.add(Calendar.HOUR_OF_DAY, 2);

                            }

                            weekAgoCal.add(Calendar.DAY_OF_MONTH, 1);
                        }
                    }

                    for (BottleEvents events : todayList) {

                        int lvl = Integer.parseInt(events.getWaterLevel());

                        AppUtils.addToLocalFromServer(lvl, events.getDate(), events.isManual(), events.getObjectId());

//                        Log.e(LOG_TAG, "waterLevel " + events.getWaterLevel() + " " + events.getCreatedAt().toString());
                    }

                    //calculate water goal_view for each day
                    AppUtils.calculateWaterGoalForEachDay(NewMainActivity.this, goalList);

//                    for (int i=0; i<dayList.size(); i++) {
//                        Log.e(LOG_TAG, dayList.get(i).getDate().toString() + " _ " + dayList.get(i).totalOunces);
//
//                        for (int j=0; j<dayList.get(i).list.size(); j++) {
//                            Log.e(LOG_TAG, "     ___ " + dayList.get(i).list.get(j).getDate() + " _ " + dayList.get(i).list.get(j).isManual() + " _ " + dayList.get(i).list.get(j).getOunces());
//                        }
//                    }

                    openDayGraph(mCal);
                    progressBar.setVisibility(View.GONE);

                    setTabsListeners();
                }
            }
        });

    }

    int countObjectsToSave = 0;
    int countObjectsSaved = 0;

    private void checkPinnedDrinks(final Date weekAgo, final Date toToday) {

        if (!AppUtils.isInternetAvailable(this)) {
            getLastWeekDrinks(weekAgo, toToday);
            return;
        }

        countObjectsToSave = 0;
        countObjectsSaved = 0;

//        ParseObject.unpinAllInBackground("manual");

        ParseQuery<BottleEvents> query = ParseQuery.getQuery("BottleEvents");
        query.fromPin("later");
        query.findInBackground(new FindCallback<BottleEvents>() {
            @Override
            public void done(List<BottleEvents> objects, ParseException e) {

                Log.e("reali", "!!!!!!!!!! later from PIN " + objects.size());

                countObjectsToSave = objects.size();

                if (countObjectsToSave == 0) {
                    getLastWeekDrinks(weekAgo, toToday);
                }

                for (final BottleEvents file : objects) {
                    file.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

//                            AppUtils.syncTrackers(file, NewMainActivity.this); // TODO: 10/21/20 sync trackers

                            file.unpinInBackground("later");
                            countObjectsSaved += 1;

                            if (countObjectsSaved == countObjectsToSave) {
                                getLastWeekDrinks(weekAgo, toToday);
                            }
                        }
                    });
                }


            }
        });


    }

    private void getDailyDrinks(Date from, Date to) {
        ParseQuery<Daily> query1 = ParseQuery.getQuery("Daily");

        query1.setLimit(1000);

        query1.whereEqualTo("UserObjectId", parseUser.getObjectId());

        query1.whereGreaterThanOrEqualTo("createdAt", from);
        query1.whereLessThan("createdAt", to);
        query1.orderByAscending("createdAt");

        boolean isNetwork = false;

        if (AppUtils.isInternetAvailable(this)) {
//            query1.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            isNetwork = true;
            query1.fromNetwork();
        } else {
//            query1.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
            query1.fromLocalDatastore();
        }

        final boolean finalIsNetwork = isNetwork;
        query1.findInBackground(new FindCallback<Daily>() {
            public void done(final List<Daily> dailyyList, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    Log.e("daily", "Error " + e.getMessage());

                    progressBar.setVisibility(View.GONE);
                    YesNoAlertActivity.showOkAlert(NewMainActivity.this, "Sync Error, try again.");

                } else {

                    if (finalIsNetwork) {
                        ParseObject.unpinAllInBackground("daily", new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                ParseObject.pinAllInBackground("daily", dailyyList);
                            }
                        });
                    }

                    checkPinnedDrinks(weekAgo, toToday);
//                    getLastWeekDrinks(weekAgo, toToday);

                    Log.e("daily", "done " + dailyyList.size());

                    dayList.clear();
                    for (Daily day : dailyyList) {

                        AppUtils.addToLocalFromServer(day.getWaterLevel(), day.getCreatedAt(), false, "");

//                        Log.e(LOG_TAG, "day " + day.getWaterLevel() + " " + day.getCreatedAt().toString());
                    }

                }
            }});
    }

    private void getGoalsHistory(final Date from, Date to) {

        Log.e(LOG_TAG, "getGoalsHistory");

        ParseQuery<Goals> query = ParseQuery.getQuery("Goals");

        query.setLimit(1000);

        query.whereEqualTo("userObject", parseUser);

        query.whereGreaterThanOrEqualTo("createdAt", from);
        query.whereLessThan("createdAt", to);
        query.orderByAscending("createdAt");

        boolean isNetwork = false;

        if (AppUtils.isInternetAvailable(this)) {
//            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            isNetwork = true;
            query.fromNetwork();
        } else {
//            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
            query.fromLocalDatastore();
        }

        final boolean finalIsNetwork = isNetwork;
        query.findInBackground(new FindCallback<Goals>() {
            public void done(final List<Goals> list, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    Log.e("goals", "Error " + e.getMessage());

                    progressBar.setVisibility(View.GONE);
                    YesNoAlertActivity.showOkAlert(NewMainActivity.this, "Sync Error, try again.");

                } else {

                    if (finalIsNetwork) {
                        ParseObject.unpinAllInBackground("goals", new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                ParseObject.pinAllInBackground("goals", list);
                            }
                        });
                    }

                    getDailyDrinks(from, weekAgo);

                    Log.e("goals", "done " + list.size());

                    if (list.size() == 1000) {
                        // TODO: 2/19/19  reached limit so we should get more goals
                    }

                    goalList.clear();

                    for (Goals goals : list) {
                        goalList.add(new goal(goals.getCreatedAt(), goals.getWaterLevel()));
//                        Log.e(LOG_TAG, "goal_view " + goals.getWaterLevel() + " " + goals.getActLvl());
                    }

                    if (!list.isEmpty()) {
                        Goals last = list.get(list.size()-1);

                        AppUtils.isCustomGoalOff = last.isCustomGoalOff();
                        AppUtils.waterLevelMl = last.getWaterLevel();
                        AppUtils.waterUn = last.getWaterUn();
                        AppUtils.weightKg = last.getWeight();
                        AppUtils.weightUn = last.getWeightUn();
                        AppUtils.sex = last.getSex();
                        AppUtils.actLvl = last.getActLvl();
                        AppUtils.age = last.getAge();
                    }

                }
            }
        });

    }

    private void setTabsListeners() {

        tab_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cur_tab != DAY_TAB) {
                    openDayGraph(mCal);
                } else {
                    viewPager.setCurrentItem(daysCount - 1, true);
                }
            }
        });

        tab_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cur_tab != HISTORY_TAB) {
                    openHistoryTab(mCal);
                }
            }
        });

        tab_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cur_tab != LIVE_TAB) {
                    openLiveTab();
                }
            }
        });

        tab_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cur_tab != SETTINGS_TAB) {
                    openSettingsTab();
                }
            }
        });

//
//        tab_monthly.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (cur_tab != MONTH_TAB) {
//                    openMonthGraph(mCal);
//                } else {
//                    viewPager.setCurrentItem(monthsCount - 1, true);
//                    horizontalPicker.setSelectedItem(monthsCount - 1);
//                }
//            }
//        });
    }

    private void checkToday(int position) {

        if (cur_tab == DAY_TAB) {

            if (position == daysCount-1) {
                setDayTabTextDefault();
            } else {
                tv_daily.setText("TODAY");
                tv_daily.setTextColor(Color.GREEN);
            }

        }

    }

    private void setWeekSelected() {

        week_tab.setBackgroundResource(R.drawable.shape_white);
        week_subtab.setVisibility(View.VISIBLE);
        tv_week.setTextColor(Color.parseColor("#2aa8e8"));

        month_tab.setBackgroundResource(android.R.color.transparent);
        month_subtab.setVisibility(View.GONE);
        tv_month.setTextColor(Color.WHITE);

        openWeekGraph(mCal);

        isWeekSelected = true;

    }

    private void setMonthSelected() {

        month_tab.setBackgroundResource(R.drawable.shape_white);
        month_subtab.setVisibility(View.VISIBLE);
        tv_month.setTextColor(Color.parseColor("#2aa8e8"));

        week_tab.setBackgroundResource(android.R.color.transparent);
        week_subtab.setVisibility(View.GONE);
        tv_week.setTextColor(Color.WHITE);

        openMonthGraph(mCal);

        isWeekSelected = false;

    }

    private void setDayTabTextDefault() {
        tv_daily.setText("DAY");
        tv_daily.setTextColor(Color.WHITE);
    }

//    private void setDayTabTextInactive() {
//        tv_daily.setText("DAY");
//        tv_daily.setTextColor(Color.parseColor("#8DACD0"));
//    }

    private void openDayGraph(Calendar mCal) {

        cur_tab = DAY_TAB;

        inactive_daily.setVisibility(View.GONE);
        inactive_history.setVisibility(View.VISIBLE);
        inactive_live.setVisibility(View.VISIBLE);
        inactive_settings.setVisibility(View.VISIBLE);

        history_view.setVisibility(View.GONE);

        if (myFragmentList.size()>0) {
            clearFragments(myFragmentList);
            myFragmentList.clear();
            myFragmentPagerAdapter.notifyDataSetChanged();
        }

        list_dates = new String[daysCount];

        Calendar mc = (Calendar) mCal.clone();
        mc.add(Calendar.DAY_OF_MONTH, -daysCount);

        for (int i=0; i<daysCount; i++) {
            mc.add(Calendar.DAY_OF_MONTH, 1);

            list_dates[i] = mc.get(Calendar.DAY_OF_MONTH) + "";

            if (i == daysCount-1) {
                myFragmentList.add(new DayGraphNewFragment(mc, true)); //day graph with live view
            } else {
                myFragmentList.add(new DayGraphNewFragment(mc));
            }

        }

        myFragmentPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(daysCount-1, false);

    }

    private void openHistoryTab(Calendar mCal) {

        cur_tab = HISTORY_TAB;

        inactive_daily.setVisibility(View.VISIBLE);
        inactive_history.setVisibility(View.GONE);
        inactive_live.setVisibility(View.VISIBLE);
        inactive_settings.setVisibility(View.VISIBLE);

        tv_daily.setText("DAY");
        tv_daily.setTextColor(Color.WHITE);

        ////

        history_view.setVisibility(View.VISIBLE);


        if (isWeekSelected) {
            setWeekSelected();
        } else {
            setMonthSelected();
        }

    }

    private void openLiveTab() {

        cur_tab = LIVE_TAB;

        inactive_daily.setVisibility(View.VISIBLE);
        inactive_history.setVisibility(View.VISIBLE);
        inactive_live.setVisibility(View.GONE);
        inactive_settings.setVisibility(View.VISIBLE);

        history_view.setVisibility(View.GONE);

        if (myFragmentList.size()>0) {
            clearFragments(myFragmentList);
            myFragmentList.clear();
            myFragmentPagerAdapter.notifyDataSetChanged();
        }

        myFragmentList.add(new LiveBottleFragment());

        myFragmentPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0, false);

    }

    private void openSettingsTab() {

        cur_tab = SETTINGS_TAB;

        inactive_daily.setVisibility(View.VISIBLE);
        inactive_history.setVisibility(View.VISIBLE);
        inactive_live.setVisibility(View.VISIBLE);
        inactive_settings.setVisibility(View.GONE);

        history_view.setVisibility(View.GONE);

        if (myFragmentList.size()>0) {
            clearFragments(myFragmentList);
            myFragmentList.clear();
            myFragmentPagerAdapter.notifyDataSetChanged();
        }

        myFragmentList.add(new SettingsFragment());

        myFragmentPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0, false);

    }

    private void openWeekGraph(Calendar mCal) {

        if (myFragmentList.size()>0) {
            clearFragments(myFragmentList);
            myFragmentList.clear();
            myFragmentPagerAdapter.notifyDataSetChanged();
        }

        Calendar mc = (Calendar) mCal.clone();

        myFragmentList.add(new WeekGraphFragment(mc));

        myFragmentPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0, false);

    }

    private void openMonthGraph(Calendar mCal) {

        if (myFragmentList.size()>0) {
            clearFragments(myFragmentList);
            myFragmentList.clear();
            myFragmentPagerAdapter.notifyDataSetChanged();
        }

//        list_dates = new String[monthsCount];

        Calendar mc = (Calendar) mCal.clone();
        mc.add(Calendar.MONTH, -monthsCount);

        for (int i=0; i<monthsCount; i++) {
            mc.add(Calendar.MONTH, 1);
//            list_dates[i] = DateUtils.getMonth(mc);
            myFragmentList.add(new MonthGraphFragment(mc));
        }

        myFragmentPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(monthsCount-1, false);

    }

    public void openSingleDayGraph(Calendar mCal) {

        cur_tab = 0;

        tv_daily.setText("TODAY");
        tv_daily.setTextColor(Color.GREEN);

        inactive_daily.setVisibility(View.GONE);
        inactive_history.setVisibility(View.VISIBLE);
        inactive_live.setVisibility(View.VISIBLE);
        inactive_settings.setVisibility(View.VISIBLE);

        history_view.setVisibility(View.GONE);

        Log.e(LOG_TAG, "openSingleDayGraph");

        if (myFragmentList.size() > 0) {
            clearFragments(myFragmentList);
            myFragmentList.clear();
            myFragmentPagerAdapter.notifyDataSetChanged();
        }

        myFragmentList.add(new DayGraphNewFragment(mCal));
        myFragmentPagerAdapter.notifyDataSetChanged();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case SettingsFragment.WRITE_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Log.e("reali", "WRITE_EXTERNAL_STORAGE permission granted");
                    SettingsFragment.createFileAndShare(this);
                }
                return;
            }

        }
    }

    private static void clearFragments(List<Fragment> fragments) {
        if (fragments != null) {
            FragmentTransaction ft = fragMan.beginTransaction();
            for (Fragment f : fragments) {
                if (f != null)
                    ft.remove(f);
            }
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public void OnBluetoothTurnedOn() {

    }

    @Override
    public void OnBeginDiscovery() {
        no_connection.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnConnected() {
        no_connection.setVisibility(View.GONE);
    }

    @Override
    public void OnError(String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                no_connection.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void OnLowBattery() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                        NewMainActivity.this,
                        getString(R.string.low_battery),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
