package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostRegister {

    @POST("qmkl1.0.0/user/vercode")
    Call<ResponseInfo> getCall(@Body RegisterRequest registerRequest);
}
