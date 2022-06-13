package com.example.utehystudent.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.utehystudent.R;

public class TinTucKhoaActivity extends AppCompatActivity {
    Toolbar toolbar;
    WebView webView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cong_thong_tin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Đang tải dữ liệu...");

        toolbar = findViewById(R.id.CongTT_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("TIN TỨC");
        toolbar.setNavigationOnClickListener(v -> finish());
        //
        webView = findViewById(R.id.CongTT_webView);

        webView.getSettings().setAppCacheEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.getSettings().setSafeBrowsingEnabled(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        //String url = "http://qldaotao.utehy.edu.vn:81/DangNhap/Login";
        String url = "https://fit.utehy.edu.vn/tin-tuc/";
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.getSettings().setJavaScriptEnabled(true);
        }

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressDialog.show();
                if (newProgress == 100) {
                    progressDialog.dismiss();
                    setTitle(view.getTitle());
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }

        super.onBackPressed();
    }
}