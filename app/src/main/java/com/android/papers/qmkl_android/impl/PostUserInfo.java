package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.ResponseInfo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 使用token登录
 * 即自动登录
 */
public interface PostUserInfo {
    @POST("user/info")
    Call<ResponseInfo> getCall(@Field("token") String token);
}
