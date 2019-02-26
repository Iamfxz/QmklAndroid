package com.example.robin.papers.requestModel;

/**
 * 是否点赞以及收藏，添加点赞、收藏或取消
 */
public class PostIsLikeRequest {
    String token,postId;

    public PostIsLikeRequest(String token, String postId) {
        this.token = token;
        this.postId = postId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
