package com.secretpackage.inspector.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Created by ali on 9/24/20.
 */

public class NewValue extends GenericJson {

    public NewValue(String value, String showValue, boolean selected) {
        this.value = value;
        this.showValue = showValue;
        this.selected = selected;
    }

    public NewValue() {
    }

    @Key("val") public String value;
    @Key("showVal") public String showValue;
    @Key("selected") public boolean selected = false;

}
