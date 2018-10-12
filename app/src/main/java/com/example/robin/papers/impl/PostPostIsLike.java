package com.example.robin.papers.impl;


import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.LikeDisLikeRequest;
import com.example.robin.papers.requestModel.PostIsLikeRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

//用户帖子是否点赞
public interface PostPostIsLike {

    @POST("qmkl1.0.0/post/like/islike")
    Call<ResponseInfo<Boolean>> getCall(@Body PostIsLikeRequest postIsLikeRequest);
}
