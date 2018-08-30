package com.example.robin.papers.impl;

import com.example.robin.papers.model.ResponseInfo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PostUpLoadFiles {

    @Multipart
    @POST("finalexam/app/upfile")
    Call<ResponseInfo<String>> getCall(@Part("userId") RequestBody userId,
                                       @Part MultipartBody.Part file,
                                       @Part("spath") RequestBody spath,
                                       @Part("note") RequestBody note,
                                       @Part("anonymous") RequestBody anonymous);
}
