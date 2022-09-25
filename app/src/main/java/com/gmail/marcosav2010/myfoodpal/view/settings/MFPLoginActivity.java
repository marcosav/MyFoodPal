package com.gmail.marcosav2010.myfoodpal.view.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gmail.marcosav2010.myfoodpal.R;

public class MFPLoginActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mfp_login);

        WebView webView = findViewById(R.id.web_view);

        var url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new MyWebViewClient());
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onLoadResource(WebView view, String url) {
            if (url.contains("/api/auth/callback/credentials")) {
                String cookies = CookieManager.getInstance().getCookie(url);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("cookies", cookies);
                setResult(Activity.RESULT_OK, returnIntent);

                finish();
            }
        }
    }
}