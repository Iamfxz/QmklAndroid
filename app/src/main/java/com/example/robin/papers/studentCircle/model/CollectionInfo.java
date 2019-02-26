package com.example.robin.papers.studentCircle.model;

import com.example.robin.papers.model.CollectionListData;
import com.example.robin.papers.model.CommentListData;

import java.io.Serializable;
import java.util.ArrayList;

public class CollectionInfo implements Serializable {
    public CollectionListData collectionListData;
    public boolean is_select = false;//是否选中全文或收起
    public boolean is_like = false;//是否点赞
    public boolean is_collect = false;//是否收藏
    public ArrayList<CommentListData> commentListData=new ArrayList<>();

    public CollectionInfo(CollectionListData collectionListData) {
        this.collectionListData = collectionListData;
    }
}
