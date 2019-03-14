package com.example.robin.papers.studentCircle.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.robin.papers.R;
import com.example.robin.papers.requestModel.PostIsLikeRequest;
import com.example.robin.papers.studentCircle.listener.CollectionOnclick;
import com.example.robin.papers.studentCircle.listener.CommentOnclick;
import com.example.robin.papers.studentCircle.listener.LikeOnclick;
import com.example.robin.papers.studentCircle.listener.PopupMenuOnclick;
import com.example.robin.papers.studentCircle.listener.UserContentOnclick;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.studentCircleActivity.DetailsActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MixShowActivity;
import com.example.robin.papers.studentCircle.tools.ImageLoaders;
import com.example.robin.papers.util.ConstantUtils;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;
import com.example.robin.papers.util.URLEncodingUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DynamicListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Mixinfo> data;

    public DynamicListAdapter(Context context, ArrayList<Mixinfo> data) {
        this.context = context;
        this.data=data;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        Mixinfo info = data.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            //初始化holder以及其中控件
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.mix_view, null);
            holder.mixViewLayout=convertView.findViewById(R.id.mix_view);
            holder.list_img = (CircleImageView) convertView.findViewById(R.id.listuserimg);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.dateTtime = (TextView) convertView.findViewById(R.id.date_time);
            holder.fullText = (TextView) convertView.findViewById(R.id.fullText);
            holder.usercontent = (TextView) convertView.findViewById(R.id.usercontent);
            holder.like=convertView.findViewById(R.id.like);
            holder.like_count= convertView.findViewById(R.id.like_count);
            holder.like_text= convertView.findViewById(R.id.like_text);
            holder.comment=convertView.findViewById(R.id.comment);
            holder.comment_count=convertView.findViewById(R.id.comment_count);
            holder.comment_text= convertView.findViewById(R.id.comment_text);
            holder.collection= convertView.findViewById(R.id.collection);
            holder.popupmenu =convertView.findViewById(R.id.popupmenu);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //留言者信息
        ImageLoaders.setsendimg(ConstantUtils.postUrl+info.postInfo.getUserId(), holder.list_img);//头像
        holder.username.setText(info.postInfo.getNickName());//昵称
        holder.dateTtime.setText(info.postInfo.getCreateTime());//留言时间
        holder.usercontent.setText(URLEncodingUtils.UTF8Decoder(info.postInfo.getContent()));//评论内容
        holder.like_count.setText(info.postInfo.getLikeNum()+"");//点赞数
        holder.like.setImageResource(R.drawable.like1);//点赞图片

        //请求该评论app使用者是否点赞
        String token= SharedPreferencesUtils.getStoredMessage(context,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,info.postInfo.getId()+"");
        RetrofitUtils.postIsLike(context,postIsLikeRequest,holder.like,position,DynamicListAdapter.class);

        //请求该评论app使用者是否收藏
        RetrofitUtils.postIsCollect(context,postIsLikeRequest,position,DynamicListAdapter.class,holder.collection);

        holder.comment_count.setText(info.postInfo.getCommentNum()+"");//评论数
        //添加点赞监听
        holder.like.setOnClickListener(new LikeOnclick(context,DynamicListAdapter.class,holder.like_count,holder.like,null,position,postIsLikeRequest));
        holder.like_count.setOnClickListener(new LikeOnclick(context,DynamicListAdapter.class,holder.like_count,holder.like,null,position,postIsLikeRequest));
        holder.like_text.setOnClickListener(new LikeOnclick(context,DynamicListAdapter.class,holder.like_count,holder.like,null,position,postIsLikeRequest));

        //点击进入详情页监听
        holder.mixViewLayout.setOnClickListener(new UserContentOnclick(context,position,DynamicListAdapter.class));
        holder.usercontent.setOnClickListener(new UserContentOnclick(context,position,DynamicListAdapter.class));
        //添加发表回复监听
        holder.comment.setOnClickListener(new CommentOnclick(context,position,DynamicListAdapter.class));
        holder.comment_count.setOnClickListener(new CommentOnclick(context,position,DynamicListAdapter.class));
        holder.comment_text.setOnClickListener(new CommentOnclick(context,position,DynamicListAdapter.class));

        setContentLayout(holder.usercontent, holder.fullText);


        //点开全文监听
        holder.fullText.setOnClickListener(new fullTextOnclick(holder.usercontent, holder.fullText, position));
        //收藏监听
        holder.collection.setOnClickListener(new CollectionOnclick(context, DynamicListAdapter.class, position, holder.collection));

        //右上角弹出菜单监听
        holder.popupmenu.setOnClickListener(new PopupMenuOnclick(context,data.get(position).postInfo.getUserId(),data.get(position).postInfo.getId()+""));

        return convertView;
    }

    public class ViewHolder {
        RelativeLayout mixViewLayout;
        CircleImageView list_img;
        TextView username,dateTtime;
        TextView usercontent, fullText;
        ImageView like,comment;
        TextView like_text,comment_text;
        TextView like_count,comment_count;
        ImageView collection;
        ImageView popupmenu;
    }


    /**
     * 收藏帖子
     */
    private void collectPost(int position, ImageView collection){
        String token= SharedPreferencesUtils.getStoredMessage(context,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,data.get(position).postInfo.getId()+"");
        RetrofitUtils.postCollectPost(null,postIsLikeRequest,data.get(position),position,DynamicListAdapter.class,collection);
    }


    public class fullTextOnclick implements View.OnClickListener {

        private TextView usercontent;
        private TextView fullText;
        private int index;

        fullTextOnclick(TextView usercontent, TextView fullText, int index) {
            this.fullText = fullText;
            this.usercontent = usercontent;
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            Mixinfo info = data.get(index);
            if (info.is_select) {
                usercontent.setMaxLines(3);
                fullText.setText("全文");
                usercontent.invalidate();
            } else {
                usercontent.setMaxLines(50);
                fullText.setText("收起");
                usercontent.invalidate();
            }
            info.is_select = !info.is_select;
            data.set(index, info);
        }
    }



    private void setContentLayout(final TextView usercontent,
                                  final TextView fullText) {
        ViewTreeObserver vto = usercontent.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            private boolean isInit;

            @Override
            public boolean onPreDraw() {
                if (isInit) {
                    return true;
                }
                Layout layout = usercontent.getLayout();
                if (layout != null) {
                    int maxline = layout.getLineCount();
                    if (maxline > 3) {
                        fullText.setVisibility(View.VISIBLE);
                    } else {
                        fullText.setVisibility(View.GONE);
                    }
                    isInit = true;
                }
                return true;
            }
        });
    }




}
