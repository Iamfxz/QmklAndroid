package com.example.robin.papers.studentCircle.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.robin.papers.R;
import com.example.robin.papers.impl.PostPostIsLike;
import com.example.robin.papers.model.PostInfo;
import com.example.robin.papers.requestModel.PostIsLikeRequest;
import com.example.robin.papers.studentCircle.dwcorephoto.MixShowActivity;
import com.example.robin.papers.studentCircle.dwcorephoto.PreviewImage;
import com.example.robin.papers.studentCircle.model.DialogueInfo;
import com.example.robin.papers.studentCircle.model.ImageBDInfo;
import com.example.robin.papers.studentCircle.model.ImageInfo;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.tools.ImageLoaders;
import com.example.robin.papers.util.ConstantUtils;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DavidWang on 15/10/8.
 */
public class MixListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Mixinfo> data;
    private ImageBDInfo bdInfo;
    private MixShowActivity activity;

    private int ImagaId[] = {R.id.img_0, R.id.img_1, R.id.img_2, R.id.img_3, R.id.img_4, R.id.img_5, R.id.img_6, R.id.img_7, R.id.img_8};
    int i=1;
    public MixListAdapter(Context context, ArrayList<Mixinfo> data) {
        this.context = context;
        this.data = data;
        bdInfo = new ImageBDInfo();
        activity = (MixShowActivity) context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        Mixinfo info = data.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            //初始化holder以及其中控件
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.mix_view, null);
            holder.list_img = (CircleImageView) convertView.findViewById(R.id.listuserimg);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.fullText = (TextView) convertView.findViewById(R.id.fullText);
            holder.usercontent = (TextView) convertView.findViewById(R.id.usercontent);
            holder.gridview = (GridLayout) convertView.findViewById(R.id.gridview);
            holder.showimage = (ImageView) convertView.findViewById(R.id.showimage);
            holder.allLayout = (LinearLayout) convertView.findViewById(R.id.allLayout);
            holder.like=convertView.findViewById(R.id.like);
            holder.like_count=convertView.findViewById(R.id.like_count);
            holder.comment=convertView.findViewById(R.id.comment);
            holder.evaluationLayout = (LinearLayout) convertView.findViewById(R.id.evaluationLayout);
            for (int i = 0; i < 9; i++) {
                holder.imgview[i] = (ImageView) convertView.findViewById(ImagaId[i]);
            }
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //留言者信息
        ImageLoaders.setsendimg(ConstantUtils.postUrl+info.postInfo.getUserId(), holder.list_img);//头像
        holder.username.setText(info.postInfo.getNickName());//昵称
        i++;
        holder.usercontent.setText(info.postInfo.getContent());//评论内容
        //没有图片，设置为GONE
        holder.showimage.setVisibility(View.GONE);
        holder.gridview.setVisibility(View.GONE);

        holder.like_count.setText(info.postInfo.getLikeNum() == 0 ? "" : info.postInfo.getLikeNum()+"");//点赞数
        holder.like.setImageResource(R.drawable.like1);//点赞图片

        //请求该评论app使用者是否点赞
        String token= SharedPreferencesUtils.getStoredMessage(context,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,info.postInfo.getId()+"");
        RetrofitUtils.postIsLike(context,postIsLikeRequest,holder.like,position);
        //添加点赞监听
        holder.like.setOnClickListener(new LikeOnclick(holder.like_count,holder.like,position,postIsLikeRequest));

//        if (info.data.size() == 1) {
//            holder.showimage.setVisibility(View.VISIBLE);
//            holder.gridview.setVisibility(View.GONE);
//            ImageInfo imageInfo = info.data.get(0);
//            float w = imageInfo.width;
//            float h = imageInfo.height;
//            float width = 0.0f;
//            float height = 0.0f;
//            if (w > h) {
//                width = activity.Width - dip2px(130);
//                height = width * h / w;
//            } else if (w < h) {
//                height = dip2px(220);
//                width = w * height / h;
//            } else if (w == h) {
//                width = activity.Width / 2;
//                height = width * h / w;
//            }
//
//            ImageLoaders.setsendimg(imageInfo.url, holder.showimage);
//            holder.showimage.getLayoutParams().width = (int) width;
//            holder.showimage.getLayoutParams().height = (int) height;
//            holder.showimage.setOnClickListener(new SingleOnclick(position, holder.showimage, holder.allLayout));
//        } else if (info.data.size() > 1) {
//            holder.showimage.setVisibility(View.GONE);
//            holder.gridview.setVisibility(View.VISIBLE);
//            int a = info.data.size() / 3;
//            int b = info.data.size() % 3;
//            if (b > 0) {
//                a++;
//            }
//            float width = (activity.Width - dip2px(80) - dip2px(2)) / 3;
//            holder.gridview.getLayoutParams().height = (int) (a * width);
//
//            for (int i = 0; i < 9; i++) {
//                holder.imgview[i].setVisibility(View.GONE);
//            }
//
//            for (int i = 0; i < info.data.size(); i++) {
//                ImageInfo imageInfo = info.data.get(i);
//                holder.imgview[i].setVisibility(View.VISIBLE);
//                holder.imgview[i].getLayoutParams().width = (int) width;
//                holder.imgview[i].getLayoutParams().height = (int) width;
//                ImageLoaders.setsendimg(imageInfo.url, holder.imgview[i]);
//                holder.imgview[i].setOnClickListener(new GridOnclick(position, holder.imgview[i], i, holder.gridview, holder.allLayout));
//            }
//        }

        setContentLayout(holder.usercontent, holder.fullText);

//        if (info.is_select) {
//            holder.fullText.setText("收起");
//            holder.usercontent.setMaxLines(50);
//        } else {
//            holder.fullText.setText("全文");
//            holder.usercontent.setMaxLines(6);
//        }

        holder.fullText.setOnClickListener(new fullTextOnclick(holder.usercontent, holder.fullText, position));

//        holder.evaluationLayout.removeAllViews();
//        for (int i = 0; i < info.dialogdata.size(); i++){
//            holder.relativeLayout = View.inflate(context, R.layout.evaluation_view, null);
//            holder.relativeLayout.setTag( position * i+88);
//            holder.contentText = (TextView) holder.relativeLayout.findViewById(R.id.contentText);
//            holder.leftText = (TextView) holder.relativeLayout.findViewById(R.id.leftText);
//            holder.defaultText = (TextView) holder.relativeLayout.findViewById(R.id.defaultText);
//            holder.rightText = (TextView) holder.relativeLayout.findViewById(R.id.rightText);
//            holder.contentEdit = (EditText) holder.relativeLayout.findViewById(R.id.contentEdit);
//            holder.relativeLayout.setOnClickListener(new evaluationOnclick(position,holder.evaluationLayout,holder.allLayout));
//            DialogueInfo dinfo = info.dialogdata.get(i);
//            holder.contentText.setText(dinfo.content);
//            holder.evaluationLayout.addView(holder.relativeLayout);
//        }
//
//
//
//        holder.leftText.setOnClickListener(new NameOnclick(position + "我来"));
//        holder.rightText.setOnClickListener(new NameOnclick(position + "你来"));

        return convertView;
    }

    public class ViewHolder {
        CircleImageView list_img;
        TextView username;
        TextView usercontent, fullText;
        GridLayout gridview;
        ImageView showimage;
        ImageView imgview[] = new ImageView[9];
        LinearLayout evaluationLayout, allLayout;
        View relativeLayout;
        TextView contentText, leftText, defaultText, rightText;
        EditText contentEdit;
        ImageView like,comment;
        TextView like_count;
    }

    class SingleOnclick implements View.OnClickListener {

        private int index;
        private ImageView imageView;
        private LinearLayout allLayout;


        public SingleOnclick(int index, ImageView imageView, LinearLayout allLayout) {
            this.index = index;
            this.imageView = imageView;
            this.allLayout = allLayout;
        }

        @Override
        public void onClick(View v) {

            View c = activity.mixlist.getChildAt(0);
            int top = c.getTop();
            int firstVisiblePosition = activity.mixlist.getFirstVisiblePosition();

            float height = 0.0f;
            for (int i = 0; i < ((index + 1) - firstVisiblePosition); i++) {
                View view = activity.mixlist.getChildAt(i);
                height += view.getHeight();
            }
            bdInfo.x = imageView.getLeft() + allLayout.getLeft();
            bdInfo.y = imageView.getTop() + height + top + activity.mixlist.getTop() + allLayout.getTop();
            bdInfo.width = imageView.getLayoutParams().width;
            bdInfo.height = imageView.getLayoutParams().height;
            Intent intent = new Intent(context, PreviewImage.class);
            ArrayList<ImageInfo> info = data.get(index).data;
            intent.putExtra("data", (Serializable) info);
            intent.putExtra("bdinfo", bdInfo);
            intent.putExtra("index", 0);
            intent.putExtra("type", 0);
            context.startActivity(intent);
        }
    }

    class GridOnclick implements View.OnClickListener {

        private int index;
        private int row;
        private ImageView imageView;
        private GridLayout gridLayout;
        private LinearLayout allLayout;

        public GridOnclick(int index, ImageView imageView, int row, GridLayout gridLayout, LinearLayout allLayout) {
            this.index = index;
            this.imageView = imageView;
            this.gridLayout = gridLayout;
            this.row = row;
            this.allLayout = allLayout;
        }

        @Override
        public void onClick(View v) {
            View c = activity.mixlist.getChildAt(0);
            int top = c.getTop();
            int firstVisiblePosition = activity.mixlist.getFirstVisiblePosition();
            float height = 0.0f;
            for (int i = 0; i < ((index + 1) - firstVisiblePosition); i++) {
                View view = activity.mixlist.getChildAt(i);
                height += view.getHeight();
            }
            bdInfo.x = imageView.getLeft() + gridLayout.getLeft() + allLayout.getLeft();
            bdInfo.y = allLayout.getTop() + gridLayout.getTop() + imageView.getTop() + height + top + activity.mixlist.getTop();
            bdInfo.width = imageView.getLayoutParams().width;
            bdInfo.height = imageView.getLayoutParams().height;
            Intent intent = new Intent(context, PreviewImage.class);
            ArrayList<ImageInfo> info = data.get(index).data;
            intent.putExtra("data", (Serializable) info);
            intent.putExtra("bdinfo", bdInfo);
            intent.putExtra("index", row);
            intent.putExtra("type", 3);
            context.startActivity(intent);
        }
    }

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    class fullTextOnclick implements View.OnClickListener {

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


    class LikeOnclick implements View.OnClickListener {

        private TextView like_count;
        private ImageView like;
        private int index;
        private PostIsLikeRequest postIsLikeRequest;

        LikeOnclick(TextView like_count, ImageView like, int index,PostIsLikeRequest postIsLikeRequest) {
            this.like_count = like_count;
            this.like = like;
            this.index = index;
            this.postIsLikeRequest=postIsLikeRequest;
        }

        @Override
        public void onClick(View v) {
            Mixinfo info = data.get(index);
            RetrofitUtils.postLike(context,postIsLikeRequest,like,like_count,info.is_like);
            info.is_like=!info.is_like;
//            info.is_select = !info.is_select;
//            data.set(index, info);

        }
    }

    private class evaluationOnclick implements View.OnClickListener {

        private int index;
        private EditText contentEdit;
        private LinearLayout evaluationLayout,allLayout;

        evaluationOnclick(int index, LinearLayout evaluationLayout, LinearLayout allLayout) {
            this.index = index;
            this.evaluationLayout = evaluationLayout;
            this.allLayout = allLayout;
        }

        @Override
        public void onClick(View v) {
            contentEdit = (EditText)v.findViewById(R.id.contentEdit);
            contentEdit.setFocusable(true);
            contentEdit.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) contentEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(contentEdit, 0);
            final int hight = evaluationLayout.getTop()+v.getTop()+allLayout.getTop();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    activity.SendContent(index,hight);
                }
            }, 200);
        }
    }

    ;

    private void setContentLayout(final TextView usercontent,
                                  final TextView fullText) {
        ViewTreeObserver vto = usercontent.getViewTreeObserver();
        Log.d("123456", "setContentLayout: ");
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

    class NameOnclick implements View.OnClickListener {

        private String name;

        NameOnclick(String name) {
            this.name = name;
        }

        @Override
        public void onClick(View v) {
            activity.showToast(name);
        }
    }

}
