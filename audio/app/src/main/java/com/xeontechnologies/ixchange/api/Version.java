package com.zitech.audio.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Version {

    @SerializedName("version")
    @Expose
    private String version;

    public String getVersion() {
        return version;
    }
}
