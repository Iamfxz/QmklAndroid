package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.UpdateUserRequest;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostUpdateUserInfo {

    @POST("qmkl1.0.0/user/update/info")
    Call<ResponseInfo> getCall(@Body UpdateUserRequest userRequest);
}
