package com.secretpackage.inspector.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ali on 7/1/19.
 */

public class Property extends GenericJson {

    public int prop_type;
    public boolean isSelected = false;

    @Key("id") private int id;
    @Key("street_name") private String street_name;
    @Key("street_num") private int street_num;

    @Key("town") private String town;
    @Key("state") private String state;
    @Key("map_id") private String map_id;
    @Key("lot_id") private String lot_id;
    @Key("property_state") private String property_state;
    @Key("last_assessed_value") private int last_assessed_value;

    @Key("property_type") private String property_type;
    @Key("notes") private String notes;

    @Key("status") private int status = 0;

    @Key("owners") private List<Owner> owners = new ArrayList<>();
    @Key("b_permits") private List<BuildingPermit> b_permits = new ArrayList<>();
    @Key("features") private List<String> features = new ArrayList<>();
    @Key("utilities") private List<Occupancy> utilities = new ArrayList<>();
    @Key("visits") private List<Visit> visits = new ArrayList<>();

    @Key("occupancy") private Occupancy occupancy;
    @Key("location") private Occupancy location;
    @Key("topology") private Occupancy topology;
    @Key("cards") private Card card;


    public int getId() {
        return id;
    }

    public String getStreet_name() {
        return street_name;
    }

    public int getStreet_num() {
        return street_num;
    }

    public String getProperty_type() {
        return property_type;
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

    public void setProperty_type(String property_type) {
        this.property_type = property_type;
    }

    public String getTown() {
        return town;
    }

    public String getState() {
        return state;
    }

    public String getMap_id() {
        return map_id;
    }

    public String getLot_id() {
        return lot_id;
    }

    public String getProperty_state() {
        return property_state;
    }

    public int getLast_assessed_value() {
        return last_assessed_value;
    }

    public List<Owner> getOwners() {
        return owners;
    }

    public List<BuildingPermit> getB_permits() {
        return b_permits;
    }

    public List<String> getFeatures() {
        return features;
    }

    public List<Occupancy> getUtilities() {
        return utilities;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public Occupancy getOccupancy() {
        return occupancy;
    }

    public Occupancy getLocation() {
        return location;
    }

    public Occupancy getTopology() {
        return topology;
    }

    public Card getCard() {
        return card;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public void setStreet_num(int street_num) {
        this.street_num = street_num;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setMap_id(String map_id) {
        this.map_id = map_id;
    }

    public void setLot_id(String lot_id) {
        this.lot_id = lot_id;
    }

    public void setProperty_state(String property_state) {
        this.property_state = property_state;
    }

    public void setLast_assessed_value(int last_assessed_value) {
        this.last_assessed_value = last_assessed_value;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }

    public void setB_permits(List<BuildingPermit> b_permits) {
        this.b_permits = b_permits;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public void setUtilities(List<Occupancy> utilities) {
        this.utilities = utilities;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

    public void setOccupancy(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    public void setLocation(Occupancy location) {
        this.location = location;
    }

    public void setTopology(Occupancy topology) {
        this.topology = topology;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
