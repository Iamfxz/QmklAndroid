package com.android.papers.qmkl_android.util;


import android.content.Context;
import android.util.Log;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.model.Request;
import com.android.papers.qmkl_android.model.ResponseInfo;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitUtils {
    public static void postLogin(Request r){
        Context context=null;
        //创建Retrofit对象
//        Log.d("123",context.getString(R.string.base_url));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://120.77.32.233/qmkl0.0.1/")// 设置 网络请求 Url,0.0.4版本
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        //创建 网络请求接口 的实例
        PostLogin request = retrofit.create(PostLogin.class);

        //对 发送请求 进行封装(账号和密码)
        Call<ResponseInfo> call = request.getCall("13157694909","f9e84102d063cf5887093255b7ad7bc64758975f");

        //发送网络请求(异步)
        call.enqueue(new Callback<ResponseInfo>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                Log.d("123成功",response.body().toString());
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
                Log.d("123失败","失败");
            }
        });
    }

}
