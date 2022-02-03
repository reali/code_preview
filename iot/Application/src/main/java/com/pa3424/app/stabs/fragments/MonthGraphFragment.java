package com.pa3424.app.stabs1.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.pa3424.app.stabs1.pa3424GridItem;
import com.pa3424.app.stabs1.R;
import com.pa3424.app.stabs1.activities.NewMainActivity;
import com.pa3424.app.stabs1.adapter.MonthNewAdapter;
import com.pa3424.app.stabs1.utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ali on 7/22/19.
 */
public class MonthGraphFragment extends Fragment {

    private static final String LOG_TAG = "reali_graphs";

    private View mMonthView;
    private TextView text_month, text_empty;
    private GridView gridView;
    private int offset;
    private Calendar cal;

    private ArrayList<pa3424GridItem> arrayOfGlasses;

    public MonthGraphFragment() {
    }

    public MonthGraphFragment(Calendar cal) {
        this.cal = (Calendar) cal.clone();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        Bundle bundle = getArguments();
//            int position = bundle.getInt("position");

        View view = inflater.inflate(R.layout.month_graph_fragment, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mMonthView = view.findViewById(R.id.month_layout);
        gridView = (GridView) view.findViewById(R.id.gridView1);
        text_month = (TextView) view.findViewById(R.id.text_month);
        text_empty = (TextView) view.findViewById(R.id.tv_empty);

        instantiateGrid();

        return view;
    }

    private void instantiateGrid() {

        Calendar cal0 = (Calendar) cal.clone();

        cal0.set(Calendar.DAY_OF_MONTH, 1); //go to 1st day of same month

        cal0.set(Calendar.HOUR_OF_DAY, 0);
        cal0.set(Calendar.MINUTE, 0);
        cal0.set(Calendar.SECOND, 0);
        cal0.set(Calendar.MILLISECOND, 0);

//        SimpleDateFormat fmtOut = new SimpleDateFormat("MMMM yyyy");
        SimpleDateFormat fmtOut = new SimpleDateFormat("MMMM");
        final String month = fmtOut.format(cal0.getTime());
        int daysInMonth = cal0.getActualMaximum(Calendar.DAY_OF_MONTH);

        text_month.setText(month);

        // Construct the data source
        arrayOfGlasses = new ArrayList<>();

//        Log.e(LOG_TAG, "day of week - " + cal0.get(Calendar.DAY_OF_WEEK));
//        Log.e(LOG_TAG, "days in month - " + daysInMonth);

        offset = 0;

        for (int i = 2 ; i < cal0.get(Calendar.DAY_OF_WEEK); i++) {
            offset++;
            arrayOfGlasses.add(new pa3424GridItem(-1, -1));
        }

        for (int i=0; i < daysInMonth; i++) {

            if (i != 0)
                cal0.add(Calendar.DAY_OF_MONTH, 1);

            int j = AppUtils.getDayIndex(cal0.getTime());

            pa3424GridItem item;

            if (j != -1) {
                item = new pa3424GridItem(AppUtils.getDayWaterLevel(getContext(), cal0.getTime()), AppUtils.getGoalWaterLevel(getContext(), cal0.getTime()));
            } else {
                item = new pa3424GridItem();
            }

            Date today = new Date();

            if (cal0.getTime().after(today)) {
                item.setFuture(true);
            }

            arrayOfGlasses.add(item);

        }

//        Log.e(LOG_TAG, "objects pa3424GridItem " + arrayOfGlasses.size());

        gridView.setEmptyView(text_empty);

        // Create the adapter to convert the array to views
//        MonthOldAdapter adapter = new MonthOldAdapter(getActivity().getBaseContext(), arrayOfGlasses);
        MonthNewAdapter adapter = new MonthNewAdapter(getActivity().getBaseContext(), arrayOfGlasses);
        // Attach the adapter to a ListView
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if (arrayOfGlasses.get(position).getOunces() > 0) {

                    Calendar cal1 = (Calendar) cal.clone();
                    cal1.set(Calendar.DAY_OF_MONTH, position-offset+1);

//                    NewMainActivity.openSingleDayGraph(cal1);
                    ((NewMainActivity)(getActivity())).openSingleDayGraph(cal1);
                }
            }
        });

    }
}