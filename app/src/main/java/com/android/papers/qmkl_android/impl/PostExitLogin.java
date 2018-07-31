package com.android.papers.qmkl_android.impl;

import android.support.annotation.Nullable;

import com.android.papers.qmkl_android.requestModel.ExitLoginRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostExitLogin {

    @POST("user/out")
    Call<String> getCall(@Body ExitLoginRequest exitLoginRequest);
}
