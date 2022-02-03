package com.secretpackage.inspector.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Created by ali on 7/1/19.
 */

public class Feature extends GenericJson {

    @Key("download_date") private String download_date;
    @Key("line_no") private int line_no;
    @Key("feature_old") private String feature_old;

    public String getDownload_date() {
        return download_date;
    }

    public int getLine_no() {
        return line_no;
    }

    public String getFeature_old() {
        return feature_old;
    }
}
