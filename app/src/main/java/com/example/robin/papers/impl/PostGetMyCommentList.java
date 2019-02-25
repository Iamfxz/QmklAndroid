package com.example.robin.papers.impl;

import com.example.robin.papers.model.CommentListData;
import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.GetCommentListRequest;
import com.example.robin.papers.requestModel.PostRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostGetMyCommentList {
    @POST("qmkl1.0.0/comment/user/list")
    Call<ResponseInfo<CommentListData[]>> getCall(@Body PostRequest postRequest);
}
