package com.example.robin.papers.model;

import java.io.Serializable;

//存储我的评论接口返回的信息
public class MyCommentListData implements Serializable {


    private PostInfo post;
    private CommentListData comment;

    public MyCommentListData(PostInfo postInfo, CommentListData commentListData) {
        this.post = postInfo;
        this.comment = commentListData;
    }

    public PostInfo getPost() {
        return post;
    }

    public void setPost(PostInfo post) {
        this.post = post;
    }

    public CommentListData getComment() {
        return comment;
    }

    public void setComment(CommentListData comment) {
        this.comment = comment;
    }

}
