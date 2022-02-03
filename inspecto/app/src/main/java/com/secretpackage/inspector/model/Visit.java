package com.secretpackage.inspector.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Created by ali on 7/1/19.
 */

public class Visit extends GenericJson {

    @Key("visit_date") private String visit_date;
    @Key("inspector") private String inspector;
    @Key("visit_code") private String visit_code;

    public String getVisit_date() {
        return visit_date;
    }

    public String getInspector() {
        return inspector;
    }

    public String getVisit_code() {
        return visit_code;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public void setVisit_code(String visit_code) {
        this.visit_code = visit_code;
    }
}
