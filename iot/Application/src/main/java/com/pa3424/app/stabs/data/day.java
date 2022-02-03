package com.pa3424.app.stabs1.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ali on 7/3/19.
 */
public class day {

    public int totalOunces;
    public int water_goal_ml;
    private Date date;
    public List<drink> list;

    public day(Date date) {
        this.date = date;

        list = new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }
}
