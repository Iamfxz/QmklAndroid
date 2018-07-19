package com.android.papers.qmkl_android.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.requestModel.Request;
import com.android.papers.qmkl_android.util.PermissionUtils;
import com.android.papers.qmkl_android.util.RetrofitUtils;

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
//        测试登录
//        Request request=new Request("13157694909","f9e84102d063cf5887093255b7ad7bc64758975f");
//        RetrofitUtils.postLogin(this,request);


        RetrofitUtils.postAd(this);


        nextActivity(LoginActivity.class);
    }
    private void nextActivity(Class clazz) {
        final Intent intent = new Intent(SplashActivity.this, clazz);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                });

            }
        }).start();
    }

    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }
}