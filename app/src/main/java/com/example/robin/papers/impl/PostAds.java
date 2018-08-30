package com.example.robin.papers.impl;

import com.example.robin.papers.model.AdData;
import com.example.robin.papers.model.ResponseInfo;

import retrofit2.Call;
import retrofit2.http.POST;

public interface PostAds {
    @POST("qmkl1.0.0/ad/detail/adpage")
    Call<ResponseInfo<AdData>> getCall();
}
