package com.android.papers.qmkl_android.util;


import android.util.Log;

import com.android.papers.qmkl_android.RequestInfo.LoginReq;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.model.ResponseInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitUtils {
    public static void postLogin(LoginReq r){
        //创建Retrofit对象
//        Log.d("123",context.getString(R.string.base_url));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://120.77.32.233/qmkl0.0.6/")// 设置 网络请求 Url,0.0.4版本
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        //创建 网络请求接口 的实例
        PostLogin request = retrofit.create(PostLogin.class);

        //对 发送请求 进行封装(账号和密码)
        LoginReq req = new LoginReq("13157694909","f9e84102d063cf5887093255b7ad7bc64758975f");
        Call<ResponseInfo> call = request.getCall(req);

        //发送网络请求(异步)
        call.enqueue(new Callback<ResponseInfo>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                Log.d("123成功",response.body().getMsg());
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
                Log.d("123失败","失败");
            }
        });
    }

}
