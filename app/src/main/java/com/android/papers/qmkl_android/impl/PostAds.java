package com.android.papers.qmkl_android.impl;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.model.AdData;
import com.android.papers.qmkl_android.model.ResponseInfo;

import retrofit2.Call;
import retrofit2.http.POST;

public interface PostAds {
    @POST("ad/detail/100")
    Call<ResponseInfo<AdData>> getCall();
}
