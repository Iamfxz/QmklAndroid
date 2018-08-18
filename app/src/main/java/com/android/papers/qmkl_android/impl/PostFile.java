package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.FileRes;
import com.android.papers.qmkl_android.requestModel.FileRequest;

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
