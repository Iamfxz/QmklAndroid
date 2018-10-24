package com.example.robin.papers.studentCircle.model;

import com.example.robin.papers.model.PostInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class Mixinfo implements Serializable {

    public PostInfo postInfo;
    public boolean is_select = false;
    public boolean is_like = false;
    public ArrayList<ImageInfo> data;
    public ArrayList<DialogueInfo> dialogdata;

    public Mixinfo(PostInfo postInfo) {
        this.postInfo = postInfo;
    }
}
