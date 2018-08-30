package com.example.robin.papers.impl;

import com.example.robin.papers.model.AcademiesOrCollegesRes;

import retrofit2.Call;
import retrofit2.http.POST;

public interface PostAllColleges {
    @POST("qmkl1.0.0/college/list")
    Call<AcademiesOrCollegesRes> getCall();
}
