package com.secretpackage.inspector.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ali on 7/1/19.
 */

public class Property2 extends GenericJson {

    public int prop_type;
    public boolean isSelected = false;

    @Key("Account_Number") private String Account_Number;
    @Key("Account_Type") private String Account_Type;
    @Key("Situs_Address") private String Situs_Address;
    @Key("Owner_Name") private String Owner_Name;
    @Key("Actual_Value") private String Actual_Value;
    @Key("property_type") private String property_type;
    @Key("Property_Code") private int Property_Code;
    @Key("Property_Code_Description") private String Property_Code_Description;
    @Key("Interior_Wall_Height") private String Interior_Wall_Height;
    @Key("Architectural_Style") private String Architectural_Style;
    @Key("Building_Use") private String Building_Use;
    @Key("Construction_Quality") private String Construction_Quality;
    @Key("Stories") private String Stories;
    @Key("StoryHeight") private String StoryHeight;
    @Key("Foundation") private String Foundation;
    @Key("Roof_Structure") private String Roof_Structure;
    @Key("Roof_Cover") private String Roof_Cover;
    @Key("Frame") private String Frame;
    @Key("Interior_Condition") private String Interior_Condition;
    @Key("Exterior_Condition") private String Exterior_Condition;
    @Key("Baths") private String Baths;
    @Key("Basement") private String Basement;
    @Key("Heating_Fuel") private String Heating_Fuel;
    @Key("Heating_Type") private String Heating_Type;
    @Key("Air_Conditioning") private String Air_Conditioning;
    @Key("Bedrooms") private String Bedrooms;
    @Key("Basement_Bedrooms") private String Basement_Bedrooms;
    @Key("Windows") private String Windows;

    @Key("notes") private String notes = "";
    @Key("status") private int status = 0;

    @Key("ext_wall") private List<String> ext_wall = new ArrayList<>();

    @Key("b_permits") private List<BuildingPermit> b_permits = new ArrayList<>();
    @Key("features") private List<Feature> features = new ArrayList<>();
    @Key("visits") private List<Visit> visits = new ArrayList<>();


    public String getAccount_Number() {
        return Account_Number;
    }

    public String getAccount_Type() {
        return Account_Type;
    }

    public String getOwner_Name() {
        return Owner_Name;
    }

    public String getActual_Value() {
        return Actual_Value;
    }

    public String getSitus_Address() {
        return Situs_Address;
    }

    public String getNotes() {
        return notes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProperty_type() {
        return property_type;
    }

    public void setProperty_type(String property_type) {
        this.property_type = property_type;
    }

    public int getProperty_Code() {
        return Property_Code;
    }

    public String getProperty_Code_Description() {
        return Property_Code_Description;
    }

    public String getInterior_Wall_Height() {
        return Interior_Wall_Height;
    }

    public String getArchitectural_Style() {
        return Architectural_Style;
    }

    public String getBuilding_Use() {
        return Building_Use;
    }

    public String getConstruction_Quality() {
        return Construction_Quality;
    }

    public String getStories() {
        return Stories;
    }

    public String getStoryHeight() {
        return StoryHeight;
    }

    public String getFoundation() {
        return Foundation;
    }

    public String getRoof_Structure() {
        return Roof_Structure;
    }

    public String getRoof_Cover() {
        return Roof_Cover;
    }

    public String getFrame() {
        return Frame;
    }

    public String getInterior_Condition() {
        return Interior_Condition;
    }

    public String getExterior_Condition() {
        return Exterior_Condition;
    }

    public String getBaths() {
        return Baths;
    }

    public String getBasement() {
        return Basement;
    }

    public String getHeating_Fuel() {
        return Heating_Fuel;
    }

    public String getHeating_Type() {
        return Heating_Type;
    }

    public String getAir_Conditioning() {
        return Air_Conditioning;
    }

    public String getBedrooms() {
        return Bedrooms;
    }

    public String getBasement_Bedrooms() {
        return Basement_Bedrooms;
    }

    public String getWindows() {
        return Windows;
    }

    public List<BuildingPermit> getB_permits() {
        return b_permits;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public void setB_permits(List<BuildingPermit> b_permits) {
        this.b_permits = b_permits;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public List<String> getExt_wall() {
        return ext_wall;
    }

    public void setExt_wall(List<String> ext_wall) {
        this.ext_wall = ext_wall;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }
}
