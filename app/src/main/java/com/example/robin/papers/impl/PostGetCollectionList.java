package com.example.robin.papers.impl;

import com.example.robin.papers.model.CollectionListData;
import com.example.robin.papers.model.CommentListData;
import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.requestModel.GetCommentListRequest;
import com.example.robin.papers.requestModel.PostRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostGetCollectionList {
    @POST("qmkl1.0.0/collect/list")
    Call<ResponseInfo<CollectionListData[]>> getCall(@Body PostRequest postRequest);
}
