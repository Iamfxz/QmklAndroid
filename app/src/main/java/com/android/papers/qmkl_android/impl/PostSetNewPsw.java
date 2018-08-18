package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.SetNewPswRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostSetNewPsw {

    @POST("qmkl1.0.0/user/update/password")
    Call<ResponseInfo> getCall(@Body SetNewPswRequest newPswRequest);
}
