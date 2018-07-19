package com.android.papers.qmkl_android.impl;


import com.android.papers.qmkl_android.model.Request;
import com.android.papers.qmkl_android.model.ResponseInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostLogin {
    @POST("user/login")
    Call<ResponseInfo> getCall(@Query("username") String username, @Query("password") String password);

}
