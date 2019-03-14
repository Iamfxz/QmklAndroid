package com.example.robin.papers.studentCircle.model;

import com.example.robin.papers.model.CollectionListData;
import com.example.robin.papers.model.CommentListData;
import com.example.robin.papers.model.PostInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class Mixinfo implements Serializable {

    public PostInfo postInfo;
    public boolean is_select = false;//是否选中全文或收起
    public boolean is_like = false;//是否点赞
    public boolean is_collect = false;//是否收藏
    public ArrayList<ImageInfo> data;
    public CommentListData commentListData=new CommentListData();
    public CollectionInfo collectionInfo;
    public Mixinfo(PostInfo postInfo) {
        this.postInfo = postInfo;
    }


    public Mixinfo(CollectionInfo collectionInfo) {
        this.postInfo = collectionInfo.collectionListData.getPostResult();
        this.collectionInfo = collectionInfo;
    }
}
