package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.FileDetailRes;
import com.android.papers.qmkl_android.requestModel.FileRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 作者：方向臻 on 2018/7/26/026 14:52
 * 邮箱：273332683@qq.com
 *
 */
public interface PostFileDetail {
    @POST("file/list/detail")
    Call<FileDetailRes> getCall(@Body FileRequest fileRequest);
}
