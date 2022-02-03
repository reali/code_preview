package com.pa3424.app.stabs1.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by ali on 2/19/19.
 */

@ParseClassName("Goals")
public class Goals extends ParseObject {

    public Goals() {

    }

    public void setWaterLevel(int waterLevel) {
        put("water_level_ml", waterLevel);
    }

    public void setIsCustomGoalOff(boolean customGoalOff) {
        put("custom_goal_off", customGoalOff);
    }

    public void setSex(int sex) {
        put("sex", sex);
    }

    public void setWeight(int weight) {
        put("weight_kg", weight);
    }

    public void setActivityLvl(int activityLvl) {
        put("activity_lvl", activityLvl);
    }

    public void setWeightUn(int weightUn) {
        put("weight_unit", weightUn);
    }

    public void setWaterUn(int waterUn) {
        put("water_unit", waterUn);
    }

    public void setAge(int age) {
        put("age", age);
    }




    public int getWaterLevel() {
        return getInt("water_level_ml");
    }

    public boolean isCustomGoalOff() {
        return getBoolean("custom_goal_off");
    }

    public int getSex() {
        return getInt("sex");
    }

    public int getWeight() {
        return getInt("weight_kg");
    }

    public int getActLvl() {
        return getInt("activity_lvl");
    }

    public int getWeightUn() {
        return getInt("weight_unit");
    }

    public int getWaterUn() {
        return getInt("water_unit");
    }

    public int getAge() {
        return getInt("age");
    }

}
