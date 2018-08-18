package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.LikeDisLikeRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 作者：方向臻 on 2018/8/9/009 16:34
 * 邮箱：273332683@qq.com
 *
 */
public interface PostLike {
    @POST("qmkl1.0.0/like/addordesc")
    Call<ResponseInfo> getCall(@Body LikeDisLikeRequest likeDisLikeRequest);

}
