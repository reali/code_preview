package com.secretpackage.inspector;

import android.app.Application;

import com.secretpackage.inspector.util.Constants;
import com.crashlytics.android.Crashlytics;
import com.kinvey.android.Client;
import io.fabric.sdk.android.Fabric;

/**
 * Created by ali on 6/23/19.
 */

public class MyApplication extends Application {

    private String TAG = "reali";
    private Client mKinveyClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mKinveyClient = new Client.Builder("kid_rJoFM39X-", "95591fc167894741a6ee5c12f4c4d472", this.getApplicationContext()).build(); //.setEncryptionKey(encryptionKey[])
        //mKinveyClient.setUseDeltaCache(true);
        mKinveyClient.enableDebugLogging();

//        mKinveyClient.ping(new KinveyPingCallback() {
//            public void onFailure(Throwable t) {
//                Log.e(TAG, "Kinvey Ping Failed", t);
//            }
//            public void onSuccess(Boolean b) {
//                Log.d(TAG, "Kinvey Ping Success");
//            }
//        });

        Constants.init();
    }

    public Client getKinveyClient(){
        return mKinveyClient;
    }
}
