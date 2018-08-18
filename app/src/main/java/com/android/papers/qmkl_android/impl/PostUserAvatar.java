package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.model.ResponseInfo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PostUserAvatar {

    @Multipart
    @POST("qmkl1.0.0/user/update/avatar")
    Call<ResponseInfo<String>> getCall(@Part MultipartBody.Part avatar, @Part("token") RequestBody token);
}
