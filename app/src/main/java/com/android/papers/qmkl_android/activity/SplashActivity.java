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

    private static final int errorCode=404;
    private static final int successCode = 200;
    private static final String TAG = "SplashActivity";
    private String oldAdName,newAdName,adPath;

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

        postLogin(getApplication(),SharedPreferencesUtils.getStoredMessage(getApplication(),"token"));

        Call<ResponseInfo<AdData>> call=RetrofitUtils.postAd(this);
        //发送网络请求(异步)
        call.enqueue(new Callback<ResponseInfo<AdData>>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<ResponseInfo<AdData>> call, Response<ResponseInfo<AdData>> response) {
                //广告页当前不可用
                if (Integer.parseInt(Objects.requireNonNull(response.body()).getCode())!=successCode||
                        !Objects.requireNonNull(response.body()).getData().isEnabled()) {
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
                                    if(SharedPreferencesUtils.getStoredMessage(getApplication(),"hasLogin").equals("false")){
                                        nextActivity(LoginActivity.class);
                                    }
                                    else {
                                        nextActivity(MainActivity.class);
                                    }
                                }
                            });
                        }
                    }).start();
                    Log.d(TAG, "广告不可用");
                    return;
                }
                //广告页当前可用
                oldAdName = SharedPreferencesUtils.getStoredMessage(getApplicationContext(), "AdName");
                newAdName = response.body().getData().getUpdatedAt();
                adPath = response.body().getData().getUrl();
                SharedPreferencesUtils.setStoredMessage(getApplicationContext(),"fallback",
                        response.body().getData().getFallback());
                //此前尚未缓存过广告数据、广告数据已更新、广告数据被删除，重新缓存
                if (oldAdName == null || !oldAdName.equals(newAdName) || !checkLocalADImage()) {
                    SharedPreferencesUtils.setStoredMessage(getApplicationContext(), "AdName", newAdName);
                    Log.d(TAG, "此前尚未缓存过广告数据或者广告数据已更新，重新缓存");
                    //下载广告页
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d(TAG, "原广告名："+oldAdName);
                                Log.d(TAG, "现广告名："+newAdName);
                                Log.d(TAG, "广告路径："+adPath);
                                Log.d(TAG, "文件路径："+SDCardUtils.getADImage(newAdName));
                                DownLoader.downloadFile(new File(SDCardUtils.getADImage(newAdName)),
                                        adPath);
                                //缓存成功，进入广告页
                                nextActivity(AdsActivity.class);
                            } catch (Exception e) {
                                //缓存失败，进入登录界面或者主界面
                                Toast.makeText(getApplicationContext(),"缓存广告失败,请反馈给开发者",Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                //广告页已准备就绪，进入广告页
                else{
                    nextActivity(AdsActivity.class);
                }


            }

            @Override
            public void onFailure(Call<ResponseInfo<AdData>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"服务器请求失败",Toast.LENGTH_SHORT).show();
            }
        });
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
     * 获取远程信息失败或者广告版本为最新时, 检查本地广告图片是否存在
     *
     */
    private boolean checkLocalADImage() {
        Log.d(TAG, "检测本地广告图像是否存在");
        File adImageFile = new File(SDCardUtils.getADImage(newAdName));
        return adImageFile.exists();
    }

    //判断当前token是否可以登录
    public void postLogin(final Context context, String token){
        if(token!=null){
            //创建Retrofit对象
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.base_url))// 设置 网络请求 Url,0.0.4版本
                    .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                    .build();

            //创建 网络请求接口 的实例
            PostLogin request = retrofit.create(PostLogin.class);

            //对 发送请求 进行封装(账号和密码)
            Call<ResponseInfo> call = request.getTokenCall(new TokenLoginRequest(token));

            //发送网络请求(异步)
            call.enqueue(new Callback<ResponseInfo>() {
                //请求成功时回调
                @Override
                public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                    int resultCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                    System.out.println(resultCode);
                    if(resultCode == errorCode){
                        SharedPreferencesUtils.setStoredMessage(getApplicationContext(),"hasLogin","false");
                    }else if (resultCode == successCode){
                        SharedPreferencesUtils.setStoredMessage(getApplicationContext(),"token",response.body().getData().toString());
                        SharedPreferencesUtils.setStoredMessage(getApplicationContext(),"hasLogin","true");
                    }else{
                        SharedPreferencesUtils.setStoredMessage(getApplicationContext(),"hasLogin","false");
                    }
                }
                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                    Log.d(TAG, "请求失败");
                    SharedPreferencesUtils.setStoredMessage(getApplicationContext(),"hasLogin","false");
                }
            });
        }
        else{
            SharedPreferencesUtils.setStoredMessage(getApplicationContext(),"hasLogin","false");
        }
    }


    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }
}