package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.AcademiesOrCollegesRes;
import com.android.papers.qmkl_android.requestModel.TokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostAllColleges {
    @POST("college/list")
    Call<AcademiesOrCollegesRes> getCall(@Body TokenRequest tokenRequest);
}
