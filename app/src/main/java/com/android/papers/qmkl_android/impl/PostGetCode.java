package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.GetCodeRequest;

import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostGetCode {
    @POST("qmkl1.0.0/sms/send")
    Call<ResponseInfo<String>> getCall(@Body GetCodeRequest request);
}
