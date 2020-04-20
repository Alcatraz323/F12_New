package io.alcatraz.f12.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import io.alcatraz.f12.R;
import io.alcatraz.f12.extended.CompatWithPipeActivity;

public class InDebuggerActivity extends CompatWithPipeActivity {
    public static final String KEY_DEBUGGER_URL = "key_alc_debugger_url";

    Toolbar toolbar;
    WebView window_webview;

    LinearLayout in_debugger_load_layer;
    ProgressBar in_deugger_progress;

    LinearLayout err_layer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_debugger);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        initViews();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void findViews() {
        toolbar = findViewById(R.id.in_debugger_toolbar);
        window_webview = findViewById(R.id.in_debugger_webview);
        in_debugger_load_layer = findViewById(R.id.in_debugger_load_layer);
        in_deugger_progress = findViewById(R.id.in_debugger_load_progress);
        err_layer = findViewById(R.id.in_debugger_err_layer);

        WebChromeClient wcc = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                in_deugger_progress.setProgress(newProgress);
                if (newProgress == 100) {
                    in_debugger_load_layer.setVisibility(View.GONE);
                }
            }
        };

        WebViewClient wvc = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                view.loadUrl("about:blank");
                err_layer.setVisibility(View.VISIBLE);
            }
        };

        WebView.setWebContentsDebuggingEnabled(false);

        window_webview.getSettings().setJavaScriptEnabled(true);
        window_webview.getSettings().setSupportZoom(true);
        window_webview.getSettings().setDisplayZoomControls(false);
        window_webview.getSettings().setBuiltInZoomControls(true);
        window_webview.setWebChromeClient(wcc);
        window_webview.setWebViewClient(wvc);

        window_webview.getSettings().setUseWideViewPort(true);
        window_webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        window_webview.getSettings().setDomStorageEnabled(true);
        window_webview.getSettings().setDatabaseEnabled(true);
        window_webview.getSettings().setAppCacheEnabled(true);
    }

    private void initViews() {
        findViews();
        setSupportActionBar(toolbar);
        String url = getIntent().getStringExtra(KEY_DEBUGGER_URL);
        window_webview.loadUrl(url);
    }
}
