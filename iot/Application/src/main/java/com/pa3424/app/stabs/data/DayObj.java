package com.pa3424.app.stabs1.data;

import java.util.Date;

/**
 * Created by ali on 5/20/20.
 */

public class DayObj {

    private Date date;
    private int percent;

    public DayObj(Date date, int percent) {
        this.date = date;
        this.percent = percent;
    }

    public Date getDate() {
        return date;
    }

    public int getPercent() {
        return percent;
    }

}
