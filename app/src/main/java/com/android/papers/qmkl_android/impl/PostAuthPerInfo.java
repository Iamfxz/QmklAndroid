package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.activity.AuthPerUserInfo;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.AuthPerInfoRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostAuthPerInfo {

    @POST("qmkl1.0.0/userauth/update/info")
    Call<ResponseInfo<String>> getCall(@Body AuthPerInfoRequest authPerInfoRequest);
}
