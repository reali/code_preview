package com.pa3424.app.stabs1.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pa3424.app.stabs1.R;
import com.pa3424.app.stabs1.parse.Goals;
import com.pa3424.app.stabs1.utils.AppUtils;
import com.pa3424.app.stabs1.utils.Log;
import com.parse.ParseUser;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeWaterGoalsNewActivity extends AppCompatActivity {

    @BindView(R.id.back) ImageView back;
    @BindView(R.id.tv_save) TextView tv_save;

    @BindView(R.id.goal) View goal_view;
    @BindView(R.id.weight) View weight_view;
    @BindView(R.id.age) View age_view;
    @BindView(R.id.sex) View sex_view;
    @BindView(R.id.activity_lvl) View activity_lvl_view;

    @BindView(R.id.number_picker_goal_view) View number_picker_goal_view;
    @BindView(R.id.picker_weight_view) View picker_weight_view;
    @BindView(R.id.number_picker_age_view) View number_picker_age_view;
    @BindView(R.id.picker_sex_view) View picker_sex_view;
    @BindView(R.id.picker_act_lvl_view) View picker_act_lvl_view;

    @BindView(R.id.number_picker_goal) NumberPicker number_picker_goal;
    @BindView(R.id.picker_weight) NumberPicker picker_weight;
    @BindView(R.id.number_picker_age) NumberPicker number_picker_age;
    @BindView(R.id.picker_sex) NumberPicker picker_sex;
    @BindView(R.id.picker_act_lvl) NumberPicker picker_act_lvl;

    @BindView(R.id.tv_goal) TextView tv_goal;
    @BindView(R.id.tv_weight) TextView tv_weight;
    @BindView(R.id.tv_age) TextView tv_age;
    @BindView(R.id.tv_sex) TextView tv_sex;
    @BindView(R.id.tv_activity_lvl) TextView tv_activity_lvl;


    @BindView(R.id.tv_weight_title) TextView tv_weight_title;
    @BindView(R.id.tv_age_title) TextView tv_age_title;
    @BindView(R.id.tv_sex_title) TextView tv_sex_title;
    @BindView(R.id.tv_act_lvl_title) TextView tv_act_lvl_title;

    @BindView(R.id.tv_auto_goal_status) TextView tv_auto_goal_status;

    @BindView(R.id.auto_goal) ToggleButton auto_goal;

    private boolean picker_goal_opened = false;
    private boolean picker_weight_opened = false;
    private boolean picker_age_opened = false;
    private boolean picker_sex_opened = false;
    private boolean picker_act_lvl_opened = false;

    private String waterSuffix, weightSuffix;

    private int age, weight;
    private String drink_unit, w_unit, sex, act_lvl;
    private boolean isCustomGoal;

    private int index_dr_un, index_w_un, index_w_un_cur, index_sex, index_act_lvl;
    private int total_plus_final, water_goal_total_old;

    String[] w_unit_arr = new String[]{"lbs", "kg"};
    String[] drink_unit_arr = new String[]{"oz", "ml"};

    String[] sex_arr = new String[]{"Female", "Male"};
    String[] act_lvl_arr = new String[]{"Sedentary", "Low Active", "Active", "Very Active"};

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_goals_new);
        ButterKnife.bind(this);

//        goal_view.setOnClickListener(goal_listener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#036aab"));
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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

        weight_view.setOnClickListener(weight_listener);
        age_view.setOnClickListener(age_listener);
        sex_view.setOnClickListener(sex_listener);
        activity_lvl_view.setOnClickListener(act_lvl_listener);

        auto_goal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                handle_goal_ui(checked);
                auto_goal.setChecked(checked);

            }
        });

        number_picker_goal.setWrapSelectorWheel(false);
        picker_weight.setWrapSelectorWheel(false);

        number_picker_age.setMinValue(16);
        number_picker_age.setMaxValue(150);
        number_picker_age.setWrapSelectorWheel(false);

        picker_sex.setMinValue(0);
        picker_sex.setMaxValue(1);
        picker_sex.setDisplayedValues(sex_arr);
        picker_sex.setWrapSelectorWheel(false);

        picker_act_lvl.setMinValue(0);
        picker_act_lvl.setMaxValue(3);
        picker_act_lvl.setDisplayedValues(act_lvl_arr);
        picker_act_lvl.setWrapSelectorWheel(false);

        getDataFromStorage();

        number_picker_goal.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                total_plus_final = newVal;
                tv_goal.setText(total_plus_final + " " + waterSuffix);
            }
        });

        picker_weight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                tv_weight.setText(newVal + " " + weightSuffix);

                Log.e("reali", "weight " + newVal + " " + picker_weight.getValue());

                recalculate_plus();
            }
        });

        number_picker_age.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                tv_age.setText(newVal + "");

                recalculate_plus();
            }
        });

        picker_sex.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                tv_sex.setText(sex_arr[newVal]);

                recalculate_plus();
            }
        });

        picker_act_lvl.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                tv_activity_lvl.setText(act_lvl_arr[newVal]);

                recalculate_plus();
            }
        });

    }

    private void handle_goal_ui(boolean checked) {

        if (checked) {

            tv_auto_goal_status.setText("On");

            tv_goal.setTextColor(Color.parseColor("#66ffffff"));

            tv_age.setTextColor(Color.parseColor("#ffffff"));
            tv_weight.setTextColor(Color.parseColor("#ffffff"));
            tv_sex.setTextColor(Color.parseColor("#ffffff"));
            tv_activity_lvl.setTextColor(Color.parseColor("#ffffff"));

            tv_age_title.setTextColor(Color.parseColor("#ffffff"));
            tv_weight_title.setTextColor(Color.parseColor("#ffffff"));
            tv_sex_title.setTextColor(Color.parseColor("#ffffff"));
            tv_act_lvl_title.setTextColor(Color.parseColor("#ffffff"));

            goal_view.setOnClickListener(null);

            weight_view.setOnClickListener(weight_listener);
            age_view.setOnClickListener(age_listener);
            sex_view.setOnClickListener(sex_listener);
            activity_lvl_view.setOnClickListener(act_lvl_listener);

        } else {

            tv_auto_goal_status.setText("Off");

            tv_goal.setTextColor(Color.parseColor("#ffffff"));

            tv_age.setTextColor(Color.parseColor("#66ffffff"));
            tv_weight.setTextColor(Color.parseColor("#66ffffff"));
            tv_sex.setTextColor(Color.parseColor("#66ffffff"));
            tv_activity_lvl.setTextColor(Color.parseColor("#66ffffff"));

            tv_age_title.setTextColor(Color.parseColor("#66ffffff"));
            tv_weight_title.setTextColor(Color.parseColor("#66ffffff"));
            tv_sex_title.setTextColor(Color.parseColor("#66ffffff"));
            tv_act_lvl_title.setTextColor(Color.parseColor("#66ffffff"));

            goal_view.setOnClickListener(goal_listener);

            age_view.setOnClickListener(null);
            weight_view.setOnClickListener(null);
            sex_view.setOnClickListener(null);
            activity_lvl_view.setOnClickListener(null);

        }

        recalculate_plus();

    }

    View.OnClickListener goal_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (picker_goal_opened) {
                number_picker_goal_view.setVisibility(View.GONE);
                picker_goal_opened = false;
            } else {
                number_picker_goal_view.setVisibility(View.VISIBLE);
                picker_goal_opened = true;
            }

        }
    };

    View.OnClickListener weight_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (picker_weight_opened) {
                picker_weight_view.setVisibility(View.GONE);
                picker_weight_opened = false;
            } else {
                picker_weight_view.setVisibility(View.VISIBLE);
                picker_weight_opened = true;
            }

        }
    };

    View.OnClickListener age_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (picker_age_opened) {
                number_picker_age_view.setVisibility(View.GONE);
                picker_age_opened = false;
            } else {
                number_picker_age_view.setVisibility(View.VISIBLE);
                picker_age_opened = true;
            }

        }
    };

    View.OnClickListener sex_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (picker_sex_opened) {
                picker_sex_view.setVisibility(View.GONE);
                picker_sex_opened = false;
            } else {
                picker_sex_view.setVisibility(View.VISIBLE);
                picker_sex_opened = true;
            }

        }
    };

    View.OnClickListener act_lvl_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (picker_act_lvl_opened) {
                picker_act_lvl_view.setVisibility(View.GONE);
                picker_act_lvl_opened = false;
            } else {
                picker_act_lvl_view.setVisibility(View.VISIBLE);
                picker_act_lvl_opened = true;
            }

        }
    };

    private void getDataFromStorage() {

        age = AppUtils.age;
        drink_unit = drink_unit_arr[AppUtils.waterUn];
        w_unit = w_unit_arr[AppUtils.weightUn];
        sex = sex_arr[AppUtils.sex];
        act_lvl = act_lvl_arr[AppUtils.actLvl];
        isCustomGoal = !AppUtils.isCustomGoalOff;

        int water_min, water_max, weight_min, weight_max;

        if (AppUtils.weightUn == 0) { //lbs
            weight = AppUtils.kgToLbs(AppUtils.weightKg);

            weight_min = AppUtils.kgToLbs(20);
            weight_max = AppUtils.kgToLbs(300);

            weightSuffix = "lb";

        } else { //kg
            weight = AppUtils.weightKg;

            weight_min = 20;
            weight_max = 300;

            weightSuffix = "kg";
        }

        if (AppUtils.waterUn == 0) { //oz
            water_goal_total_old = AppUtils.mlToOz(AppUtils.waterLevelMl);

            water_min = 20;
            water_max = 100;

            waterSuffix = "oz";

        } else {  //ml
            water_goal_total_old = AppUtils.waterLevelMl;

            water_min = AppUtils.ozToMl(20);
            water_max = AppUtils.ozToMl(100);

            waterSuffix = "ml";
        }

        number_picker_goal.setMinValue(water_min);
        number_picker_goal.setMaxValue(water_max);

        picker_weight.setMinValue(weight_min);
        picker_weight.setMaxValue(weight_max);

//        Log.e("reali", "age " + age + " wat " + water_goal_total_old + drink_unit + " s " + sex + " " + act_lvl + " wei " + weight + w_unit + " isCust " + isCustomGoal);

        total_plus_final = water_goal_total_old;

        number_picker_goal.setValue(water_goal_total_old);
        number_picker_age.setValue(age);
        picker_weight.setValue(weight);

        tv_goal.setText(water_goal_total_old + " " + drink_unit_arr[AppUtils.waterUn]);
        tv_age.setText(age + "");
        tv_weight.setText(weight + " " + w_unit_arr[AppUtils.weightUn]);

        index_dr_un = findItemIndex(drink_unit, drink_unit_arr);
        index_w_un_cur = index_w_un = findItemIndex(w_unit, w_unit_arr);
        index_sex = findItemIndex(sex, sex_arr);
        index_act_lvl = findItemIndex(act_lvl, act_lvl_arr);

        picker_sex.setValue(index_sex);
        picker_act_lvl.setValue(index_act_lvl);

        tv_sex.setText(sex);
        tv_activity_lvl.setText(act_lvl);

        auto_goal.setChecked(!isCustomGoal);
        handle_goal_ui(!isCustomGoal);

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

        if (!auto_goal.isChecked()) {

            tv_goal.setText(total_plus_final + " " + waterSuffix);
            Log.e("reali", "total_plus_final " + total_plus_final);

        } else {

            total_plus_final = 0;

            /////////////////////////////////////////////////////////////////////////

            int weight_current_temp = picker_weight.getValue();
            int weight_current_kg;

            if (index_w_un_cur == 0) {  //if weight_view kg - ok, lbs - convert
                weight_current_kg = AppUtils.lbsToKg(weight_current_temp);
            } else {
                weight_current_kg = weight_current_temp;
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

            int index_sex_curr = picker_sex.getValue();
            int sex_plus = 0;

//            int sex_plus_percent_15 = water_goal_base_ml / 100 * 15;
////            Log.e("reali", "sex_plus_percent_15 " + sex_plus_percent_15);
//            if (index_sex_curr == 1) { //was FeMale - now Mmale
//                sex_plus += sex_plus_percent_15; // +15%
//            }

            /////////////////////////////////////////////////////////////////////////

            int index_act_lvl_curr = picker_act_lvl.getValue();
            int act_lvl_plus = 0;

            int act_lvl_percent_20 = water_goal_base_ml / 100 * 20;
            int act_lvl_percent_35 = water_goal_base_ml / 100 * 35;

//            Log.e("reali", "act_lvl_percent_20 " + act_lvl_percent_20);
//            Log.e("reali", "act_lvl_percent_35 " + act_lvl_percent_35);

            if (index_act_lvl_curr == 2) {
                act_lvl_plus += act_lvl_percent_20; //+20
            } else if (index_act_lvl_curr == 3) {
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

            tv_goal.setText(total_plus_final + " " + waterSuffix);

//            Log.e("reali", "total_plus_final "+total_plus_final);
        }

    }

    private void saveToStorage() {

        int water_goal_current = total_plus_final;

        int age_current = number_picker_age.getValue();

        int weight_current = picker_weight.getValue();

        int drink_unit_current_index = index_dr_un;
        int w_unit_current_index = index_w_un_cur;

        String sex_current = sex_arr[picker_sex.getValue()];
        String act_lvl_current = act_lvl_arr[picker_act_lvl.getValue()];
        boolean custom_goal_current = !auto_goal.isChecked();

        if (water_goal_total_old != water_goal_current) {

//            Log.e("reali", "water_goal_total_old != total_goal");

            if (drink_unit_current_index == 0) { //oz;
                AppUtils.waterLevelMl =  AppUtils.ozToMl(water_goal_current);
                AppUtils.setDayGoal(AppUtils.ozToMl(water_goal_current), new Date(), ChangeWaterGoalsNewActivity.this);
            } else {
                AppUtils.waterLevelMl =  water_goal_current;
                AppUtils.setDayGoal(water_goal_current, new Date(), ChangeWaterGoalsNewActivity.this);
            }

            NewMainActivity.updateWaterData = true;
        }

        if (age != age_current) {
            AppUtils.age = age_current;
        }

        if (weight != weight_current) {

            if (w_unit_current_index == 0) { //lbs
                AppUtils.weightKg = AppUtils.lbsToKg(weight_current);
            } else {
                AppUtils.weightKg = weight_current;
            }
        }

        if (!sex_current.equals(sex)) {
            AppUtils.sex = picker_sex.getValue();
        }

        if (!act_lvl_current.equals(act_lvl)) {
            AppUtils.actLvl = picker_act_lvl.getValue();
        }

        if (custom_goal_current != isCustomGoal) {
            AppUtils.isCustomGoalOff = !custom_goal_current;
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

        if (index_w_un_cur == 0) {  //if weight_view kg - ok, lbs - convert
            weight_kg = AppUtils.lbsToKg(weight_current);
        } else {
            weight_kg = weight_current;
        }

        ////////////////////////////////

        Goals goals = new Goals();
        goals.setWaterLevel(total_goal_ml);
        goals.setIsCustomGoalOff(!custom_goal_current);
        goals.setWeight(weight_kg);
        goals.setAge(age_current);
        goals.setSex(picker_sex.getValue());
        goals.setActivityLvl(picker_act_lvl.getValue());
        goals.setWaterUn(drink_unit_current_index);
        goals.setWeightUn(w_unit_current_index);

        ParseUser user = ParseUser.getCurrentUser();
        goals.put("userObject", user);

        goals.saveEventually();
        Log.e("reali", "Goals sent goal");

    }

}
