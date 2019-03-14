package com.example.robin.papers.model;

import java.io.Serializable;
import java.util.ArrayList;

//存储当前留言的评论信息
public class CommentListData implements Serializable {

    private ArrayList<CommentData> comment;
    private ArrayList<PrivateCommentData> privateComment;

    public CommentListData(){
        this.comment = new ArrayList<>();
        this.privateComment = new ArrayList<>();
    }
    public CommentListData(ArrayList<CommentData> comment, ArrayList<PrivateCommentData> privateComment) {
        this.comment = comment;
        this.privateComment = privateComment;
    }

    public ArrayList<CommentData> getComment() {
        return comment;
    }

    public void setComment(ArrayList<CommentData> comment) {
        this.comment = comment;
    }

    public ArrayList<PrivateCommentData> getPrivateComment() {
        return privateComment;
    }

    public void setPrivateComment(ArrayList<PrivateCommentData> privateComment) {
        this.privateComment = privateComment;
    }
}
