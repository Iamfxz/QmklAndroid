package com.android.papers.qmkl_android.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.requestModel.TokenLoginRequest;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdsDetailsActivity extends Activity {

    private static final String TAG = "AdsDetailsActivity";
    private String adUrl;
    @BindView(R.id.ads_webview)
    WebView ads_webview;
    @BindView((R.id.xtfy_activity_back_iv))
    ImageView back_iv;
    @BindView(R.id.webview_title)
    TextView webview_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_ads);
        ButterKnife.bind(this);

//        adUrl = SharedPreferencesUtils.getStoredMessage(getApplicationContext(), "fallback");
        Log.d(TAG, "fallback="+SharedPreferencesUtils.getStoredMessage(getApplicationContext(), "fallback"));

        adUrl=getIntent().getStringExtra("url");
        webview_title.setText(getIntent().getStringExtra("title"));
        ads_webview.getSettings().setJavaScriptEnabled(true);
        ads_webview.getSettings().setDomStorageEnabled(true);
        ads_webview.loadUrl(adUrl);

        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedPreferencesUtils.getStoredMessage(getApplication(),"hasLogin").equals("false")){
                    nextActivity(LoginActivity.class);
                }
                else {
                    nextActivity(MainActivity.class);
                }
            }
        });

        /*网页*/
        ads_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http:") || url.startsWith("https:") ) {
                    Log.d(TAG, url);
                    view.loadUrl(url);
                    return false;
                }else{
                    Log.d(TAG, "1111111: ");
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }catch (ActivityNotFoundException e){
                        e.printStackTrace();
                        Log.d(TAG, "没有匹配");
                    }
                    return true;
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == keyEvent.KEYCODE_BACK) {//监听返回键，如果可以后退就后退
            if (ads_webview.canGoBack()) {
                ads_webview.goBack();
                return true;
            }
            else{
                if(SharedPreferencesUtils.getStoredMessage(getApplication(),"hasLogin").equals("false")){
                    nextActivity(LoginActivity.class);
                }
                else {
                    nextActivity(MainActivity.class);
                }
            }
        }

        return super.onKeyDown(keyCode, keyEvent);
    }



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
