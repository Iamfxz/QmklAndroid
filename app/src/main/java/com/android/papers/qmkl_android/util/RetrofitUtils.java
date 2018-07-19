package com.android.papers.qmkl_android.util;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.activity.LoginActivity;
import com.android.papers.qmkl_android.impl.PostAds;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.model.AdData;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitUtils {

    //登录调用
    public static void postLogin(Context context, LoginRequest r){
        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))// 设置 网络请求 Url,0.0.4版本
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        //创建 网络请求接口 的实例
        PostLogin request = retrofit.create(PostLogin.class);

        //对 发送请求 进行封装(账号和密码)
        Call<ResponseInfo> call = request.getCall(r);

        //发送网络请求(异步)
        call.enqueue(new Callback<ResponseInfo>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {

                Log.d("123成功","登录token"+response.body().getData());

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
                Log.d("123失败","失败");
            }
        });
    }

    //获取广告
    public static Call postAd(final Context context){
        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))// 设置 网络请求 Url,0.0.4版本
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        //创建 网络请求接口 的实例
        PostAds request = retrofit.create(PostAds.class);

        //对 发送请求 进行封装(广告地址)
        Call<ResponseInfo<AdData>> call = request.getCall();

        return call;

    }
}
