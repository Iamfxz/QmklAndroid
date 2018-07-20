package com.android.papers.qmkl_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;


import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.model.AdData;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.LoginRequest;
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
    private boolean isLogin =false;
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
                if (!response.body().getData().isEnabled()||
                        Integer.parseInt(response.body().getCode())!=successCode) {
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
                                    nextActivity(LoginActivity.class);   //不显示广告,跳入mainactivity
                                }
                            });
                        }
                    }).start();
                    Log.d(TAG, "广告不可用");
                    return;
                }
                //广告页当前可用
                oldAdName = SharedPreferencesUtils.getStoredMessage(getBaseContext(), "AdName");
                newAdName = response.body().getData().getUpdatedAt();
                adPath = response.body().getData().getUrl();
                //此前尚未缓存过广告数据、广告数据已更新、广告数据被删除，重新缓存
                if (oldAdName == null || !oldAdName.equals(newAdName) || !checkLocalADImage()) {
                    SharedPreferencesUtils.setStoredMessage(getBaseContext(), "AdName", newAdName);
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
                                e.printStackTrace();
                                nextActivity(next());
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
                Log.d("123失败", "失败");
            }
        });
    }

    /**
     * 判断当前用户信息是否存在或失效，存在且有效进入主界面，失效则进入登录界面
     * @return 下一活动的Class
     */
    public Class next(){
        String token=SharedPreferencesUtils.getStoredMessage(getBaseContext(), "token");
        if(token!=null){
            //token合法，返回主界面
            if(postLogin(getBaseContext(),token)){
                //返回主界面
            }
            else{
                return LoginActivity.class;
            }
        }
        return LoginActivity.class;
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
    private boolean checkLocalADImage() {

        Log.d(TAG, "检测本地广告图像是否存在");

        File adImageFile = new File(SDCardUtils.getADImage(newAdName));
        return adImageFile.exists();
//        Class clazz = adImageFile.exists() ? AdsActivity.class : LoginActivity.class;    //本地图片不存在, 跳转至mainactivity
//        nextActivity(clazz);
//        if(adImageFile.exists())Log.d(TAG, "本地广告图像存在");
//        else Log.d(TAG, "本地广告图像不存在");
    }

    //判断当前token是否可以登录
    public boolean postLogin(final Context context, String token){

        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))// 设置 网络请求 Url,0.0.4版本
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        //创建 网络请求接口 的实例
        PostLogin request = retrofit.create(PostLogin.class);

        //对 发送请求 进行封装(账号和密码)
        Call<ResponseInfo> call = request.getTokenCall(token);

        //发送网络请求(异步)
        call.enqueue(new Callback<ResponseInfo>() {
            //请求成功时回调
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                int resultCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                System.out.println(resultCode);
                if(resultCode == errorCode){
                    isLogin =false;
                }else if (resultCode == successCode){
                    isLogin =true;
                }else{
                    isLogin =false;
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                isLogin =false;
            }
        });

        return isLogin;
    }


    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }
}