package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.PerfectInfoRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostPerfectInfo {
    @POST("user/all/info")
    Call<ResponseInfo<String>> getCall(@Body PerfectInfoRequest perfectInfoRequest);
}
