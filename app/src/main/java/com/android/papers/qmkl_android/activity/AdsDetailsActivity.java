package com.android.papers.qmkl_android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.util.ActivityManager;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdsDetailsActivity extends AppCompatActivity {

    private static final String TAG = "AdsDetailsActivity";
    @BindView(R.id.ads_webView)//网页视图
            WebView ads_webView;
    @BindView(R.id.activity_back_iv)//后退按钮视图
    ImageView back_iv;
    @BindView(R.id.webView_title)//网页标题
    TextView webView_title;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        setContentView(R.layout.web_ads);
        ButterKnife.bind(this);

        //TODO 干嘛用的？
        Log.d(TAG, "fallback=" + SharedPreferencesUtils.getStoredMessage(getApplicationContext(), "fallback"));

        //获取广告指向链接的URL
        String adUrl = getIntent().getStringExtra("url");
        webView_title.setText(getIntent().getStringExtra("title"));
        ads_webView.getSettings().setJavaScriptEnabled(true);
        ads_webView.getSettings().setDomStorageEnabled(true);
        ads_webView.loadUrl(adUrl);

        //返回按钮的事件处理 TODO 好像没起到效果
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //设置网页客户端
        ads_webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    Log.d(TAG, "广告网页的URL" + url);
                    view.loadUrl(url);
                    return false;
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        Log.d(TAG, "广告网页没有匹配");
                    }
                    return true;
                }

            }
        });
    }

    /**
     *      返回按键处理
     * @param keyCode 按键编码
     * @param keyEvent 按键事件
     * @return 是否已经处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//监听返回键，如果可以后退就后退
            if (ads_webView.canGoBack()) {
                ads_webView.goBack();
                return true;
            } else {
                if (SharedPreferencesUtils.getStoredMessage(getApplication(), "hasLogin").equals("false")) {
                    nextActivity(LoginActivity.class);
                } else {
                    nextActivity(MainActivity.class);
                }
            }
        }

        return super.onKeyDown(keyCode, keyEvent);
    }

    /**
     *      进入下一个Activity
     * @param clazz 活动类名
     */
    public void nextActivity(Class clazz) {
        final Intent intent = new Intent(AdsDetailsActivity.this, clazz);
        startActivity(intent);
        finish();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

}
