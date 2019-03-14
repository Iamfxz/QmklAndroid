package com.example.robin.papers.studentCircle.listener;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.robin.papers.requestModel.PostIsLikeRequest;
import com.example.robin.papers.studentCircle.adapter.CollectionListAdapter;
import com.example.robin.papers.studentCircle.adapter.DynamicListAdapter;
import com.example.robin.papers.studentCircle.adapter.MixListAdapter;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.studentCircleActivity.DetailsActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.DetailsFromCommentActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MixShowActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyCollectionActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyDynamicActivity;
import com.example.robin.papers.util.RetrofitUtils;


public class LikeOnclick implements View.OnClickListener {
    private Context context;
    private Class sourceClass;
    private TextView like_count;
    private ImageView like,like2;
    private int index;
    private PostIsLikeRequest postIsLikeRequest;
    private Mixinfo mixinfo;
    public LikeOnclick(Context context, Class sourceClass, TextView like_count, ImageView like, ImageView like2, int index, PostIsLikeRequest postIsLikeRequest) {
        if(sourceClass==MixListAdapter.class){
            mixinfo= MixShowActivity.data.get(index);
        }
        else if(sourceClass== CollectionListAdapter.class){
            mixinfo= MyCollectionActivity.data.get(index);
        }
        else if(sourceClass==DynamicListAdapter.class){
            mixinfo= MyDynamicActivity.data.get(index);
        }
        else if(sourceClass==null){
            mixinfo= DetailsFromCommentActivity.mixinfo;
        }
        this.context=context;
        this.sourceClass=sourceClass;
        this.like_count = like_count;
        this.like = like;
        this.like2 = like2;
        this.index = index;
        this.postIsLikeRequest=postIsLikeRequest;
    }

    @Override
    public void onClick(View v) {
        RetrofitUtils.postLike(context,postIsLikeRequest,like,like2,like_count,mixinfo.is_like);
        if(mixinfo.is_like){
            mixinfo.postInfo.setLikeNum(mixinfo.postInfo.getLikeNum()-1);
        }
        else {
            mixinfo.postInfo.setLikeNum(mixinfo.postInfo.getLikeNum()+1);
        }
        mixinfo.is_like=!mixinfo.is_like;
        if(sourceClass==MixListAdapter.class){
            MixShowActivity.data.set(index,mixinfo);
            MixShowActivity.adapterData.notifyDataSetChanged();
        }
        else if(sourceClass== CollectionListAdapter.class){
            MyCollectionActivity.data.set(index,mixinfo);
            MyCollectionActivity.adapterData.notifyDataSetChanged();
        }
        else if(sourceClass == DynamicListAdapter.class){
            MyDynamicActivity.data.set(index,mixinfo);
            MyDynamicActivity.adapterData.notifyDataSetChanged();
        }
        else if(sourceClass== null ){
            DetailsFromCommentActivity.mixinfo=mixinfo;
        }
    }
}

