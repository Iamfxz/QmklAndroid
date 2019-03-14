package com.example.robin.papers.studentCircle.adapter;

import android.content.Context;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robin.papers.R;
import com.example.robin.papers.model.CommentListData;
import com.example.robin.papers.studentCircle.studentCircleActivity.MixShowActivity;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyCollectionActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyDynamicActivity;
import com.example.robin.papers.studentCircle.tools.DataManagerUtils;
import com.example.robin.papers.studentCircle.tools.ImageLoaders;
import com.example.robin.papers.util.ConstantUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;


public class CommentListAdapter extends BaseAdapter{

    private CommentListData commentListData;
    private Context context;
    private int mixPosition;
    private Class sourceClass;

    public CommentListAdapter(Context context, int mixPosition,Class sourceClass){
        this.context = context;
        this.sourceClass=sourceClass;
        this.commentListData = DataManagerUtils.getCommentListData(sourceClass,mixPosition);
        this.mixPosition = mixPosition;
    }


    @Override
    public int getCount() {
        int oldCommentNum=commentListData.getComment().size()+commentListData.getPrivateComment().size();
        this.commentListData = DataManagerUtils.getCommentListData(sourceClass,mixPosition);
    if(oldCommentNum!=commentListData.getComment().size()+commentListData.getPrivateComment().size())
        Log.d("测试", "getCount: "+(commentListData.getComment().size()+commentListData.getPrivateComment().size()));
        return commentListData.getComment().size()+commentListData.getPrivateComment().size();
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
        holder.commentUser.setText(commentListData.getComment().get(position).getNickName());
        //评论转码
        String writeNote="";
        try {
            writeNote = URLDecoder.decode(commentListData.getComment().get(position).getContent(), "utf-8");//utf-8解码
            if(writeNote.length()>5 && writeNote.substring(0,5).equals("仅楼主可见")){
                holder.commentText.setTextColor(context.getResources().getColor(R.color.btn_blue));
                holder.commentText.setText(writeNote.substring(5));
            }
            else{
                holder.commentText.setTextColor(context.getResources().getColor(R.color.black));
                holder.commentText.setText(writeNote);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.commentTime.setText(commentListData.getComment().get(position).getCreateTime());
        ImageLoaders.setsendimg(ConstantUtils.postUrl+commentListData.getComment().get(position).getUserId(),holder.commentImg);//头像

        return convertView;
    }


    public class ViewHolder {
        public TextView commentUser, commentText,commentTime;
        public ImageView commentImg;
    }


}

