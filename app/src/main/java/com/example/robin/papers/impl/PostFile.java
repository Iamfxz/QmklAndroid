package com.example.robin.papers.impl;

import com.example.robin.papers.model.FileRes;
import com.example.robin.papers.requestModel.FileRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 作者：方向臻 on 2018/7/24/024 11:01
 * 邮箱：273332683@qq.com
 */
public interface PostFile {
    @POST("qmkl1.0.0/file/list")
    Call<FileRes> getCall(@Body FileRequest fileRequest);
}
