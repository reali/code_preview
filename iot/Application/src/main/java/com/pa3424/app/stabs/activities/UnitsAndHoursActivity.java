package com.pa3424.app.stabs1.activities;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pa3424.app.stabs1.R;
import com.pa3424.app.stabs1.parse.Goals;
import com.pa3424.app.stabs1.utils.AppUtils;
import com.pa3424.app.stabs1.utils.DateUtils;
import com.pa3424.app.stabs1.utils.Log;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UnitsAndHoursActivity extends AppCompatActivity {

    @BindView(R.id.back) ImageView back;
    @BindView(R.id.tv_save) TextView tv_save;

    @BindView(R.id.water_units) View water_units;
    @BindView(R.id.temp_units) View temp_units;
    @BindView(R.id.weight_units) View weight_units;
    @BindView(R.id.day_start) View day_start;
    @BindView(R.id.day_end) View day_end;

    //weight_view

    @BindView(R.id.tv_water_units) TextView tv_water_units;
    @BindView(R.id.tv_temp_units) TextView tv_temp_units;
    @BindView(R.id.tv_weight_units) TextView tv_weight_units;
    @BindView(R.id.tv_day_start) TextView tv_day_start;
    @BindView(R.id.tv_day_end) TextView tv_day_end;

    @BindView(R.id.number_picker_temp) NumberPicker number_picker_temp;
    @BindView(R.id.number_picker_temp_view) View number_picker_temp_view;
    @BindView(R.id.number_picker_weight) NumberPicker number_picker_weight;
    @BindView(R.id.number_picker_weight_view) View number_picker_weight_view;
    @BindView(R.id.number_picker_water) NumberPicker number_picker_water;
    @BindView(R.id.number_picker_water_view) View number_picker_water_view;

    @BindView(R.id.time_picker_day_start) TimePicker time_picker_day_start;
    @BindView(R.id.time_picker_start) View time_picker_start;
    @BindView(R.id.time_picker_day_end) TimePicker time_picker_day_end;
    @BindView(R.id.time_picker_end) View time_picker_end;

    private boolean number_picker_water_opened = false;
    private boolean number_picker_temp_opened = false;
    private boolean number_picker_weight_opened = false;
    private boolean time_picker_day_start_opened = false;
    private boolean time_picker_day_end_opened = false;

    String[] array_water = new String[] { "Ounces", "Milliliters"};
    String[] array_temp = new String[] {"Fahrenheit", "Celsius"};
    String[] array_weight = new String[] {"Lbs", "Kilogram"};
    String[] sex_arr = new String[] {"Female", "Male"};
    String[] act_lvl_arr = new String[] {"Sedentary", "Low Active", "Active", "Very Active"};
    String[] drink_unit_arr = new String[]{"oz", "ml"};
    String[] w_unit_arr = new String[]{"lbs", "kg"};

    Calendar mcurrentTime_start, mcurrentTime_end;

    private boolean isCustomGoal;
    private int age, weight;
    private String drink_unit, w_unit, sex, act_lvl;

    private int index_dr_un, index_w_un, index_w_un_cur, index_sex, index_act_lvl;
    private int total_plus_final, water_goal_total_old;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units_and_hours);
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

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToStorage();
                finish();
            }
        });

        number_picker_water.setMinValue(0);
        number_picker_water.setMaxValue(1);
        number_picker_water.setDisplayedValues(array_water);
        number_picker_water.setWrapSelectorWheel(false);

        number_picker_temp.setMinValue(0);
        number_picker_temp.setMaxValue(1);
        number_picker_temp.setDisplayedValues(array_temp);
        number_picker_temp.setWrapSelectorWheel(false);

        number_picker_weight.setMinValue(0);
        number_picker_weight.setMaxValue(1);
        number_picker_weight.setDisplayedValues(array_weight);
        number_picker_weight.setWrapSelectorWheel(false);

        number_picker_water.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                tv_water_units.setText(array_water[newVal]);

                recalculate_plus();
            }
        });

        number_picker_temp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                tv_temp_units.setText(array_temp[newVal]);
            }
        });

        number_picker_weight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                tv_weight_units.setText(array_weight[newVal]);

                recalculate_plus();
            }
        });

        mcurrentTime_start = Calendar.getInstance();
        mcurrentTime_start.set(Calendar.HOUR_OF_DAY, 8);
        mcurrentTime_start.set(Calendar.MINUTE, 0);

        tv_day_start.setText(DateUtils.fromDateToUserReadableHoursString2(mcurrentTime_start.getTime()));

        time_picker_day_start.setCurrentHour(mcurrentTime_start.get(Calendar.HOUR_OF_DAY));
        time_picker_day_start.setCurrentMinute(0);
        time_picker_day_start.setIs24HourView(false);

        time_picker_day_start.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {

                mcurrentTime_start.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mcurrentTime_start.set(Calendar.MINUTE, minute);

                tv_day_start.setText(DateUtils.fromDateToUserReadableHoursString2(mcurrentTime_start.getTime()));
            }
        });

        //

        mcurrentTime_end = Calendar.getInstance();
        mcurrentTime_end.set(Calendar.HOUR_OF_DAY, 22);
        mcurrentTime_end.set(Calendar.MINUTE, 0);

        tv_day_end.setText(DateUtils.fromDateToUserReadableHoursString2(mcurrentTime_end.getTime()));

        time_picker_day_end.setCurrentHour(mcurrentTime_end.get(Calendar.HOUR_OF_DAY));
        time_picker_day_end.setCurrentMinute(0);
        time_picker_day_end.setIs24HourView(false);

        time_picker_day_end.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {

                mcurrentTime_end.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mcurrentTime_end.set(Calendar.MINUTE, minute);

                tv_day_end.setText(DateUtils.fromDateToUserReadableHoursString2(mcurrentTime_end.getTime()));
            }
        });

        //

        water_units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (number_picker_water_opened) {
                    number_picker_water_view.setVisibility(View.GONE);
                    number_picker_water_opened = false;
                } else {
                    number_picker_water_view.setVisibility(View.VISIBLE);
                    number_picker_water_opened = true;
                }

            }
        });

        temp_units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (number_picker_temp_opened) {
                    number_picker_temp_view.setVisibility(View.GONE);
                    number_picker_temp_opened = false;
                } else {
                    number_picker_temp_view.setVisibility(View.VISIBLE);
                    number_picker_temp_opened = true;
                }

            }
        });

        weight_units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (number_picker_weight_opened) {
                    number_picker_weight_view.setVisibility(View.GONE);
                    number_picker_weight_opened = false;
                } else {
                    number_picker_weight_view.setVisibility(View.VISIBLE);
                    number_picker_weight_opened = true;
                }

            }
        });

        day_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (time_picker_day_start_opened) {
                    time_picker_start.setVisibility(View.GONE);
                    time_picker_day_start_opened = false;
                } else {
                    time_picker_start.setVisibility(View.VISIBLE);
                    time_picker_day_start_opened = true;
                }

            }
        });

        day_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (time_picker_day_end_opened) {
                    time_picker_end.setVisibility(View.GONE);
                    time_picker_day_end_opened = false;
                } else {
                    time_picker_end.setVisibility(View.VISIBLE);
                    time_picker_day_end_opened = true;
                }

            }
        });

        String drink_unit = array_water[AppUtils.waterUn];
        String w_unit = array_weight[AppUtils.weightUn];

        number_picker_water.setValue(AppUtils.waterUn);
        number_picker_weight.setValue(AppUtils.weightUn);

        tv_water_units.setText(drink_unit);
        tv_weight_units.setText(w_unit);

        getDataFromStorage();

    }

    private void getDataFromStorage() {

        age = AppUtils.age;
        drink_unit = drink_unit_arr[AppUtils.waterUn];
        w_unit = w_unit_arr[AppUtils.weightUn];
        sex = sex_arr[AppUtils.sex];
        act_lvl = act_lvl_arr[AppUtils.actLvl];
        isCustomGoal = !AppUtils.isCustomGoalOff;

        if (AppUtils.weightUn == 0) { //lbs
            weight = AppUtils.kgToLbs(AppUtils.weightKg);
        } else { //kg
            weight = AppUtils.weightKg;
        }

        if (AppUtils.waterUn == 0) { //oz
            water_goal_total_old = AppUtils.mlToOz(AppUtils.waterLevelMl);
        } else {  //ml
            water_goal_total_old = AppUtils.waterLevelMl;
        }

        total_plus_final = water_goal_total_old;

        index_dr_un = findItemIndex(drink_unit, drink_unit_arr);
        index_w_un_cur = index_w_un = findItemIndex(w_unit, w_unit_arr);
        index_sex = findItemIndex(sex, sex_arr);
        index_act_lvl = findItemIndex(act_lvl, act_lvl_arr);

    }

    private int findItemIndex(String item, String[] arr) {

        if (item.equals(""))
            return 0;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(item))
                return i;
        }

        return 0;
    }

    private void recalculate_plus() {

        int water_goal_base_ml = AppUtils.ozToMl(60);

        if (isCustomGoal) {
            Log.e("reali", "total_plus_final "+total_plus_final);
        } else {

            total_plus_final = 0;

            /////////////////////////////////////////////////////////////////////////

            int weight_current_kg = weight;

            if (index_w_un_cur == 0) {  //if weight kg - ok, lbs - convert
                weight_current_kg = AppUtils.lbsToKg(weight_current_kg);
            }

            if (weight_current_kg < 29) {
                weight_current_kg = 29;
            }

            /////////////////////////////////////////////////////////////////////////

            int weight_plus = 0;

            int weight_ert = weight_current_kg - 75;
            if (weight_ert != 0) {
                weight_plus = weight_ert * 30;  //in ml
            }

            /////////////////////////////////////////////////////////////////////////

            int sex_plus = 0;

//            int sex_plus_percent_15 = water_goal_base_ml / 100 * 15;
////            Log.e("reali", "sex_plus_percent_15 " + sex_plus_percent_15);
//            if (index_sex_curr == 1) { //was FeMale - now Mmale
//                sex_plus += sex_plus_percent_15; // +15%
//            }

            /////////////////////////////////////////////////////////////////////////

            int act_lvl_plus = 0;

            int act_lvl_percent_20 = water_goal_base_ml / 100 * 20;
            int act_lvl_percent_35 = water_goal_base_ml / 100 * 35;

//            Log.e("reali", "act_lvl_percent_20 " + act_lvl_percent_20);
//            Log.e("reali", "act_lvl_percent_35 " + act_lvl_percent_35);

            if (index_act_lvl == 2) {
                act_lvl_plus += act_lvl_percent_20; //+20
            } else if (index_act_lvl == 3) {
                act_lvl_plus += act_lvl_percent_35; //+35
            }

            /////////////////////////////////////////////////////////////////////////

            Log.e("reali", "=======================");
//        Log.e("reali", "wat_cur_ml   " + water_goal_current_ml);
            Log.e("reali", "weight_plus  " + weight_plus);
            Log.e("reali", "sex_plus     " + sex_plus);
            Log.e("reali", "act_lvl_plus " + act_lvl_plus);
            Log.e("reali", "=======================");

            int total_plus_ml = weight_plus + sex_plus + act_lvl_plus;

            if (index_dr_un == 0) { //oz
                total_plus_final = 60 + AppUtils.mlToOz(total_plus_ml);
            } else {
                total_plus_final = AppUtils.ozToMl(60) + total_plus_ml;
            }

            Log.e("reali", "total_plus_final " + total_plus_final);
        }

    }

    private void saveToStorage() {

        int water_goal_current = total_plus_final;

        int age_current = age;
        int weight_current = weight;

        int drink_unit_current_index = number_picker_water.getValue();
        int w_unit_current_index = number_picker_weight.getValue();

        String drink_unit_current = drink_unit_arr[drink_unit_current_index];
        String w_unit_current = w_unit_arr[w_unit_current_index];

        if (water_goal_total_old != water_goal_current) {

//            Log.e("reali", "water_goal_total_old != total_goal");

            if (drink_unit_current_index == 0) { //oz;
                AppUtils.waterLevelMl =  AppUtils.ozToMl(water_goal_current);
                AppUtils.setDayGoal(AppUtils.ozToMl(water_goal_current), new Date(), UnitsAndHoursActivity.this);
            } else {
                AppUtils.waterLevelMl =  water_goal_current;
                AppUtils.setDayGoal(water_goal_current, new Date(), UnitsAndHoursActivity.this);
            }

            NewMainActivity.updateWaterData = true;
        }

        if (weight != weight_current) {

            if (w_unit_current_index == 0) { //lbs
                AppUtils.weightKg = AppUtils.lbsToKg(weight_current);
            } else {
                AppUtils.weightKg = weight_current;
            }
        }

        if (!drink_unit_current.equals(drink_unit)) {
            AppUtils.waterUn = drink_unit_current_index;
        }

        if (!w_unit_current.equals(w_unit)) {
            AppUtils.weightUn = w_unit_current_index;
        }

        /////////////////////////////////

        int water_goal_ml = 0;
        int total_goal_ml = 0;

        if (index_dr_un == 0) { //if water ml - ok, oz - convert
            water_goal_ml = AppUtils.ozToMl(water_goal_current);
            total_goal_ml = AppUtils.ozToMl(water_goal_current);
        } else {
            water_goal_ml = water_goal_current;
            total_goal_ml = water_goal_current;
        }

        /////////////////////////////////////////////////////////////////////////

        int weight_kg = 0;

        if (index_w_un_cur == 0) {  //if weight kg - ok, lbs - convert
            weight_kg = AppUtils.lbsToKg(weight_current);
        } else {
            weight_kg = weight_current;
        }

        ////////////////////////////////

        Goals goals = new Goals();
        goals.setWaterLevel(total_goal_ml);
        goals.setIsCustomGoalOff(!isCustomGoal);
        goals.setWeight(weight_kg);
        goals.setAge(age_current);
        goals.setSex(index_sex);
        goals.setActivityLvl(index_act_lvl);
        goals.setWaterUn(drink_unit_current_index);
        goals.setWeightUn(w_unit_current_index);

        ParseUser user = ParseUser.getCurrentUser();
        goals.put("userObject", user);

        goals.saveEventually();
        Log.e("reali", "Goals sent eventually");

    }

}
