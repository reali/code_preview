package com.secretpackage.inspector.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Created by ali on 9/26/20.
 */

public class PermitChanges extends GenericJson {

    @Key("b_permit") public BuildingPermit permit;
    @Key("selected") public boolean selected = false;
    @Key("updated") public boolean isUpdated = false;

    public PermitChanges(BuildingPermit permit) {
        this.permit = permit;
        selected = false;
        isUpdated = false;
    }

    public PermitChanges() {
    }
}
