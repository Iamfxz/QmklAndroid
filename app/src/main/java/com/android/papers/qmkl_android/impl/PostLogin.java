package com.android.papers.qmkl_android.impl;


<<<<<<< HEAD
import android.content.Context;

import com.android.papers.qmkl_android.R;
=======
import com.android.papers.qmkl_android.RequestInfo.LoginReq;
>>>>>>> eebb50d47daf62cb45fe88bbfd810bd36447116e
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.Request;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostLogin {
    @POST("user/login")
<<<<<<< HEAD
    Call<ResponseInfo> getCall(@Body Request request);

=======
    Call<ResponseInfo> getCall(@Body LoginReq Req);
>>>>>>> eebb50d47daf62cb45fe88bbfd810bd36447116e

}
