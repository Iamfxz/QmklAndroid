package com.example.robin.papers.studentCircle.listener;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import com.example.robin.papers.R;
import com.example.robin.papers.requestModel.PostIsLikeRequest;
import com.example.robin.papers.studentCircle.studentCircleActivity.MixShowActivity;
import com.example.robin.papers.util.DialogUtils;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;

public class PopupMenuOnclick implements View.OnClickListener {

    private Context context;
    private int userId;
    private String postId;

    public PopupMenuOnclick(Context context, int userId,  String postId) {
        this.context = context;
        this.userId = userId;
        this.postId = postId;
    }

    @Override
    public void onClick(View v) {
        PopupMenu popupMenu= DialogUtils.showPopupMenu(context,v,userId);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.report:
                        reportPost();
                        break;
                    case R.id.del_post:
                        delPost();
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 举报帖子
     */
    private void reportPost() {

    }

    /**
     * 删除帖子
     */
    private void delPost() {
        String token= SharedPreferencesUtils.getStoredMessage(context,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,postId);
        RetrofitUtils.postDelPost(context,postIsLikeRequest,(Activity)context,MixShowActivity.class);
    }
}
