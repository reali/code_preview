package com.secretpackage.inspector.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ali on 8/8/19.
 */

public class PropChanges extends GenericJson {

    @Key("id") public int prop_id = 0;

    public PropChanges(int prop_id) {
        this.prop_id = prop_id;
    }

    public PropChanges() {
    }

    @Key("isOccuCodeChecked") public boolean isOccuCodeChecked = false;
    @Key("isTopolChecked") public boolean isTopolChecked = false;
    @Key("isOccuChecked") public boolean isOccuChecked = false;
    @Key("isLocChecked") public boolean isLocChecked = false;

    @Key("locVal") public String locVal = "";
    @Key("topolVal") public String topolVal = "";
    @Key("occuVal") public String occuVal = "";
    @Key("occuCodeVal") public String occuCodeVal = "";

    @Key("isKitchenChecked") public boolean isKitchenChecked = false;
    @Key("isBathStyleChecked") public boolean isBathStyleChecked = false;
    @Key("isCeilingTypeChecked") public boolean isCeilingTypeChecked = false;
    @Key("isDeprGradeChecked") public boolean isDeprGradeChecked = false;
    @Key("isBedroomChecked") public boolean isBedroomChecked = false;
    @Key("isBathroomChecked") public boolean isBathroomChecked = false;
    @Key("isHalfbathChecked") public boolean isHalfbathChecked = false;
    @Key("isOtherRoomChecked") public boolean isOtherRoomChecked = false;
    @Key("isFixturesChecked") public boolean isFixturesChecked = false;

    @Key("kitchenVal") public String kitchenVal = "";
    @Key("bathStyleVal") public String bathStyleVal = "";
    @Key("ceilingTypeVal") public String ceilingTypeVal = "";
    @Key("deprGradeVal") public String deprGradeVal = "";
    @Key("bedroomVal") public int bedroomVal = 0;
    @Key("bathroomVal") public int bathroomVal = 0;
    @Key("halfbathVal") public int halfbathVal = 0;
    @Key("otherRoomVal") public int otherRoomVal = 0;
    @Key("fixturesVal") public int fixturesVal = 0;

    @Key("isHeatTypeChecked") public boolean isHeatTypeChecked = false;
    @Key("isFuelChecked") public boolean isFuelChecked = false;
    @Key("isAcChecked") public boolean isAcChecked = false;

    @Key("heatVal") public String heatVal = "";
    @Key("fuelVal") public String fuelVal = "";
    @Key("acVal") public String acVal = "";

    @Key("isStyleChecked") public boolean isStyleChecked = false;
    @Key("isGradeChecked") public boolean isGradeChecked = false;
    @Key("isStoriesChecked") public boolean isStoriesChecked = false;
    @Key("isRoofCovTypeChecked") public boolean isRoofCovTypeChecked = false;
    @Key("isRoofStrChecked") public boolean isRoofStrChecked = false;

    @Key("styleVal") public String styleVal = "";
    @Key("gradeVal") public String gradeVal = "";
    @Key("storiesVal") public String storiesVal = "";
    @Key("roofCovVal") public String roofCovVal = "";
    @Key("roofStrVal") public String roofStrVal = "";

    @Key("b_perm") public List<PermitChanges> b_permits;

    @Key("ext_spec") public List<NewValue> ext_specNew;
    @Key("utilitiesList") public List<NewValue> utilitiesListNew;
    @Key("int_wall") public List<NewValue> int_wallNew;
    @Key("floors") public List<NewValue> floorsNew;
    @Key("ext_wall") public List<NewValue> ext_wallNew;



}
