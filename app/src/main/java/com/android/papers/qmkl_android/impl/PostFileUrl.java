package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.FileUrlRes;
import com.android.papers.qmkl_android.requestModel.FileRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 作者：方向臻 on 2018/7/25/025 15:38
 * 邮箱：273332683@qq.com
 *      请求文件的详细数据的接口
 */
public interface PostFileUrl {
    @POST("file/download/url")
    Call<FileUrlRes> getCall(@Body FileRequest fileDetailRequest);
}
