package com.example.robin.papers.impl;

import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostRegister {

    @POST("qmkl1.0.0/user/vercode")
    Call<ResponseInfo> getCall(@Body RegisterRequest registerRequest);
}
