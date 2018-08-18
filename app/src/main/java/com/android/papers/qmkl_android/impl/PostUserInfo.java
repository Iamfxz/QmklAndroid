package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.UserInfoRes;
import com.android.papers.qmkl_android.requestModel.TokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 使用token登录
 * 即自动登录
 */
public interface PostUserInfo {
    @POST("qmkl1.0.0/user/info")
    Call<UserInfoRes> getCall(@Body TokenRequest token);
}
