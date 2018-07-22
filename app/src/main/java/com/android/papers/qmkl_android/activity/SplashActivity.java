package com.android.papers.qmkl_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;


import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.model.AdData;
import com.android.papers.qmkl_android.requestModel.TokenLoginRequest;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.util.DownLoader;
import com.android.papers.qmkl_android.util.PermissionUtils;
import com.android.papers.qmkl_android.util.RetrofitUtils;

import com.android.papers.qmkl_android.util.SDCardUtils;

import java.io.File;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.android.papers.qmkl_android.util.SharedPreferencesUtils;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*
         *获取sd卡存储权限
         * 当第一次运行本程序时，由于权限还未获取，所以会直接跳过广告下载界面
         * 如果用户选择授予该权限，则下次启动时才会自主从网络下载广告资源
         * */
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE);

        RetrofitUtils.postLogin(getApplicationContext(),SharedPreferencesUtils.getStoredMessage(getApplication(),"token"));

        RetrofitUtils.postAd(this,SplashActivity.this);

    }


    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }
}