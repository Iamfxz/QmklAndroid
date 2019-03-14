package com.example.robin.papers.model;

import java.io.Serializable;

public class CommentData implements Serializable {

    private int id,userId,postId;
    private String content,createTime,nickName;

    public CommentData(int id, int userId, int postId, String content, String createTime, String nickName) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.createTime = createTime;
        this.nickName = nickName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
