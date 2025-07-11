package com.rovot.newsreader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class WebsiteActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private int progressStatus = 0;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);

        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra("url");
            if (url != null) {
                // Show progress bar initially
                progressBar.setVisibility(View.VISIBLE);

                // Simulate loading progress with a timer
                new Thread(() -> {
                    while (progressStatus < 5) {
                        progressStatus += 1;
                        handler.post(() -> progressBar.setProgress(progressStatus));
                        try {
                            Thread.sleep(5); // Adjust delay as needed
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // Load the URL after simulated progress
                    handler.post(() -> {
                        webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.loadUrl(url);
                    });
                }).start();
            }
        }
    }

    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}