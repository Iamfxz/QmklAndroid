package com.example.robin.papers.requestModel;

public class GetCommentListRequest {

    private String token,postId,page,num;

    public GetCommentListRequest(String token, String postId, String page, String num) {
        this.token = token;
        this.postId = postId;
        this.page = page;
        this.num = num;
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

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
