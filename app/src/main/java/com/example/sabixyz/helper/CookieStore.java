package com.example.sabixyz.helper;


import android.content.SharedPreferences;

import com.example.sabixyz.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import static android.content.Context.MODE_PRIVATE;

public class CookieStore implements CookieJar {
    private final Set<Cookie> cookieStore = new HashSet<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        //Save cookies to the store
        cookieStore.addAll(cookies);
    }

    public static List<Cookie> validCookies = new ArrayList<>();

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        for (Cookie cookie : cookieStore) {
            LogCookie(cookie);
            if (cookie.expiresAt() < System.currentTimeMillis()) {
                // invalid cookie(s)
            } else {
                validCookies.add(cookie);
            }
        }
        return validCookies;
    }

    private void LogCookie(Cookie cookie) {
        System.out.println("String: " + cookie.toString());
        System.out.println("Expires: " + cookie.expiresAt());
        System.out.println("Hash: " + cookie.hashCode());
        System.out.println("Path: " + cookie.path());
        System.out.println("Domain: " + cookie.domain());
        System.out.println("Name: " + cookie.name());
        System.out.println("Value: " + cookie.value());
    }
}
