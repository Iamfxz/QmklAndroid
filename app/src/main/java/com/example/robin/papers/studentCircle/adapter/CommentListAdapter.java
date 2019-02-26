package com.example.robin.papers.studentCircle.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.robin.papers.R;
import com.example.robin.papers.model.CommentListData;
import com.example.robin.papers.studentCircle.studentCircleActivity.MixShowActivity;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyCollectionActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyCommentActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyDynamicActivity;
import com.example.robin.papers.studentCircle.tools.ImageLoaders;
import com.example.robin.papers.util.ConstantUtils;

import java.util.ArrayList;


public class CommentListAdapter extends BaseAdapter {

    private ArrayList<CommentListData> commentListData;
    private Context context;
    private int mixPosition;
    private Class sourceClass;

    public CommentListAdapter(Context context, int mixPosition,Class sourceClass){
        this.context = context;
        this.sourceClass=sourceClass;
        if(sourceClass ==MixListAdapter.class){
            this.commentListData= MixShowActivity.data.get(mixPosition).commentListData;
        }
        else if(sourceClass ==CollectionListAdapter.class){
            this.commentListData= MyCollectionActivity.data.get(mixPosition).commentListData;
        }
        else if(sourceClass == DynamicListAdapter.class){
            this.commentListData= MyDynamicActivity.data.get(mixPosition).commentListData;
        }
        this.mixPosition = mixPosition;
    }
    public CommentListAdapter(Context context,Class sourceClass){
        this.context = context;
        this.sourceClass=sourceClass;
        this.commentListData= MyCommentActivity.data;
    }

    @Override
    public int getCount() {
        if(sourceClass ==MixListAdapter.class){
            this.commentListData= MixShowActivity.data.get(mixPosition).commentListData;
        }
        else if(sourceClass ==CollectionListAdapter.class){
            this.commentListData= MyCollectionActivity.data.get(mixPosition).commentListData;
        }
        else if(sourceClass == DynamicListAdapter.class){
            this.commentListData= MyDynamicActivity.data.get(mixPosition).commentListData;
        }
        else if(sourceClass == MyCommentActivity.class){
            this.commentListData= MyCommentActivity.data;
        }
        return commentListData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            //初始化holder以及其中控件
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.comment_view, null);
            holder.commentUser=convertView.findViewById(R.id.username);
            holder.commentText=convertView.findViewById(R.id.usercontent);
            holder.commentTime=convertView.findViewById(R.id.date_time);
            holder.commentImg=convertView.findViewById(R.id.listuserimg);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.d("commentListAdapter", commentListData.size()+"");

            holder.commentUser.setText(commentListData.get(position).getNickName());
            holder.commentText.setText(commentListData.get(position).getContent());
            holder.commentTime.setText(commentListData.get(position).getCreateTime());
            ImageLoaders.setsendimg(ConstantUtils.postUrl+commentListData.get(position).getUserId(),holder.commentImg);//头像

        return convertView;
    }
    public class ViewHolder {
        public TextView commentUser, commentText,commentTime;
        public ImageView commentImg;
    }


}

