package com.pa3424.app.stabs1.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.pa3424.app.stabs1.R;
import com.pa3424.app.stabs1.adapter.WeekAdapter;
import com.pa3424.app.stabs1.data.DayObj;
import com.pa3424.app.stabs1.utils.AppUtils;
import com.pa3424.app.stabs1.utils.DateUtils;
import com.pa3424.app.stabs1.utils.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ali on 7/22/19.
 */
public class WeekGraphFragment extends Fragment {

    private static final String LOG_TAG = "reali_week_graphs";

    private Calendar cal;

    @BindView(R.id.tv_week_title) TextView tv_week_title;
    @BindView(R.id.tv_average_oz) TextView tv_average_oz;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private List<DayObj> weekList;
    private WeekAdapter weeksAdapter;

    public WeekGraphFragment() {
    }

    public WeekGraphFragment(Calendar cal) {
        this.cal = (Calendar) cal.clone();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.week_graph_fragment, container, false);
        ButterKnife.bind(this, view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        setData();

        WeekAdapter.OnItemClickListener listener = new WeekAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int pos) {

            }
        };

        DisplayMetrics dMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

        int progress_100 = dMetrics.widthPixels - dpToPx(110);

        weeksAdapter = new WeekAdapter(getContext(), weekList, progress_100, listener);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(weeksAdapter);

        return view;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void setData() {

        if (cal != null) {
            Calendar cal0 = (Calendar) cal.clone();

//            Log.e(LOG_TAG, "getWeek  " + cal0.getTime().toString());

            cal0.set(Calendar.HOUR_OF_DAY, 0);
            cal0.set(Calendar.MINUTE, 0);
            cal0.set(Calendar.SECOND, 0);
            cal0.set(Calendar.MILLISECOND, 0);

            Calendar lastDay_cal = (Calendar) cal0.clone();
            lastDay_cal.add(Calendar.DAY_OF_MONTH, -1);

            SimpleDateFormat fmtOut = new SimpleDateFormat("MM/d");
            String lastDay = fmtOut.format(lastDay_cal.getTime());
//            Log.e(LOG_TAG, "getWeek  " + cal0.getTime().toString());

            weekList = new ArrayList<>();

            for (int i = 0; i < 30; i++) {

                cal0.add(Calendar.DAY_OF_MONTH, -1);

//                Log.e(LOG_TAG, "getWeek day " + i + " " + cal0.getTime().toString());

                int dayOunces = AppUtils.getDayWaterLevel(getContext(), cal0.getTime());

                int goal = AppUtils.getGoalWaterLevel(getContext(), cal.getTime());
                int percent = dayOunces * 100 / goal;

                weekList.add(new DayObj(cal0.getTime(), percent));

            }

            int weekSum = 0;

            for (DayObj obj : weekList) {
                weekSum += obj.getPercent();
            }

            double average = 0;

            if (weekList.size() != 0) {
                average = weekSum / weekList.size();
            }

            tv_average_oz.setText("Daily average: " + Math.round(average) + "/" + AppUtils.getGoalWaterLevel(getActivity(), cal0.getTime()) + " " + AppUtils.getWaterUnit());

            String firstDay = fmtOut.format(cal0.getTime());

            tv_week_title.setText("Days of " + firstDay + " - " + lastDay);
        }
    }

//    public void onValueSelected() {
//
//        Calendar cal1 = (Calendar) cal.clone();
//
//        cal1.set(Calendar.HOUR_OF_DAY, 0);
//        cal1.set(Calendar.MINUTE, 0);
//        cal1.set(Calendar.SECOND, 0);
//        cal1.set(Calendar.MILLISECOND, 0);
//
//        int mOffset = cal1.get(Calendar.DAY_OF_WEEK) - 2 - dayOfWeek;
//
//        cal1.add(Calendar.DAY_OF_MONTH, -mOffset);
//
//        final Calendar newCal = (Calendar)cal1.clone();
//
//        Log.e(LOG_TAG, "onValueSelected");
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
//
//                if (getActivity() != null) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            NewMainActivity.openSingleDayGraph(newCal);
//                        }
//                    });
//                }
//            }
//        }).start();
//
//    }

}