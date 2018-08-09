package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.UMengLoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostAuthLogin {

    @POST("userauth/login")
    Call<ResponseInfo<String>> getCall(@Body UMengLoginRequest uMengLoginRequest);
}
