package com.example.robin.papers.impl;

import com.example.robin.papers.model.AdData;
import com.example.robin.papers.model.PostInfo;
import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.PostRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


//查询所有帖子
public interface PostAllPost {
    @POST("qmkl1.0.0/post/list")
    Call<ResponseInfo<PostInfo[]>> getCall(@Body PostRequest request);

}
