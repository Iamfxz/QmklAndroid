package com.example.robin.papers.impl;

import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.UMengLoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostAuthLogin {

    @POST("qmkl1.0.0/userauth/login")
    Call<ResponseInfo<String>> getCall(@Body UMengLoginRequest uMengLoginRequest);
}