package com.example.robin.papers.impl;

import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.GetCodeRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostGetCode {
    @POST("qmkl1.0.0/sms/send")
    Call<ResponseInfo<String>> getCall(@Body GetCodeRequest request);
}
