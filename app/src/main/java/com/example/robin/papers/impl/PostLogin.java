package com.example.robin.papers.impl;


import com.example.robin.papers.requestModel.TokenRequest;
import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.LoginRequest;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostLogin {
    @POST("qmkl1.0.0/user/login")
    Call<ResponseInfo> getCall(@Body LoginRequest request);

    @POST("qmkl1.0.0/user/login")
    Call<ResponseInfo> getTokenCall(@Body TokenRequest token);

}
