package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.ResponseInfo;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 使用token登录
 * 即自动登录
 */
public interface UserInfo {
    @POST("user/info")
    Call<ResponseInfo> getCall(@Query("token") String token);
}
