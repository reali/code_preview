package com.pa3424.app.stabs1.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pa3424.app.stabs1.adapter.ManualAdapter;
import com.pa3424.app.stabs1.parse.BottleEvents;
import com.pa3424.app.stabs1.R;
import com.pa3424.app.stabs1.data.drink;
import com.pa3424.app.stabs1.utils.AppUtils;
import com.pa3424.app.stabs1.utils.DateUtils;
import com.pa3424.app.stabs1.utils.DialogUtils;
import com.pa3424.app.stabs1.utils.Log;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManualListActivity extends AppCompatActivity {

    private List<drink> list;
    private ManualAdapter adapter;

    @BindView(R.id.back) ImageView back;
    @BindView(R.id.tv_emptylist) TextView empty;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    @BindView(R.id.amount) View amount;
    @BindView(R.id.time) View time;

    @BindView(R.id.number_picker) NumberPicker number_picker;
    @BindView(R.id.number_picker_view) View number_picker_view;

    @BindView(R.id.time_picker) TimePicker time_picker;
    @BindView(R.id.time_picker_view) View time_picker_view;

    @BindView(R.id.tv_amount) TextView tv_amount;
    @BindView(R.id.tv_time) TextView tv_time;

    @BindView(R.id.add) View add;

    private Calendar mcurrentTime;

    private boolean number_picker_opened = false;
    private boolean time_picker_opened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_list);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#036aab"));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        list = new ArrayList<>();

        int index = AppUtils.getDayIndex(new Date());
        if (index != -1) {
            for (drink mDrink : NewMainActivity.dayList.get(index).list) {
//                Log.e("reali", "mDrink " + mDrink.getDate() + " " + mDrink.getOunces() + " " + mDrink.isManual());
                if (mDrink.isManual())
                    list.add(mDrink);
            }
        }

        number_picker.setMinValue(1);
        number_picker.setMaxValue(50);
        number_picker.setValue(8);
        number_picker.setWrapSelectorWheel(false);

        number_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                tv_amount.setText(newVal + " OZ");
            }
        });

        mcurrentTime = Calendar.getInstance();
        tv_time.setText(DateUtils.fromDateToUserReadableHoursString2(mcurrentTime.getTime()));

        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        time_picker.setCurrentHour(hour);
        time_picker.setCurrentMinute(minute);
        time_picker.setIs24HourView(false);

        time_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {

                mcurrentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mcurrentTime.set(Calendar.MINUTE, minute);

                tv_time.setText(DateUtils.fromDateToUserReadableHoursString2(mcurrentTime.getTime()));
            }
        });

        final ManualAdapter.OnItemDeleteListener listener = new ManualAdapter.OnItemDeleteListener() {

            @Override
            public void onItemDelete(final int pos) {

                // TODO: 10/26/20 manual delete from trackers
                // TODO: 10/26/20 manual delete from daily on parse js

                if (list.get(pos).getObjId() != null && !list.get(pos).getObjId().isEmpty()) {

                    ParseQuery<BottleEvents> query = ParseQuery.getQuery("BottleEvents");

                    if (AppUtils.isInternetAvailable(ManualListActivity.this)) {
                        query.fromNetwork();
                    } else {
                        query.fromLocalDatastore();
                    }

                    query.getInBackground(list.get(pos).getObjId(), new GetCallback<BottleEvents>() {
                        public void done(BottleEvents object, ParseException e) {
                            if (e == null) {

                                Log.e("reali", "found BottleEvents to delete");
                                // object will be your game score
                                object.deleteEventually();
                                AppUtils.removeFromLocal(new Date(), list.get(pos).getObjId());
                                updateList();
                            } else {
                                // something went wrong
                                Log.e("reali", "error while finding BottleEvents to delete");
                            }
                        }
                    });

                } else if (list.get(pos).getTempId() != null && !list.get(pos).getTempId().isEmpty()) {

                    ParseQuery<BottleEvents> query = ParseQuery.getQuery("BottleEvents");

                    if (AppUtils.isInternetAvailable(ManualListActivity.this)) {
                        query.fromNetwork();
                    } else {
                        query.fromLocalDatastore();
                    }

                    Log.e("reali", "tempId");
                    query.whereEqualTo("tempId", list.get(pos).getTempId());

                    query.getFirstInBackground(new GetCallback<BottleEvents>() {
                        @Override
                        public void done(BottleEvents object, ParseException e) {
                            if (e == null) {

                                Log.e("reali", "found BottleEvents to delete");
                                // object will be your game score
                                object.deleteEventually();
                                AppUtils.removeTempFromLocal(new Date(), list.get(pos).getTempId());
                                updateList();

                            } else {
                                // something went wrong
                                Log.e("reali", "error while finding BottleEvents to delete");
                            }
                        }
                    });

                }

                Toast.makeText(ManualListActivity.this, "Removed", Toast.LENGTH_SHORT).show();

                NewMainActivity.updateWaterData = true;
            }
        };

        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (number_picker_opened) {
                    number_picker_view.setVisibility(View.GONE);
                    number_picker_opened = false;
                } else {
                    number_picker_view.setVisibility(View.VISIBLE);
                    number_picker_opened = true;

                    time_picker_view.setVisibility(View.GONE);
                    time_picker_opened = false;
                }

            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (time_picker_opened) {
                    time_picker_view.setVisibility(View.GONE);
                    time_picker_opened = false;
                } else {
                    time_picker_view.setVisibility(View.VISIBLE);
                    time_picker_opened = true;

                    number_picker_view.setVisibility(View.GONE);
                    number_picker_opened = false;
                }

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                manuallyAdd(number_picker.getValue(), mcurrentTime.getTime());

            }
        });

        adapter = new ManualAdapter(ManualListActivity.this, list, listener);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

    }

    private void manuallyAdd(final int amount, final Date date) {

        ParseUser user = ParseUser.getCurrentUser();

        String uuid = UUID.randomUUID().toString();

        if (user != null) {

//            final ProgressDialog dialog = DialogUtils.showProgressDialog(ManualListActivity.this, "Sending ...");

//      Log.e("reali", "am - " + Integer.parseInt(et_amount.getText().toString()) + " date - " + et_date.getText().toString());

            final BottleEvents events = new BottleEvents();

            events.setWaterLevel(amount+"");
            events.setEventName("BottleLevelUpdate");
            events.setUserObjectId(user);
            events.setIsManual(true);
            events.setDate(date);

            if (AppUtils.isInternetAvailable(this)) {
                events.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.e("reali", "Event sent goal");
                        events.pinInBackground();
                        AppUtils.syncTrackers(amount, ManualListActivity.this);

                        Toast.makeText(ManualListActivity.this, "Added", Toast.LENGTH_SHORT).show();

                        AppUtils.addToLocal(ManualListActivity.this, amount, date, true, events.getObjectId(), null);
                        Log.e("reali", "obj id " + events.getObjectId());

                        updateList();
                    }
                });
            } else {

                events.setTempId(uuid);
                events.pinInBackground("later");
                AppUtils.addToLocal(ManualListActivity.this, amount, date, true, null, uuid);
                updateList();
                Log.e("reali", "Event pinned goal");
                Toast.makeText(ManualListActivity.this, "Added", Toast.LENGTH_SHORT).show();
            }
        } else {

            AppUtils.addToLocal(ManualListActivity.this, amount, date, true, null, uuid);
            updateList();

            Toast.makeText(ManualListActivity.this, "Added", Toast.LENGTH_SHORT).show();

        }

        NewMainActivity.updateWaterData = true;

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            updateList();
        }
    }

    private void updateList() {

        list.clear();

        int index = AppUtils.getDayIndex(new Date());
        if (index != -1) {
            for (drink mDrink : NewMainActivity.dayList.get(index).list) {
                if (mDrink.isManual())
                    list.add(mDrink);
            }
        }

        adapter.notifyDataSetChanged();

        if (list.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }
    }
}
