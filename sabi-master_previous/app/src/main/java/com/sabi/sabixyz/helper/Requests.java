package com.sabi.sabixyz.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sabi.sabixyz.Constants;
import com.google.gson.Gson;


import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Requests {
    //    private static final MediaType JSON = MediaType.parse("application/json");

    private static final String BASE_URL = Constants.LOCAL_PATH+"api";
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

    public static void signup(Map<String, Object> params, Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/user_signup", BASE_URL), params), callback, context);
    }

    public static void save_book_for_later(Map<String, Object> params, Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/user_save_book_for_later", BASE_URL), params), callback, context);
    }
    public static void confirmSignup(Map<String, Object> params, Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/user_confirm_signup", BASE_URL), params), callback, context);
    }

    public static void updateUserDetails(Map<String, Object> params, Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/user_update_details", BASE_URL), params), callback, context);
    }

    public static void updateUserBooksTable(Map<String, Object> params, Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/update_user_books", BASE_URL), params), callback, context);
    }


    public static void changePassword(Map<String, Object> params, Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/change_password", BASE_URL), params), callback, context);
    }

    public static void sendConfirmationCode(Map<String, Object> params, Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/send_confirmation_code_to_email", BASE_URL), params), callback, context);
    }

    public static void submitConfirmationCode(Map<String, Object> params, Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/submit_confirmation_code", BASE_URL), params), callback, context);
    }

    public static void fetchRecyclerViewData(String Url, Response.Listener<String> successListener, Response.ErrorListener errorListener, Context context){
        StringRequest stringRequest = new StringRequest(Method.GET, String.format("%s/"+Url, BASE_URL), successListener, errorListener);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public static void fetchRecyclerViewDataNew(Map<String, Object> params,String url,  Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/"+url, BASE_URL), params), callback, context);
    }

    public static void beforeValidateTransaction(Map<String, Object> params, Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/beforeValidateTransaction", BASE_URL), params), callback, context);
    }

    public static void validateTransaction(Map<String, Object> params, Callback callback, Context context) {
        performRequest(makeSubmitRequest_noHeader(String.format("%s/validateTransaction", BASE_URL), params), callback, context);
    }

    public static void uploadImage(Map<String, Object> params, File filetoupload, String cookie, Callback callback, Context context) {
        performRequest(makeImageSubmitRequest_withHeader(String.format("%s/upload_image", BASE_URL), cookie, params, filetoupload), callback, context);
    }



    private static Request makeImageSubmitRequest_withHeader(String url, String cookie, Map<String, Object> params, File filetoupload) {
        if (!params.isEmpty()) {
            return new Request
                    .Builder()
                    .addHeader("Cookie", cookie)
                    .url(url)
                    .post(CreateImageFormToSubmit(params, filetoupload))
                    .build();
        }
        return null;
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


    private static RequestBody CreateImageFormToSubmit(Map<String, Object> params, File file) {
        Set<String> keys = params.keySet();
        MultipartBody.Builder f = new MultipartBody.Builder();
        f.setType(MultipartBody.FORM).addFormDataPart("ImageFile", file.getName(),
                RequestBody.create(MediaType.parse("image/jpeg"), file));

        for (String key : keys) {
            f.addFormDataPart(key, String.valueOf(params.get(key)));
            Log.d("key-value pair ==", key + " : " + params.get(key));
        }

        f.setType(MultipartBody.FORM);
        return f.build();
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