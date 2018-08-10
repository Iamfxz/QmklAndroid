package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.LikeDisLikeRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 作者：方向臻 on 2018/8/10/010 14:14
 * 邮箱：273332683@qq.com
 */
public interface PostIsDislike {
    @POST("dislike/is/dislike")
    Call<ResponseInfo> getCall(@Body LikeDisLikeRequest likeDisLikeRequest);
}
