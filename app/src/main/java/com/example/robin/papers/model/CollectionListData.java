package com.example.robin.papers.model;

/**
 * 存储当前用户的收藏贴
 */
public class CollectionListData {

    private int id,userId,postId;
    private String createTime;
    private boolean state;
    private PostInfo postResult;

    public CollectionListData(int id, int userId, int postId, String createTime, boolean state, PostInfo postResult) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.createTime = createTime;
        this.state = state;
        this.postResult = postResult;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public PostInfo getPostResult() {
        return postResult;
    }

    public void setPostResult(PostInfo postResult) {
        this.postResult = postResult;
    }
}
