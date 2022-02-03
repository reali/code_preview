package com.pa3424.app.stabs1.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pa3424.app.stabs1.R;
import com.pa3424.app.stabs1.parse.Goals;
import com.pa3424.app.stabs1.utils.AppUtils;
import com.pa3424.app.stabs1.utils.Log;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;

public class ChangeWaterGoalsActivity extends AppCompatActivity {

//    private TextView tv_water_goal;
//    private EditText et_water_goal, et_age, et_weight;
//    private Spinner spinner_dr_unit, spinner_w_unit, spinner_sex, spinner_act_lvl;
    public static String[] drink_unit_arr = new String[]{"oz", "ml"};
//    private String[] w_unit_arr, sex_arr, act_lvl_arr;
//    private ArrayAdapter<String> adapterDrUnit, adapter_w_unit, adapter_sex, adapter_act_lvl;
//    private ToggleButton custom_goal;
//
//    private int age, weight;
//    private String drink_unit, w_unit, sex, act_lvl;
//    private boolean isCustomGoal;
//
//    private int index_dr_un, index_w_un, index_w_un_cur, index_sex, index_act_lvl;
//    private int total_plus_final, water_goal_total_old;
//
//    private SharedPreferences sharedPreferences;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_water_goals);
//
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//
//        Button bt_dismiss = (Button) findViewById(R.id.button5);
//        bt_dismiss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                saveToStorage();
//                finish();
//            }
//        });
//
//        et_water_goal = (EditText) findViewById(R.id.et_water_amount);
//        tv_water_goal = (TextView) findViewById(R.id.tv_water_amount);
//        et_age = (EditText) findViewById(R.id.et_age);
//        et_weight = (EditText) findViewById(R.id.et_weight);
//
//        spinner_dr_unit = (Spinner) findViewById(R.id.spinner);
//        spinner_w_unit = (Spinner) findViewById(R.id.spinner_weight_unit);
//        spinner_sex = (Spinner) findViewById(R.id.spinner_sex);
//        spinner_act_lvl = (Spinner) findViewById(R.id.spinner_act_lvl);
//
////        spinner_dr_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
////            @Override
////            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
////
////                Log.e("reali", "selected " + i + " - " + drink_unit_arr[i]);
////
////            }
////
////            @Override
////            public void onNothingSelected(AdapterView<?> adapterView) {
////
////            }
////        });
//
//        w_unit_arr = new String[]{"lbs", "kg"};
//        sex_arr = new String[]{"Female", "Male"};
//        act_lvl_arr = new String[]{"Sedentary", "Low Active", "Active", "Very Active"};
//
//        adapterDrUnit = new ArrayAdapter<String>(this, R.layout.spinner_item, drink_unit_arr);
//        adapter_w_unit = new ArrayAdapter<String>(this, R.layout.spinner_item, w_unit_arr);
//        adapter_sex = new ArrayAdapter<String>(this, R.layout.spinner_item, sex_arr);
//        adapter_act_lvl = new ArrayAdapter<String>(this, R.layout.spinner_item, act_lvl_arr);
//
//        adapterDrUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        adapter_w_unit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        adapter_sex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        adapter_act_lvl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner_dr_unit.setAdapter(adapterDrUnit);
//        spinner_w_unit.setAdapter(adapter_w_unit);
//        spinner_sex.setAdapter(adapter_sex);
//        spinner_act_lvl.setAdapter(adapter_act_lvl);
//
//        spinner_dr_unit.setSelection(0);
//        spinner_w_unit.setSelection(0);
//        spinner_sex.setSelection(0);
//        spinner_act_lvl.setSelection(0);
//
//        spinner_dr_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                int water_goal_current = getWaterCurrentGoal();
//
//                if (index_dr_un != i) {
//                    setWaterGoalUI(water_goal_current);
//                }
//
//                index_dr_un = i;
//
//                recalculate_plus();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
//
//        spinner_w_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                int weight_current = 0;
//                try {
//                    weight_current = Integer.parseInt(et_weight.getText().toString());
//                } catch (Exception e) {
//
//                }
//
//                if (index_w_un_cur != i) {
//                    if (index_w_un_cur == 0) {
//                        et_weight.setText(AppUtils.lbsToKg(weight_current) + "");
//                    } else {
//                        et_weight.setText(AppUtils.kgToLbs(weight_current) + "");
//                    }
//                }
//
//                index_w_un_cur = i;
//
//                recalculate_plus();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
//
//        spinner_sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                recalculate_plus();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
//
//        spinner_act_lvl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                recalculate_plus();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
//
//        custom_goal = (ToggleButton) findViewById(R.id.tg_btn);
//        custom_goal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//
//                handle_goal_ui(checked);
//                custom_goal.setChecked(checked);
//
//            }
//        });
//
////        spinner_dr_unit.setSelection(0);
//
//        getDataFromStorage();
//
//
//        et_water_goal.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                try {
//                    total_plus_final = Integer.parseInt(et_water_goal.getText().toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        et_weight.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                recalculate_plus();
//            }
//        });
//
//    }
//
//    private void handle_goal_ui(boolean checked) {
//
//        if (checked) {
//
//            et_weight.setFocusable(false);
//            et_weight.setEnabled(false);
//            et_age.setFocusable(false);
//            et_age.setEnabled(false);
//
//            spinner_w_unit.setEnabled(false);
//            spinner_sex.setEnabled(false);
//            spinner_act_lvl.setEnabled(false);
//
//            et_water_goal.setFocusableInTouchMode(true);
//            et_water_goal.setEnabled(true);
//
//        } else {
//
//            et_weight.setFocusableInTouchMode(true);
//            et_weight.setEnabled(true);
//            et_age.setFocusableInTouchMode(true);
//            et_age.setEnabled(true);
//
//            spinner_w_unit.setEnabled(true);
//            spinner_sex.setEnabled(true);
//            spinner_act_lvl.setEnabled(true);
//
//            et_water_goal.setFocusable(false);
//            et_water_goal.setEnabled(false);
//        }
//
//        recalculate_plus();
//
//    }
//
//    private int getWaterCurrentGoal() {
//
////        int water_goal_current = 0;
////
////        if (custom_goal.isChecked()) {
////            try {
////                water_goal_current = Integer.parseInt(et_water_goal.getText().toString());
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        } else {
////            try {
////                water_goal_current = Integer.parseInt(tv_water_goal.getText().toString());
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
////
////        //Log.e("reali", "getWaterCurrentGoal " + water_goal_current + " " + total_plus_final);
////
////        return water_goal_current;
//
//        return total_plus_final;
//
//    }
//
//    private void setWaterGoalUI(int water_goal_current) {
//
//        int goal = 0;
//
//        if (index_dr_un == 0) {
//            goal = AppUtils.ozToMl(water_goal_current);
//        } else {
//            goal = AppUtils.mlToOz(water_goal_current);
//        }
//
////        Log.e("reali", "setWaterGoalUI " + goal);
//
//        if (custom_goal.isChecked()) {
//            et_water_goal.setText(goal+"");
//        } else {
//            tv_water_goal.setText(goal+"");
//        }
//    }
//
//    private void recalculate_plus() {
//
//        int water_goal_base_ml = AppUtils.ozToMl(60);
//
//        if (custom_goal.isChecked()) {
//
//            et_water_goal.setVisibility(View.VISIBLE);
//            tv_water_goal.setVisibility(View.GONE);
//
//            et_water_goal.setText(total_plus_final+"");
//            Log.e("reali", "total_plus_final "+total_plus_final);
//
//        } else {
//
//            total_plus_final = 0;
//
//            /////////////////////////////////////////////////////////////////////////
//
//            int weight_current_kg = 0;
//
//            try {
//                weight_current_kg = Integer.parseInt(et_weight.getText().toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            if (index_w_un_cur == 0) {  //if weight kg - ok, lbs - convert
//                weight_current_kg = AppUtils.lbsToKg(weight_current_kg);
//            }
//
//            if (weight_current_kg < 29) {
//                weight_current_kg = 29;
//            }
//
//            /////////////////////////////////////////////////////////////////////////
//
//            int weight_plus = 0;
//
//            int weight_ert = weight_current_kg - 75;
//            if (weight_ert != 0) {
//                weight_plus = weight_ert * 30;  //in ml
//            }
//
//            /////////////////////////////////////////////////////////////////////////
//
//            int index_sex_curr = spinner_sex.getSelectedItemPosition();
//            int sex_plus = 0;
//
////            int sex_plus_percent_15 = water_goal_base_ml / 100 * 15;
//////            Log.e("reali", "sex_plus_percent_15 " + sex_plus_percent_15);
////            if (index_sex_curr == 1) { //was FeMale - now Mmale
////                sex_plus += sex_plus_percent_15; // +15%
////            }
//
//            /////////////////////////////////////////////////////////////////////////
//
//            int index_act_lvl_curr = spinner_act_lvl.getSelectedItemPosition();
//            int act_lvl_plus = 0;
//
//            int act_lvl_percent_20 = water_goal_base_ml / 100 * 20;
//            int act_lvl_percent_35 = water_goal_base_ml / 100 * 35;
//
////            Log.e("reali", "act_lvl_percent_20 " + act_lvl_percent_20);
////            Log.e("reali", "act_lvl_percent_35 " + act_lvl_percent_35);
//
//            if (index_act_lvl_curr == 2) {
//                act_lvl_plus += act_lvl_percent_20; //+20
//            } else if (index_act_lvl_curr == 3) {
//                act_lvl_plus += act_lvl_percent_35; //+35
//            }
//
//            /////////////////////////////////////////////////////////////////////////
//
//        Log.e("reali", "=======================");
////        Log.e("reali", "wat_cur_ml   " + water_goal_current_ml);
//        Log.e("reali", "weight_plus  " + weight_plus);
//        Log.e("reali", "sex_plus     " + sex_plus);
//        Log.e("reali", "act_lvl_plus " + act_lvl_plus);
//        Log.e("reali", "=======================");
//
//            int total_plus_ml = weight_plus + sex_plus + act_lvl_plus;
//
//            if (index_dr_un == 0) { //oz
//                total_plus_final = 60 + AppUtils.mlToOz(total_plus_ml);
//            } else {
//                total_plus_final = AppUtils.ozToMl(60) + total_plus_ml;
//            }
//
//
//            et_water_goal.setVisibility(View.INVISIBLE);
//            tv_water_goal.setVisibility(View.VISIBLE);
//
//            tv_water_goal.setText(total_plus_final + "");
////            Log.e("reali", "total_plus_final "+total_plus_final);
//        }
//
//    }
//
//    private void saveToStorage() {
//
//        int water_goal_current = getWaterCurrentGoal();
//
//        int age_current = 0;
//        try {
//            age_current = Integer.parseInt(et_age.getText().toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        int weight_current = 0;
//        try {
//            weight_current = Integer.parseInt(et_weight.getText().toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        int drink_unit_current_index = spinner_dr_unit.getSelectedItemPosition();
//        int w_unit_current_index = spinner_w_unit.getSelectedItemPosition();
//
//        String drink_unit_current = drink_unit_arr[drink_unit_current_index];
//        String w_unit_current = w_unit_arr[w_unit_current_index];
//        String sex_current = sex_arr[spinner_sex.getSelectedItemPosition()];
//        String act_lvl_current = act_lvl_arr[spinner_act_lvl.getSelectedItemPosition()];
//        boolean custom_goal_current = custom_goal.isChecked();
//
//        if (water_goal_total_old != water_goal_current) {
//
////            Log.e("reali", "water_goal_total_old != total_goal");
//
//            if (drink_unit_current_index == 0) { //oz;
//                AppUtils.waterLevelMl =  AppUtils.ozToMl(water_goal_current);
//                AppUtils.setDayGoal(AppUtils.ozToMl(water_goal_current), new Date(), ChangeWaterGoalsActivity.this);
//            } else {
//                AppUtils.waterLevelMl =  water_goal_current;
//                AppUtils.setDayGoal(water_goal_current, new Date(), ChangeWaterGoalsActivity.this);
//            }
//
//            NewMainActivity.updateWaterData = true;
//        }
//
//        if (age != age_current) {
//            AppUtils.age = age_current;
//        }
//
//        if (weight != weight_current) {
//
//            if (w_unit_current_index == 0) { //lbs
//                AppUtils.weightKg = AppUtils.lbsToKg(weight_current);
//            } else {
//                AppUtils.weightKg = weight_current;
//            }
//        }
//
//        if (!drink_unit_current.equals(drink_unit)) {
//            AppUtils.waterUn = spinner_dr_unit.getSelectedItemPosition();
//        }
//
//        if (!w_unit_current.equals(w_unit)) {
//            AppUtils.weightUn = spinner_w_unit.getSelectedItemPosition();
//        }
//
//        if (!sex_current.equals(sex)) {
//            AppUtils.sex = spinner_sex.getSelectedItemPosition();
//        }
//
//        if (!act_lvl_current.equals(act_lvl)) {
//            AppUtils.actLvl = spinner_act_lvl.getSelectedItemPosition();
//        }
//
//        if (custom_goal_current != isCustomGoal) {
//            AppUtils.isCustomGoalOff = !custom_goal_current;
//        }
//
//        /////////////////////////////////
//
//        int water_goal_ml = 0;
//        int total_goal_ml = 0;
//
//        if (index_dr_un == 0) { //if water ml - ok, oz - convert
//            water_goal_ml = AppUtils.ozToMl(water_goal_current);
//            total_goal_ml = AppUtils.ozToMl(water_goal_current);
//        } else {
//            water_goal_ml = water_goal_current;
//            total_goal_ml = water_goal_current;
//        }
//
//        /////////////////////////////////////////////////////////////////////////
//
//        int weight_kg = 0;
//
//        if (index_w_un_cur == 0) {  //if weight kg - ok, lbs - convert
//            weight_kg = AppUtils.lbsToKg(weight_current);
//        } else {
//            weight_kg = weight_current;
//        }
//
//        ////////////////////////////////
//
//        Goals goals = new Goals();
//        goals.setWaterLevel(total_goal_ml);
//        goals.setIsCustomGoalOff(!custom_goal_current);
//        goals.setWeight(weight_kg);
//        goals.setAge(age_current);
//        goals.setSex(spinner_sex.getSelectedItemPosition());
//        goals.setActivityLvl(spinner_act_lvl.getSelectedItemPosition());
//        goals.setWaterUn(spinner_dr_unit.getSelectedItemPosition());
//        goals.setWeightUn(spinner_w_unit.getSelectedItemPosition());
//
//        ParseUser user = ParseUser.getCurrentUser();
//
//        if (user != null) {
//            goals.put("userObject", user);
//
//            goals.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e != null) {
//                        Toast.makeText(
//                                getApplicationContext(),
//                                "Error goals saving: " + e.getMessage(),
//                                Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.e("reali", "Goals sent");
//                    }
//                }
//
//            });
//        }
//
//    }
//
//    private void getDataFromStorage() {
//
//        age = AppUtils.age;
//        drink_unit = drink_unit_arr[AppUtils.waterUn];
//        w_unit = w_unit_arr[AppUtils.weightUn];
//        sex = sex_arr[AppUtils.sex];
//        act_lvl = act_lvl_arr[AppUtils.actLvl];
//        isCustomGoal = !AppUtils.isCustomGoalOff;
//
//        if (AppUtils.weightUn == 0) { //lbs
//            weight = AppUtils.kgToLbs(AppUtils.weightKg);
//        } else { //kg
//            weight = AppUtils.weightKg;
//        }
//
//        if (AppUtils.waterUn == 0) { //oz
//            water_goal_total_old = AppUtils.mlToOz(AppUtils.waterLevelMl);
//        } else {  //ml
//            water_goal_total_old = AppUtils.waterLevelMl;
//        }
//
//        Log.e("reali", "age " + age + " wat " + water_goal_total_old + drink_unit + " s " + sex + " " + act_lvl + " wei " + weight + w_unit + " isCust " + isCustomGoal);
//
//        total_plus_final = water_goal_total_old;
//
//        et_water_goal.setText(water_goal_total_old + "");
//        et_age.setText(age + "");
//        et_weight.setText(weight + "");
//
//        index_dr_un = findItemIndex(drink_unit, drink_unit_arr);
//        index_w_un_cur = index_w_un = findItemIndex(w_unit, w_unit_arr);
//        index_sex = findItemIndex(sex, sex_arr);
//        index_act_lvl = findItemIndex(act_lvl, act_lvl_arr);
//
//        spinner_dr_unit.setSelection(index_dr_un);
//        spinner_w_unit.setSelection(index_w_un);
//        spinner_sex.setSelection(index_sex);
//        spinner_act_lvl.setSelection(index_act_lvl);
//
//        custom_goal.setChecked(isCustomGoal);
//        handle_goal_ui(isCustomGoal);
//
//    }
//
//    private int findItemIndex(String item, String[] arr) {
//
//        if (item.equals(""))
//            return 0;
//
//        for (int i = 0; i < arr.length; i++) {
//            if (arr[i].equals(item))
//                return i;
//        }
//
//        return 0;
//    }

}
