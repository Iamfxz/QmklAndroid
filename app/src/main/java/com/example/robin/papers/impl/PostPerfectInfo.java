package com.example.robin.papers.impl;

import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.PerfectInfoRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostPerfectInfo {
    @POST("qmkl1.0.0/user/all/info")
    Call<ResponseInfo<String>> getCall(@Body PerfectInfoRequest perfectInfoRequest);
}
