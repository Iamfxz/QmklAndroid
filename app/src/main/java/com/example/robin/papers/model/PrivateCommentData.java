package com.example.robin.papers.model;

import java.io.Serializable;

public class PrivateCommentData implements Serializable {

    private int id,userId1,userId2,postId;
    private String content,date,nickName1,nickName2;

    public PrivateCommentData(int id, int userId1, int userId2, int postId, String content, String date, String nickName1, String nickName2) {
        this.id = id;
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.postId = postId;
        this.content = content;
        this.date = date;
        this.nickName1 = nickName1;
        this.nickName2 = nickName2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId1() {
        return userId1;
    }

    public void setUserId1(int userId1) {
        this.userId1 = userId1;
    }

    public int getUserId2() {
        return userId2;
    }

    public void setUserId2(int userId2) {
        this.userId2 = userId2;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNickName1() {
        return nickName1;
    }

    public void setNickName1(String nickName1) {
        this.nickName1 = nickName1;
    }

    public String getNickName2() {
        return nickName2;
    }

    public void setNickName2(String nickName2) {
        this.nickName2 = nickName2;
    }
}

