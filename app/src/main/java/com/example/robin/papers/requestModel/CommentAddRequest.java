package com.example.robin.papers.requestModel;

//用户对帖子发表评论
public class CommentAddRequest {

    String token,content,postId;

    public CommentAddRequest(String token, String content, String postId) {
        this.token = token;
        this.content = content;
        this.postId = postId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
