package com.android.papers.qmkl_android.impl;


import com.android.papers.qmkl_android.requestModel.TokenLoginRequest;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.LoginRequest;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostLogin {
    @POST("user/login")
    Call<ResponseInfo> getCall(@Body LoginRequest request);

    @POST("user/login")
    Call<ResponseInfo> getTokenCall(@Body TokenLoginRequest token);

}
