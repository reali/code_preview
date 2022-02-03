package com.pa3424.app.stabs1.api;

import com.pa3424.app.stabs1.utils.Log;

import com.pa3424.app.stabs1.data.resp;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ali on 4/27/19.
 */
public class RequestManager {

    private static final String API_URL = "https://api.fitbit.com";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    private String  doHttpGet(String url) throws IOException {

        Log.e("reali_pa3424", "httpGet " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        String resp = response.body().string();

        Log.e("reali_pa3424", "httpGet resp " + resp);

        return resp;
    }

    private resp doHttpGetResp(String url) throws IOException {

        Log.e("reali_pa3424", "httpGet " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        String resp = response.body().string();

        Log.e("reali_pa3424", "httpGet resp " + resp);

        return new resp(resp, response.code());
    }

    private resp doHttpGetResp(String url, String auth) throws IOException {

        Log.e("reali_pa3424", "httpGet " + url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + auth)
                .build();

        Response response = client.newCall(request).execute();

        String resp = response.body().string();
        int code = response.code();

        Log.e("reali_pa3424", "httpGet resp " + resp + " " + code);

        return new resp(resp, code);
    }

    private String doHttpGet(String url, String auth) throws IOException { //auth

        Log.e("reali_pa3424", "httpGet " + url + " auth " + auth);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + auth)
                .build();

        Response response = client.newCall(request).execute();

//        for (int i=0; i<response.headers().size(); i++) {
//            Log.e("reali_rama", response.headers().name(i) + " - " + response.headers().get(response.headers().name(i)));
//        }

        String resp = response.body().string();

        Log.e("reali_pa3424", "httpGet resp " + resp);

        return resp;
    }

    private String doHttpPost(String url, RequestBody formBody, String auth) throws IOException {

        Log.e("reali_pa3424", "httpPost " + url + " " + formBody.toString());

//        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + auth)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        String resp = response.body().string();

        Log.e("reali_pa3424", "httpPost resp " + resp);

        return resp;

    }

    private resp doHttpPostResp(String url, RequestBody formBody, String auth) throws IOException {

        Log.e("reali_pa3424", "httpPost " + url);

//        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + auth)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();

        String resp = response.body().string();
        int code = response.code();

        Log.e("reali_pa3424", "httpPostResp resp " + resp);

        return new resp(resp, code);

    }

    private resp doHttpPostJsonResp(String url, String json, String auth) throws IOException {

        Log.e("reali_pa3424", "httpPost " + url + " " + json.toString());

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + auth)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        String resp = response.body().string();
        int code = response.code();

        Log.e("reali_pa3424", "httpPostResp resp " + resp);

        return new resp(resp, code);

    }

    public resp getFitbitProfile(String user, String token) {

        try {
            return doHttpGetResp(API_URL + "/1/user/" + user + "/profile.json", token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public resp getFitbitWaterLevel(String user, String date, String token) {

        try {
            return doHttpGetResp(API_URL + "/1/user/" + user + "/foods/log/water/date/" + date + ".json", token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //https://api.fitbit.com/1/user/[user-id]/foods/log/water/[water-log-id].json
    public resp setFitbitWaterLevel(int waterLevel, String user, String date, String token) {
        RequestBody formBody = new FormBody.Builder()
                .add("amount", Integer.toString(waterLevel))
                .add("unit", "fl oz")
                .add("date", date) //yyyy-MM-dd
                .build();

//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("amount", waterLevel);
//            jsonObject.put("date", date);
//            jsonObject.put("unit", "fl oz");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        try {
            return doHttpPostResp(API_URL + "/1/user/" + user + "/foods/log/water.json", formBody, token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
