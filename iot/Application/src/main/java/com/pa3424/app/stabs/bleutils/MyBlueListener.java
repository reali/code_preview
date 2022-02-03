package com.pa3424.app.stabs1.bleutils;

/**
 * Created by ali on 8/22/19.
 */

public interface MyBlueListener {

    void onConnected();
    void onConnectionFailed();
    void onDisconnected();
    void onError();
    void onSerialMessageReceived(int curr, int consumed);

}
