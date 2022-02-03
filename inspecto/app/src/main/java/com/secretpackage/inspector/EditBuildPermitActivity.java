package com.secretpackage.inspector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.secretpackage.inspector.model.BuildingPermit;
import com.secretpackage.inspector.model.PermitChanges;
import com.secretpackage.inspector.model.PropChanges;
import com.secretpackage.inspector.model.Property2;
import com.secretpackage.inspector.util.DateUtils;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditBuildPermitActivity extends AppCompatActivity {

    PropChanges propCh;
    Property2 property2;
    BuildingPermit b_permit;
    PermitChanges b_permit_changes;

    @BindView(R.id.et_date) EditText et_date;
    @BindView(R.id.et_c_date) EditText et_c_date;
    @BindView(R.id.tv_i_date) TextView tv_i_date;
//    @BindView(R.id.bt_change_date) Button bt_change_date;
//    @BindView(R.id.bt_change_c_date) Button bt_change_c_date;
    @BindView(R.id.tv_type) TextView tv_type;
    @BindView(R.id.tv_price) TextView tv_price;
    @BindView(R.id.tv_note) TextView tv_note;
    @BindView(R.id.et_percent) EditText et_percent;
    @BindView(R.id.seekBar_percent) SeekBar seekBar_percent;
    @BindView(R.id.bt_save) Button bt_save;

    Calendar mInspCalendar, mComplCalendar;
//    DatePickerDialog.OnDateSetListener date, cdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_build_permit);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int pos = intent.getIntExtra("pos", 0);

        if (intent.hasExtra("propid")) {

            int prop_id = intent.getIntExtra("propid", 0);

            for (PropChanges pch : MainActivity.mListChanges) {
                if (pch.prop_id == prop_id) {
                    propCh = pch;
                    b_permit_changes = propCh.b_permits.get(pos);
                    b_permit = b_permit_changes.permit;
                }
            }

        } else {

            String acc_num = intent.getStringExtra("acc_num");

            for (Property2 p : MainActivity.mList2) {
                if (p.getAccount_Number().equals(acc_num)) {
                    property2 = p;
                    b_permit = property2.getB_permits().get(pos);
                }
            }

        }

        if (b_permit == null) {
            finish();
        }

        if (b_permit.getIssue_date() != null)
            tv_i_date.setText(DateUtils.fromRawStringToString(b_permit.getIssue_date()));

        if (b_permit.getCompl_date() != null)
            et_c_date.setText(DateUtils.fromRawStringToString(b_permit.getCompl_date()));
        else
            et_c_date.setText(DateUtils.fromDateToString(new Date()));

        if (b_permit.getInsp_date() != null)
            et_date.setText(DateUtils.fromRawStringToString(b_permit.getInsp_date()));
        else
            et_date.setText(DateUtils.fromDateToString(new Date()));

        Log.e("reali", "i " + b_permit.getInsp_date() + " ii " + b_permit.getCompl_date() + " iii " +
                b_permit.getIssue_date());

//        TextWatcher tw = new TextWatcher() {
//
////            private String current = "";
////            private String ddmmyyyy = "DDMMYYYY";
////            private Calendar cal = Calendar.getInstance();
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        };
//
//        TextWatcher tw_c = new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        };

//        et_c_date.addTextChangedListener(tw_c);
//        et_date.addTextChangedListener(tw);

        DecimalFormat formatter = new DecimalFormat("#,###");

        tv_type.setText(b_permit.getPermit_type() + " - " + b_permit.getDescription());
        tv_price.setText("$" + formatter.format(b_permit.getAmount()));
        tv_note.setText(b_permit.getComments());

        et_percent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();

                int perc = 0;

                try {
                    perc = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                seekBar_percent.setProgress(perc);

            }
        });

        et_percent.setText(b_permit.getPercentage()+"");

        seekBar_percent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                et_percent.setText(i + "");
                et_percent.setSelection(et_percent.getText().length());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mInspCalendar = Calendar.getInstance();
        mComplCalendar = Calendar.getInstance();

        if (b_permit.getInsp_date() != null) {

            Date inspDate = DateUtils.fromRawStringToDate(b_permit.getInsp_date());

            if (inspDate == null) {
                inspDate = new Date();
            }

            mInspCalendar.setTime(inspDate);
        } else {
            mInspCalendar.setTime(new Date());
        }

        if (b_permit.getCompl_date() != null) {

            Date complDate = DateUtils.fromRawStringToDate(b_permit.getCompl_date());

            if (complDate == null) {
                complDate = new Date();
            }

            mComplCalendar.setTime(complDate);
        } else {
            mComplCalendar.setTime(new Date());
        }

        et_date.setText(DateUtils.fromDateToString(mInspCalendar.getTime()));
        et_c_date.setText(DateUtils.fromDateToString(mComplCalendar.getTime()));

//        date = new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                mInspCalendar.set(Calendar.YEAR, year);
//                mInspCalendar.set(Calendar.MONTH, monthOfYear);
//                mInspCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//                tv_date.setText(DateUtils.fromDateToString(mInspCalendar.getTime()));
//            }
//
//        };
//
//        cdate = new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                mComplCalendar.set(Calendar.YEAR, year);
//                mComplCalendar.set(Calendar.MONTH, monthOfYear);
//                mComplCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//                tv_c_date.setText(DateUtils.fromDateToString(mComplCalendar.getTime()));
//            }
//
//        };

//        bt_change_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new DatePickerDialog(EditBuildPermitActivity.this, date, mInspCalendar
//                        .get(Calendar.YEAR), mInspCalendar.get(Calendar.MONTH),
//                        mInspCalendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });
//
//        bt_change_c_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new DatePickerDialog(EditBuildPermitActivity.this, cdate, mComplCalendar
//                        .get(Calendar.YEAR), mComplCalendar.get(Calendar.MONTH),
//                        mComplCalendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newComplDate = et_c_date.getText().toString();
                String newInspDate = et_date.getText().toString();
                int newPercentage = seekBar_percent.getProgress();

                if (b_permit.getInsp_date() == null || b_permit.getCompl_date() == null ||
                        !b_permit.getCompl_date().equals(newComplDate) ||
                        b_permit.getInsp_date().equals(newInspDate) ||
                        b_permit.getPercentage() != newPercentage) {

                    b_permit_changes.isUpdated = true;
                    b_permit_changes.selected = true;

                    b_permit.setCompl_date(newComplDate);
                    b_permit.setInsp_date(newInspDate);
                    b_permit.setPercentage(newPercentage);
                }

                setResult(1313);
                finish();
            }
        });
    }

}
