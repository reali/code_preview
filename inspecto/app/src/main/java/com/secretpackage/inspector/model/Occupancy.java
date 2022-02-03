package com.secretpackage.inspector.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Created by ali on 7/1/19.
 */

public class Occupancy extends GenericJson {

    @Key("download_date") private String download_date;
    @Key("code_old") private String code;

    public String getDownload_date() {
        return download_date;
    }

    public String getCode() {
        return code;
    }

    public Occupancy(String code) {
        this.code = code;
    }

    public Occupancy() {

    }
}
