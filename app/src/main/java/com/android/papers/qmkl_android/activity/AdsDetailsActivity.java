package com.android.papers.qmkl_android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdsDetailsActivity extends Activity {

    String adUrl;
    @BindView(R.id.ads_webview)
    WebView ads_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_ads);
        ButterKnife.bind(this);

        adUrl = SharedPreferencesUtils.getStoredMessage(getBaseContext(), "fallback");

        ads_webview.getSettings().setJavaScriptEnabled(true);
        ads_webview.loadUrl(adUrl);
        /*网页*/
        ads_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdsDetailsActivity.this,LoginActivity.class));
        finish();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }
}
