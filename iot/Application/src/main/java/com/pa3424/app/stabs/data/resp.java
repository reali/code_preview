package com.pa3424.app.stabs1.data;

/**
 * Created by ali on 4/29/19.
 */
public class resp {

    private String resp;
    private int code;

    public resp(String resp, int code) {
        this.resp = resp;
        this.code = code;
    }

    public String getResp() {
        return resp;
    }

    public int getCode() {
        return code;
    }
}
