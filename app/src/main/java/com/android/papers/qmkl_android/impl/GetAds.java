package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.AdData;
import com.android.papers.qmkl_android.model.ResponseInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GetAds {

    @GET
    Call<ResponseInfo<AdData>> getCall(@Url String url);

}
