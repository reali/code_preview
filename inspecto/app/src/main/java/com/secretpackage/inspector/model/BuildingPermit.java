package com.secretpackage.inspector.model;

import com.secretpackage.inspector.util.DateUtils;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.Date;

/**
 * Created by ali on 7/1/19.
 */

public class BuildingPermit extends GenericJson implements Comparable<BuildingPermit> {

    @Key("issue_date") private String issue_date;
    @Key("completion_date") private String compl_date;
    @Key("inspection_date") private String insp_date;
    @Key("percent_complete") private int percentage = 0;
    @Key("permit_type") private String permit_type;
    @Key("description") private String description;
    @Key("amount") private float amount;
    @Key("comments") private String comments;

    public String getIssue_date() {
        return issue_date;
    }

    public String getPermit_type() {
        return permit_type;
    }

    public String getDescription() {
        return description;
    }

    public float getAmount() {
        return amount;
    }

    public String getComments() {
        return comments;
    }

    public String getCompl_date() {
        return compl_date;
    }

    public String getInsp_date() {
        return insp_date;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setCompl_date(String compl_date) {
        this.compl_date = compl_date;
    }

    public void setInsp_date(String insp_date) {
        this.insp_date = insp_date;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    @Override
    public int compareTo(BuildingPermit obj) {

        Date mDate = DateUtils.fromRawStringToDate(issue_date);
        Date oDate = DateUtils.fromRawStringToDate(obj.getIssue_date());

        return mDate.compareTo(oDate);
    }
}
