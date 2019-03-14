package com.example.robin.papers.impl;

import com.example.robin.papers.model.AdData;
import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostPicture {
    @POST("qmkl1.0.0/ad/detail/adpage")
    Call<ResponseInfo<AdData>> getAdPage();

    //朋友圈轮播图 编码后 %e6%9c%8b%e5%8f%8b%e5%9c%88%e8%bd%ae%e6%92%ad%e5%9b%be
    @POST("qmkl1.0.0/ad/list/detail/%e6%9c%8b%e5%8f%8b%e5%9c%88%e8%bd%ae%e6%92%ad%e5%9b%be")

    Call<ResponseInfo<AdData[]>> getHomePage();

    //首页轮播图 编码后 %e9%a6%96%e9%a1%b5%e8%bd%ae%e6%92%ad%e5%9b%be
    @POST("qmkl1.0.0/ad/list/detail/%e9%a6%96%e9%a1%b5%e8%bd%ae%e6%92%ad%e5%9b%be")
    Call<ResponseInfo<AdData[]>> getBannerPic();



}
