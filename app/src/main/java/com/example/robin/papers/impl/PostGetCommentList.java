package com.example.robin.papers.impl;

import com.example.robin.papers.model.CommentListData;
import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.GetCommentListRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostGetCommentList {
    @POST("qmkl1.0.0/comment/list")
    Call<ResponseInfo<CommentListData[]>> getCall(@Body GetCommentListRequest getCommentListRequest);
}
