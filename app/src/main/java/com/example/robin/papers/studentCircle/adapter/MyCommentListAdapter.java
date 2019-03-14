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
import com.example.robin.papers.model.MyCommentListData;
import com.example.robin.papers.model.PostInfo;
import com.example.robin.papers.studentCircle.studentCircleActivity.MixShowActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyCollectionActivity;

import com.example.robin.papers.studentCircle.studentCircleActivity.MyDynamicActivity;
import com.example.robin.papers.studentCircle.tools.ImageLoaders;
import com.example.robin.papers.util.ConstantUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;


public class MyCommentListAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<MyCommentListData> commentListData;


    public MyCommentListAdapter(Context context, ArrayList<MyCommentListData> commentListData) {
        this.context = context;
        this.commentListData = commentListData;
    }

    @Override
    public int getCount() {
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
//        ViewHolder holder = null;
//        if (convertView == null) {
//            //初始化holder以及其中控件
//            holder = new ViewHolder();
//            convertView = View.inflate(context, R.layout.my_comment_view, null);
//            holder.commentUser=convertView.findViewById(R.id.username);
//            holder.commentText=convertView.findViewById(R.id.usercontent);
//            holder.commentTime=convertView.findViewById(R.id.date_time);
//            holder.commentImg=convertView.findViewById(R.id.listuserimg);
//            holder.postNickname=convertView.findViewById(R.id.postNickname);
//            holder.postContent=convertView.findViewById(R.id.postContent);
//            convertView.setTag(holder);
//        }
//        else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        Log.d("commentListAdapter", commentListData.size()+"");
//        holder.commentUser.setText(commentListData.get(position).getComment().toString());
//        //评论转码
//        String writeNote="";
//        try {
//            writeNote = URLDecoder.decode(commentListData.get(position).getContent(), "utf-8");//utf-8解码
//            if(writeNote.length()>5 && writeNote.substring(0,5).equals("仅楼主可见")){
//                holder.commentText.setTextColor(context.getResources().getColor(R.color.btn_blue));
//                holder.commentText.setText(writeNote.substring(5));
//            }
//            else{
//                holder.commentText.setTextColor(context.getResources().getColor(R.color.black));
//                holder.commentText.setText(writeNote);
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        holder.commentTime.setText(commentListData.get(position).getCreateTime());
//        ImageLoaders.setsendimg(ConstantUtils.postUrl+commentListData.get(position).getUserId(),holder.commentImg);//头像
//        //原贴未删除
//        if(postInfos.get(position)!=null){
//            holder.postNickname.setText(postInfos.get(position).getNickName());
//            //原贴内容转码
//            writeNote="";
//            try {
//                writeNote = URLDecoder.decode(postInfos.get(position).getContent(), "utf-8");//utf-8解码
//                holder.postContent.setText(writeNote);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        //原贴已删除
//        else{
//
//        }
        return convertView;
    }


    public class ViewHolder {
        public TextView commentUser, commentText,commentTime;
        public ImageView commentImg;
        public TextView postNickname,postContent;
    }


}

