package com.example.robin.papers.impl;

import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.AuthPerInfoRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostAuthPerInfo {

    @POST("qmkl1.0.0/userauth/update/info")
    Call<ResponseInfo<String>> getCall(@Body AuthPerInfoRequest authPerInfoRequest);
}
