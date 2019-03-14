package com.example.robin.papers.studentCircle.listener;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.example.robin.papers.requestModel.PostIsLikeRequest;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.studentCircleActivity.DetailsActivity;
import com.example.robin.papers.studentCircle.tools.DataManagerUtils;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;

public class CollectionOnclick implements View.OnClickListener {

    private Context context;
    private Class sourceClass;
    private int position;
    private ImageView collection;

    public CollectionOnclick(Context context, Class sourceClass, int position, ImageView collection) {
        this.context = context;
        this.sourceClass = sourceClass;
        this.position = position;
        this.collection = collection;
    }



    @Override
    public void onClick(View v) {
        collectPost(position,collection);
    }

    /**
     * 收藏帖子
     */
    private void collectPost(int position,ImageView collection){
        String token= SharedPreferencesUtils.getStoredMessage(context,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,DataManagerUtils.getData(sourceClass).get(position).postInfo.getId()+"");
        RetrofitUtils.postCollectPost(null,postIsLikeRequest,DataManagerUtils.getData(sourceClass).get(position),position,sourceClass,collection);
    }

}
