package com.android.papers.qmkl_android.util;


import android.util.Log;

<<<<<<< HEAD
import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.impl.PostAds;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.model.AdData;
import com.android.papers.qmkl_android.requestModel.Request;
=======
import com.android.papers.qmkl_android.RequestInfo.LoginReq;
import com.android.papers.qmkl_android.impl.PostLogin;
>>>>>>> eebb50d47daf62cb45fe88bbfd810bd36447116e
import com.android.papers.qmkl_android.model.ResponseInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitUtils {
<<<<<<< HEAD

    //登录调用
    public static void postLogin(Context context,Request r){
=======
    public static void postLogin(LoginReq r){
>>>>>>> eebb50d47daf62cb45fe88bbfd810bd36447116e
        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
<<<<<<< HEAD
                .baseUrl(context.getString(R.string.base_url))// 设置 网络请求 Url,0.0.4版本
=======
                .baseUrl("http://120.77.32.233/qmkl0.0.6/")// 设置 网络请求 Url,0.0.4版本
>>>>>>> eebb50d47daf62cb45fe88bbfd810bd36447116e
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        //创建 网络请求接口 的实例
        PostLogin request = retrofit.create(PostLogin.class);

        //对 发送请求 进行封装(账号和密码)
<<<<<<< HEAD
        Call<ResponseInfo> call = request.getCall(r);
=======
        LoginReq req = new LoginReq("13157694909","f9e84102d063cf5887093255b7ad7bc64758975f");
        Call<ResponseInfo> call = request.getCall(req);
>>>>>>> eebb50d47daf62cb45fe88bbfd810bd36447116e

        //发送网络请求(异步)
        call.enqueue(new Callback<ResponseInfo>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
<<<<<<< HEAD

                Log.d("123成功","登录token"+response.body().getData());
=======
                Log.d("123成功",response.body().getMsg());
>>>>>>> eebb50d47daf62cb45fe88bbfd810bd36447116e
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
                Log.d("123失败","失败");
            }
        });
    }

    //获取广告
    public static void postAd(Context context){
        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))// 设置 网络请求 Url,0.0.4版本
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        //创建 网络请求接口 的实例
        PostAds request = retrofit.create(PostAds.class);

        //对 发送请求 进行封装(广告地址)
        Call<ResponseInfo<AdData>> call = request.getCall();

        //发送网络请求(异步)
        call.enqueue(new Callback<ResponseInfo<AdData>>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<ResponseInfo<AdData>> call, Response<ResponseInfo<AdData>> response) {
                Log.d("123成功",response.body().getData().getFallback());
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<ResponseInfo<AdData>> call, Throwable t) {
                Log.d("123失败","失败");
            }
        });
    }
}
