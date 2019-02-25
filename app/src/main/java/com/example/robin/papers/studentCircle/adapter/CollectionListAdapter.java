package com.example.robin.papers.studentCircle.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.Layout;
import android.util.Log;
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
import com.example.robin.papers.model.CollectionListData;
import com.example.robin.papers.requestModel.PostIsLikeRequest;
import com.example.robin.papers.studentCircle.model.CollectionInfo;
import com.example.robin.papers.studentCircle.studentCircleActivity.DetailsActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MixShowActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.PreviewImage;
import com.example.robin.papers.studentCircle.model.ImageBDInfo;
import com.example.robin.papers.studentCircle.model.ImageInfo;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.tools.ImageLoaders;
import com.example.robin.papers.util.ConstantUtils;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CollectionListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Mixinfo> data;

    int i=1;

    public CollectionListAdapter(Context context, ArrayList<Mixinfo> data) {
        this.context = context;
        this.data=data;

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
            holder.gridview = (GridLayout) convertView.findViewById(R.id.gridview);
            holder.showimage = (ImageView) convertView.findViewById(R.id.showimage);
            holder.allLayout = (LinearLayout) convertView.findViewById(R.id.allLayout);
            holder.like=convertView.findViewById(R.id.like);
            holder.like_count= convertView.findViewById(R.id.like_count);
            holder.like_text= convertView.findViewById(R.id.like_text);
            holder.comment=convertView.findViewById(R.id.comment);
            holder.comment_count=convertView.findViewById(R.id.comment_count);
            holder.comment_text= convertView.findViewById(R.id.comment_text);
            holder.evaluationLayout = (LinearLayout) convertView.findViewById(R.id.evaluationLayout);
            holder.popupmenu =convertView.findViewById(R.id.popupmenu);


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //留言者信息
        ImageLoaders.setsendimg(ConstantUtils.postUrl+info.postInfo.getUserId(), holder.list_img);//头像
        holder.username.setText(info.postInfo.getNickName());//昵称
        holder.dateTtime.setText(info.postInfo.getCreateTime());//留言时间
        i++;
        holder.usercontent.setText(info.postInfo.getContent());//评论内容
        //没有图片，设置为GONE
        holder.showimage.setVisibility(View.GONE);
        holder.gridview.setVisibility(View.GONE);

        holder.like_count.setText(info.postInfo.getLikeNum()+"");//点赞数
        holder.like.setImageResource(R.drawable.like1);//点赞图片

        //请求该评论app使用者是否点赞
        String token= SharedPreferencesUtils.getStoredMessage(context,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,info.postInfo.getId()+"");
        RetrofitUtils.postIsLike(context,postIsLikeRequest,holder.like,position,CollectionListAdapter.class);

        //请求该评论app使用者是否收藏
        RetrofitUtils.postIsCollect(context,postIsLikeRequest,position,CollectionListAdapter.class);

        holder.comment_count.setText(info.postInfo.getCommentNum()+"");//评论数


        //添加点赞监听
        holder.like.setOnClickListener(new LikeOnclick(holder.like_count,holder.like,position,postIsLikeRequest));
        holder.like_count.setOnClickListener(new LikeOnclick(holder.like_count,holder.like,position,postIsLikeRequest));
        holder.like_text.setOnClickListener(new LikeOnclick(holder.like_count,holder.like,position,postIsLikeRequest));

        //点击进入详情页监听
        holder.mixViewLayout.setOnClickListener(new UserContentOnclick(position));
        holder.usercontent.setOnClickListener(new UserContentOnclick(position));
        //添加发表回复监听
        holder.comment.setOnClickListener(new CommentOnclick(position));
        holder.comment_count.setOnClickListener(new CommentOnclick(position));
        holder.comment_text.setOnClickListener(new CommentOnclick(position));

        setContentLayout(holder.usercontent, holder.fullText);


        //点开全文监听
        holder.fullText.setOnClickListener(new fullTextOnclick(holder.usercontent, holder.fullText, position));

        //右上角弹出菜单监听
        holder.popupmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v,position);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        RelativeLayout mixViewLayout;
        CircleImageView list_img;
        TextView username,dateTtime;
        TextView usercontent, fullText;
        GridLayout gridview;
        ImageView showimage;
        LinearLayout evaluationLayout, allLayout;
        ImageView like,comment;
        TextView like_text,comment_text;
        TextView like_count,comment_count;
        ImageView popupmenu;
    }


    /**
     * @param view popupMenu需要依附的view
     * @param position 该帖子在列表中的位置
     *             弹出菜单
     */
    private void showPopupMenu(View view, final int position) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(context, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.popupemu, popupMenu.getMenu());
        //根据是否收藏显示不同菜单
        if(data.get(position).is_collect){
            popupMenu.getMenu().findItem(R.id.collect).setVisible(false);
        }
        else {
            popupMenu.getMenu().findItem(R.id.del_collect).setVisible(false);
        }
        //显示图标
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper helper = (MenuPopupHelper) field.get(popupMenu);
            helper.setForceShowIcon(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        popupMenu.show();
        // 通过上面这几行代码，就可以把控件显示出来了
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.collect:
                    case R.id.del_collect:
                        collectPost(position);
                        break;

                }
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // 控件消失时的事件
            }
        });
    }

    /**
     * 收藏帖子
     */
    private void collectPost(int position){
        String token= SharedPreferencesUtils.getStoredMessage(context,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,data.get(position).postInfo.getId()+"");
        RetrofitUtils.postCollectPost(null,postIsLikeRequest,data.get(position),position,CollectionListAdapter.class);
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


    public class LikeOnclick implements View.OnClickListener {

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
            RetrofitUtils.postLike(context,postIsLikeRequest,like,null,like_count,info.is_like);
            if(info.is_like){
                info.postInfo.setLikeNum(info.postInfo.getLikeNum()-1);
            }
            else {
                info.postInfo.setLikeNum(info.postInfo.getLikeNum()+1);
            }
            info.is_like=!info.is_like;
            data.set(index,info);
        }
    }

    public class CommentOnclick implements View.OnClickListener {
        private int index;

        CommentOnclick(int index) {
            this.index = index;

        }

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(context, DetailsActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("isComment",true);
            intent.putExtra("sourceClass",CollectionListAdapter.class);
            context.startActivity(intent);

        }
    }
    public class UserContentOnclick implements View.OnClickListener {
        private int index;

        UserContentOnclick(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(context, DetailsActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("isComment",false);
            intent.putExtra("sourceClass",CollectionListAdapter.class);
            context.startActivity(intent);
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
