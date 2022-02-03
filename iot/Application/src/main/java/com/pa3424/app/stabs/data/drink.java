package com.pa3424.app.stabs1.data;

import java.util.Date;

/**
 * Created by ali on 7/3/19.
 */
public class drink {

    private int ounces;
    private boolean isManual;
    private Date date;
    private String objId;
    private String tempId;

    public drink(int ounces, boolean isManual, Date date, String objId, String tempId) {
        this.ounces = ounces;
        this.isManual = isManual;
        this.date = date;
        this.objId = objId;
        this.tempId = tempId;
    }

    public int getOunces() {
        return ounces;
    }

    public Date getDate() {
        return date;
    }

    public boolean isManual() {
        return isManual;
    }

    public String getObjId() {
        return objId;
    }

    public String getTempId() {
        return tempId;
    }
}
