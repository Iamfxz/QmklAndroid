package com.example.robin.papers.impl;

import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.CommentAddRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostCommentAdd {
    @POST("qmkl1.0.0/comment/add")
    Call<ResponseInfo> getCall(@Body CommentAddRequest commentAddRequest);
}
