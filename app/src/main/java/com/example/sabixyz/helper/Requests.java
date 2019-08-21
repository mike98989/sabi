package com.example.sabixyz.helper;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;


import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Requests {
    //    private static final MediaType JSON = MediaType.parse("application/json");
    private static final String BASE_URL = "http://172.20.10.3/sabi/api";
    private static final String TEST_URL = "https://simplifiedcoding.net/demos/marvel/";
    //private static final String BASE_URL = "http://api.pullova.com";
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final CookieStore cookieStore = new CookieStore();

    private static void performRequest(Request request, Callback callback, Context context) {

        OkHttpClient.Builder okHttpClient = new OkHttpClient
                .Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS);
                //.cookieJar(cookieStore);

        okHttpClient
                .build()
                .newCall(request)
                .enqueue(callback);
    }


    public static void login(Map<String, Object> params, Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/user_login", BASE_URL), params), callback, context);
    }


    public static void fetchRecyclerViewData(Response.Listener<String> successListener, Response.ErrorListener errorListener, Context context){
        StringRequest stringRequest = new StringRequest(Method.GET, TEST_URL, successListener, errorListener);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    private static Request makeSubmitRequest_noHeader(String url, Map<String, Object> params) {
        if (!params.isEmpty()) {
            return new Request
                    .Builder()
                    .url(url)
                    .post(CreateFormToSubmit(params))
                    .build();
        }
        return null;
    }

    private static Request makeSubmitJsonRequest_noHeader(String url, Map<String, Object> params) {

        if (!params.isEmpty()) {
            String json = new Gson().toJson(params);
            Log.d("Json to sent: ", json);
            System.out.println("URL: " + url);
            RequestBody body = RequestBody.create(JSON, json);
            if (CookieStore.validCookies.isEmpty()) {
                return new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
            } else
                return new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Cookie", String.valueOf(CookieStore.validCookies.get(0)))
                        .build();
        }
        return null;
    }




    private static RequestBody CreateFormToSubmit(Map<String, Object> params) {
        Set<String> keys = params.keySet();
        MultipartBody.Builder f = new MultipartBody.Builder();

        for (String key : keys) {
            f.addFormDataPart(key, String.valueOf(params.get(key)));
            Log.d("key-value pair ==", key + " : " + params.get(key));
            //System.out.println("key-value pair == " + key + " : " + params.get(key));
        }

        f.setType(MultipartBody.FORM);
        return f.build();
    }


}
