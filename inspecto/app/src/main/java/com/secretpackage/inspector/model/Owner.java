package com.secretpackage.inspector.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Created by ali on 7/1/19.
 */

public class Owner extends GenericJson {

    @Key("name") private String name;
    @Key("address") private String address;
    @Key("acquired_date") private String acquired_date;
    @Key("sold_date") private String sold_date;
    @Key("sale_price") private float sale_price;
    @Key("assessed_value") private float assessed_value;


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getAcquired_date() {
        return acquired_date;
    }

    public String getSold_date() {
        return sold_date;
    }

    public float getSale_price() {
        return sale_price;
    }

    public float getAssessed_value() {
        return assessed_value;
    }

}
