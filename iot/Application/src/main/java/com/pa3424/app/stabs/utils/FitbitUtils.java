package com.pa3424.app.stabs1.utils;

import android.app.Activity;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ali on 10/14/20.
 */

public class FitbitUtils {

    public static void fitbitLogin(Activity ctx) {
        String redirect_uri = "____________";

        try {
            redirect_uri = URLEncoder.encode(redirect_uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "https://www.fitbit.com/oauth2/authorize?response_type=token&" +
                "client_id=227";
                //            Log.e("reali", "url " + url);

//            Bundle headers = new Bundle();
//            headers.putString("Authorization", "Basic " + "Y2xpZW50X2lkOmNsaWVudCBzZWNyZXQ=");

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
//            customTabsIntent.intent.putExtra(Browser.EXTRA_HEADERS, headers);
        customTabsIntent.launchUrl(ctx, Uri.parse(url));
    }

}
