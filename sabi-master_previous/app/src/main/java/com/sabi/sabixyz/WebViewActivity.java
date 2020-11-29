package com.sabi.sabixyz;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends Activity {
private WebView mwebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if(!getIntent().getStringExtra("url").isEmpty()) {
            String url = getIntent().getStringExtra("url");
            mwebView = (WebView) findViewById(R.id.webview);
            mwebView.loadUrl(url);
        }
    }
}