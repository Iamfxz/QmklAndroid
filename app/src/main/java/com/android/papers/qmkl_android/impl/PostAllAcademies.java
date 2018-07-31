package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.AcademiesOrCollegesRes;
import com.android.papers.qmkl_android.requestModel.QueryAcademiesRequest;
import com.android.papers.qmkl_android.requestModel.TokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PostAllAcademies {
    @POST("academy/list/college")
    Call<AcademiesOrCollegesRes> getCall(@Body QueryAcademiesRequest academiesRequest);

    @POST("academy/list")
    Call<AcademiesOrCollegesRes> getCall(@Body TokenRequest tokenRequest);
}
