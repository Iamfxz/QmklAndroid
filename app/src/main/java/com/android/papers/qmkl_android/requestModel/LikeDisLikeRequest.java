package com.android.papers.qmkl_android.requestModel;

/**
 * 作者：方向臻 on 2018/8/9/009 16:39
 * 邮箱：273332683@qq.com
 */
public class LikeDisLikeRequest {
    private String fileId;
    private String token;

    public LikeDisLikeRequest(String fileId , String token){
        this.fileId = fileId;
        this.token = token;
    }
}
