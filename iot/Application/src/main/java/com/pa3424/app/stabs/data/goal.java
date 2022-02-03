package com.pa3424.app.stabs1.data;

import java.util.Date;

/**
 * Created by ali on 2/19/19.
 */
public class goal {

    private int water_goal;
    private Date date;

    public goal(Date date, int water_goal) {
        this.date = date;
        this.water_goal = water_goal;
    }

    public int getWater_goal() {
        return water_goal;
    }
    public Date getDate() {
        return date;
    }
}
