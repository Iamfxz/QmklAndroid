package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.AdData;
import com.android.papers.qmkl_android.model.ResponseInfo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface GetAds {

    @FormUrlEncoded
    @POST("user/login")
    Call<ResponseInfo> getTokenCall(@Field("token") String token);

}
