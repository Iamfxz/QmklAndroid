package com.android.papers.qmkl_android.impl;


import com.android.papers.qmkl_android.RequestInfo.LoginReq;
import com.android.papers.qmkl_android.model.ResponseInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostLogin {
    @POST("user/login")
    Call<ResponseInfo> getCall(@Body LoginReq Req);

}
