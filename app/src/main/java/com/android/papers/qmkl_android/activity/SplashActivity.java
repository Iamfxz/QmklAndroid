package com.android.papers.qmkl_android.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.model.AdData;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.util.PermissionUtils;
import com.android.papers.qmkl_android.util.RetrofitUtils;
import com.android.papers.qmkl_android.util.SDCardUtils;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


        Call<ResponseInfo<AdData>> call=RetrofitUtils.postAd(this);
        //发送网络请求(异步)
        call.enqueue(new Callback<ResponseInfo<AdData>>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<ResponseInfo<AdData>> call, Response<ResponseInfo<AdData>> response) {
                //广告页当前不可用
                if(response.body().getData().isEnabled()){
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(2000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    nextActivity(LoginActivity.class);   //不显示广告,跳入mainactivity
//                                }
//                            });
//                        }
//                    }).start();
//                    Log.d(TAG, "已缓存");
//                    return;
                }
                //广告页当前可用


            }
            //请求失败时回调
            @Override
            public void onFailure(Call<ResponseInfo<AdData>> call, Throwable t) {
                Log.d("123失败","失败");
            }
        });

        nextActivity(LoginActivity.class);
    }
    public void nextActivity(Class clazz) {
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

    /**
     *
     * 获取远程信息失败或者广告版本为最新时, 检查本地广告图片是否存在,若存在则显示广告页,不存在跳至主页
     *
     */
    private void checkLocalADImage() {

        Log.d(TAG, "检测本地广告图像是否存在");

        File adImageFile = new File(SDCardUtils.getADImage());
        Class clazz = adImageFile.exists() ? AdsActivity.class : LoginActivity.class;    //本地图片不存在, 跳转至mainactivity
        nextActivity(clazz);
        if(adImageFile.exists())Log.d(TAG, "本地广告图像存在");
        else Log.d(TAG, "本地广告图像不存在");
    }

    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }
}