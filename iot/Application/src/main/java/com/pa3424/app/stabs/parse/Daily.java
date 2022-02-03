package com.pa3424.app.stabs1.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Daily")
public class Daily extends ParseObject {

    public Daily() {
        // A default constructor is required.
    }

    public int getWaterLevel() {
        return getInt("waterConsumed");
    }

    public String getEventName() {
        return getString("UserObjectId");
    }

}