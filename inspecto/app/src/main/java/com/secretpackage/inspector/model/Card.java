package com.secretpackage.inspector.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ali on 7/1/19.
 */

public class Card extends GenericJson {

    @Key("download_date") private String download_date;
    @Key("style") private String style;
    @Key("style_commerc") private String style_commerc;
    @Key("grade") private String grade;
    @Key("stories") private String stories;
    @Key("occupancy_code") private String occupancy_code;
    @Key("occupancy_code_commerc") private String occupancy_code_commerc;
    @Key("occupancy") private String occupancy;
    @Key("num_units") private int num_units;
    @Key("wall_construction") private String wall_construction;
    @Key("wall_height") private String wall_height;

    @Key("ext_wall") private List<String> ext_wall = new ArrayList<>();
    @Key("int_wall") private List<String> int_wall = new ArrayList<>();
    @Key("int_floor") private List<String> int_floor = new ArrayList<>();

    @Key("roof_structure") private String roof_structure;
    @Key("roof_cover") private String roof_cover;

    @Key("ceiling_type_commerc") private String ceiling_type_commerc;
    @Key("heat_fuel") private String heat_fuel = "00";
    @Key("heat_type") private String heat_type;
    @Key("ac_type") private String ac_type;
    @Key("bedroom_count") private int bedroom_count;
    @Key("bath_count") private int bath_count;
    @Key("other_rooms_count") private int other_rooms_count;
    @Key("half_bath_count") private int half_bath_count;
    @Key("extra_fixtures") private int extra_fixtures;
    @Key("total_room_count") private int total_room_count;
    @Key("bath_style") private String bath_style;
    @Key("kitchen_style") private String kitchen_style;
    @Key("depreciation_code") private String depr_grade = "";

    public String getDownload_date() {
        return download_date;
    }

    public String getStyle() {
        return style;
    }

    public String getStyle_commerc() {
        return style_commerc;
    }

    public String getGrade() {
        return grade;
    }

    public String getStories() {
        return stories;
    }

    public String getOccupancy_code() {
        return occupancy_code;
    }

    public String getOccupancy_code_commerc() {
        return occupancy_code_commerc;
    }

    public String getOccupancy() {
        return occupancy;
    }

    public int getNum_units() {
        return num_units;
    }

    public String getWall_construction() {
        return wall_construction;
    }

    public String getWall_height() {
        return wall_height;
    }

    public String getRoof_structure() {
        return roof_structure;
    }

    public String getRoof_cover() {
        return roof_cover;
    }

    public List<String> getExt_wall() {
        return ext_wall;
    }

    public List<String> getInt_wall() {
        return int_wall;
    }

    public List<String> getInt_floor() {
        return int_floor;
    }

    public String getCeiling_type_commerc() {
        return ceiling_type_commerc;
    }

    public String getHeat_fuel() {
        return heat_fuel;
    }

    public String getHeat_type() {
        return heat_type;
    }

    public String getAc_type() {
        return ac_type;
    }

    public int getBedroom_count() {
        return bedroom_count;
    }

    public int getBath_count() {
        return bath_count;
    }

    public int getHalf_bath_count() {
        return half_bath_count;
    }

    public int getExtra_fixtures() {
        return extra_fixtures;
    }

    public int getOther_rooms_count() {
        return other_rooms_count;
    }

    public String getDepr_grade() {
        return depr_grade;
    }

    public int getTotal_room_count() {
        return total_room_count;
    }

    public String getBath_style() {
        return bath_style;
    }

    public String getKitchen_style() {
        return kitchen_style;
    }

    public void setOccupancy_code(String occupancy_code) {
        this.occupancy_code = occupancy_code;
    }

    public void setDownload_date(String download_date) {
        this.download_date = download_date;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setStyle_commerc(String style_commerc) {
        this.style_commerc = style_commerc;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setStories(String stories) {
        this.stories = stories;
    }

    public void setOccupancy_code_commerc(String occupancy_code_commerc) {
        this.occupancy_code_commerc = occupancy_code_commerc;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
    }

    public void setNum_units(int num_units) {
        this.num_units = num_units;
    }

    public void setWall_construction(String wall_construction) {
        this.wall_construction = wall_construction;
    }

    public void setWall_height(String wall_height) {
        this.wall_height = wall_height;
    }

    public void setExt_wall(List<String> ext_wall) {
        this.ext_wall = ext_wall;
    }

    public void setInt_wall(List<String> int_wall) {
        this.int_wall = int_wall;
    }

    public void setInt_floor(List<String> int_floor) {
        this.int_floor = int_floor;
    }

    public void setRoof_structure(String roof_structure) {
        this.roof_structure = roof_structure;
    }

    public void setRoof_cover(String roof_cover) {
        this.roof_cover = roof_cover;
    }

    public void setCeiling_type_commerc(String ceiling_type_commerc) {
        this.ceiling_type_commerc = ceiling_type_commerc;
    }

    public void setHeat_fuel(String heat_fuel) {
        this.heat_fuel = heat_fuel;
    }

    public void setHeat_type(String heat_type) {
        this.heat_type = heat_type;
    }

    public void setAc_type(String ac_type) {
        this.ac_type = ac_type;
    }

    public void setBedroom_count(int bedroom_count) {
        this.bedroom_count = bedroom_count;
    }

    public void setBath_count(int bath_count) {
        this.bath_count = bath_count;
    }

    public void setOther_rooms_count(int other_rooms_count) {
        this.other_rooms_count = other_rooms_count;
    }

    public void setHalf_bath_count(int half_bath_count) {
        this.half_bath_count = half_bath_count;
    }

    public void setExtra_fixtures(int extra_fixtures) {
        this.extra_fixtures = extra_fixtures;
    }

    public void setTotal_room_count(int total_room_count) {
        this.total_room_count = total_room_count;
    }

    public void setBath_style(String bath_style) {
        this.bath_style = bath_style;
    }

    public void setKitchen_style(String kitchen_style) {
        this.kitchen_style = kitchen_style;
    }

    public void setDepr_grade(String depr_grade) {
        this.depr_grade = depr_grade;
    }
}
