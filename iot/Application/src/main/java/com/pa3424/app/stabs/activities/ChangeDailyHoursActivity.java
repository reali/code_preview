package com.pa3424.app.stabs1.activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.pa3424.app.stabs1.R;
import com.pa3424.app.stabs1.utils.DateUtils;

import java.util.Calendar;

public class ChangeDailyHoursActivity extends AppCompatActivity {

    private EditText et_from, et_to;
    private Calendar mDateFrom, mDateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_hours);

        et_from = (EditText) findViewById(R.id.et_from);
        et_to = (EditText) findViewById(R.id.et_to);

        et_from.setInputType(InputType.TYPE_NULL);
        et_to.setInputType(InputType.TYPE_NULL);

        mDateFrom = Calendar.getInstance();
        mDateTo = Calendar.getInstance();

        mDateFrom.set(Calendar.HOUR_OF_DAY, 6);
        mDateFrom.set(Calendar.MINUTE, 0);

        mDateTo.set(Calendar.HOUR_OF_DAY, 22);
        mDateTo.set(Calendar.MINUTE, 0);

        et_from.setText(DateUtils.fromDateToUserReadableHoursString(mDateFrom.getTime()));
        et_to.setText(DateUtils.fromDateToUserReadableHoursString(mDateTo.getTime()));

        et_from.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                new TimePickerDialog(ChangeDailyHoursActivity.this, mTimeFrom, mDateFrom
                        .get(Calendar.HOUR_OF_DAY), mDateFrom.get(Calendar.MINUTE), true).show();
            }
        });

        et_to.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                new TimePickerDialog(ChangeDailyHoursActivity.this, mTimeTo, mDateTo
                        .get(Calendar.HOUR_OF_DAY), mDateTo.get(Calendar.MINUTE), true).show();
            }
        });

        Button bt_dismiss = (Button) findViewById(R.id.button5);
        bt_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    final TimePickerDialog.OnTimeSetListener mTimeFrom = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
            mDateFrom.set(Calendar.HOUR_OF_DAY, hours);
            mDateFrom.set(Calendar.MINUTE, minutes);
            mDateFrom.set(Calendar.SECOND, 0);

            et_from.setText(DateUtils.fromDateToUserReadableHoursString(mDateFrom.getTime()));
        }
    };

    final TimePickerDialog.OnTimeSetListener mTimeTo = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
            mDateTo.set(Calendar.HOUR_OF_DAY, hours);
            mDateTo.set(Calendar.MINUTE, minutes);
            mDateTo.set(Calendar.SECOND, 0);

            et_to.setText(DateUtils.fromDateToUserReadableHoursString(mDateTo.getTime()));
        }
    };

}
