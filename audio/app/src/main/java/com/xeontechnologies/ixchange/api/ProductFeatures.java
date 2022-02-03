package com.zitech.audio.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductFeatures {

    @SerializedName("product_model")
    @Expose
    private String product_model;

    @SerializedName("alert_led")
    @Expose
    private int alert_led;

    @SerializedName("sound_preference")
    @Expose
    private int sound_preference;

    @SerializedName("never_forget_alert")
    @Expose
    private int never_forget_alert;

    @SerializedName("volume")
    @Expose
    private int volume;

    @SerializedName("voice_command")
    @Expose
    private int voice_command;

    @SerializedName("voice_prompt")
    @Expose
    private int voice_prompt;

    @SerializedName("find_the_headset")
    @Expose
    private int find_the_headset;

    @SerializedName("find_the_phone")
    @Expose
    private int find_the_phone;

    @SerializedName("battery_capacity")
    @Expose
    private int battery_capacity;

    @SerializedName("upgrade")
    @Expose
    private int upgrade;


    public String getProduct_model() {
        return product_model;
    }

    public int getAlert_led() {
        return alert_led;
    }

    public int getSound_preference() {
        return sound_preference;
    }

    public int getNever_forget_alert() {
        return never_forget_alert;
    }

    public int getVolume() {
        return volume;
    }

    public int getVoice_command() {
        return voice_command;
    }

    public int getVoice_prompt() {
        return voice_prompt;
    }

    public int getFind_the_headset() {
        return find_the_headset;
    }

    public int getFind_the_phone() {
        return find_the_phone;
    }

    public int getBattery_capacity() {
        return battery_capacity;
    }

    public int getUpgrade() {
        return upgrade;
    }
}
