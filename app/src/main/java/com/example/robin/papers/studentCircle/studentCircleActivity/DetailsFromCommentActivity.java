package com.example.robin.papers.studentCircle.studentCircleActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robin.papers.R;
import com.example.robin.papers.model.PostInfo;
import com.example.robin.papers.requestModel.CommentAddRequest;
import com.example.robin.papers.requestModel.GetCommentListRequest;
import com.example.robin.papers.requestModel.PostIsLikeRequest;
import com.example.robin.papers.studentCircle.adapter.CollectionListAdapter;
import com.example.robin.papers.studentCircle.adapter.CommentListAdapter;
import com.example.robin.papers.studentCircle.adapter.DynamicListAdapter;
import com.example.robin.papers.studentCircle.adapter.MixListAdapter;
import com.example.robin.papers.studentCircle.listener.CommentOnclick;
import com.example.robin.papers.studentCircle.listener.LikeOnclick;
import com.example.robin.papers.studentCircle.listener.PopupMenuOnclick;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.tools.ImageLoaders;
import com.example.robin.papers.studentCircle.view.NoScrollListView;
import com.example.robin.papers.util.ConstantUtils;
import com.example.robin.papers.util.DialogUtils;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;
import com.example.robin.papers.util.URLEncodingUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 帖子详情页，此页和DetailsActivity基本相同，从我的评论处点击进入
 */
public class DetailsFromCommentActivity extends BaseActivity {

    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;
    @BindView(R.id.listuserimg)
    CircleImageView headerImg;
    @BindView(R.id.rightLayout)
    RelativeLayout rightLayout;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.date_time)
    TextView dateTime;
    @BindView(R.id.popupmenu)
    ImageView popupmenu;
    @BindView(R.id.belowLayout)
    RelativeLayout belowLayout;
    @BindView(R.id.usercontent)
    TextView userContent;
    @BindView(R.id.like_icon)
    ImageView likeIcon;
    @BindView(R.id.like_count)
    TextView likeCount;
    @BindView(R.id.like_text)
    TextView likeText;
    @BindView(R.id.comment_icon)
    ImageView commentIcon;
    @BindView(R.id.comment_count)
    TextView commentCount;
    @BindView(R.id.comment_text)
    TextView commentText;
    @BindView(R.id.comment)
    NoScrollListView commentList;
    @BindView(R.id.like_icon2)
    ImageView likeIcon2;
    @BindView(R.id.comment_icon2)
    ImageView commentIcon2;
    @BindView(R.id.bottomView)
    RelativeLayout bottomView;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.sendBtn)
    Button sendBtn;
    @BindView(R.id.iv_back)
    ImageView backBtn;
    @BindView(R.id.checkbox)
    CheckBox checkBox;

    public static Mixinfo mixinfo;
    public static CommentListAdapter commentListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_details);
        ButterKnife.bind(this);
        mixinfo=(Mixinfo)getIntent().getSerializableExtra("mixinfo");

        //请求该评论app使用者是否点赞
        String token= SharedPreferencesUtils.getStoredMessage(this,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,mixinfo.postInfo.getId()+"");
        RetrofitUtils.postIsLike(this,postIsLikeRequest,likeIcon,likeIcon2);

        //请求该评论app使用者是否收藏
        RetrofitUtils.postIsCollect(this,postIsLikeRequest);

        InData(mixinfo.postInfo,mixinfo.is_like);
        AddListener(mixinfo.postInfo.getId()+"");
//        AddComment(mixinfo.postInfo.getId()+"",mixinfo.postInfo.getCommentNum());
        AddToolbar();
    }

    /**
     * 载入用户信息
     */
    public void InData(PostInfo postInfo, boolean isLike) {

        ImageLoaders.setsendimg(ConstantUtils.postUrl+postInfo.getUserId(),headerImg);//头像
        username.setText(postInfo.getNickName());
        dateTime.setText(postInfo.getCreateTime());
        userContent.setText(URLEncodingUtils.UTF8Decoder(postInfo.getContent()));
        likeCount.setText(postInfo.getLikeNum()+"");
        commentCount.setText(postInfo.getCommentNum()+"");
        if(isLike){
            likeIcon.setImageResource(R.drawable.like2);
            likeIcon2.setImageResource(R.drawable.like2);
        }
        else {
            likeIcon.setImageResource(R.drawable.like1);
            likeIcon2.setImageResource(R.drawable.like1);
        }
        bottomView.setVisibility(View.GONE);
    }
    /**
     * @param postId 帖子ID
     */
    public void AddListener(String postId){
        String token= SharedPreferencesUtils.getStoredMessage(this,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,postId);
        //添加点赞监听
        likeIcon.setOnClickListener(new LikeOnclick(this,null,likeCount,likeIcon,likeIcon2,0,postIsLikeRequest));
        likeIcon2.setOnClickListener(new LikeOnclick(this,null,likeCount,likeIcon,likeIcon2,0,postIsLikeRequest));
        likeCount.setOnClickListener(new LikeOnclick(this,null,likeCount,likeIcon,likeIcon2,0,postIsLikeRequest));
        likeText.setOnClickListener(new LikeOnclick(this,null,likeCount,likeIcon,likeIcon2,0,postIsLikeRequest));

        //发表留言监听，弹出下方输入框
        commentIcon.setOnClickListener(new CommentOnclick(bottomView,editText));
        commentIcon2.setOnClickListener(new CommentOnclick(bottomView,editText));
        commentCount.setOnClickListener(new CommentOnclick(bottomView,editText));
        commentText.setOnClickListener(new CommentOnclick(bottomView,editText));

//        //发送留言监听
//        sendBtn.setOnClickListener(new SendCommentOnclick());

        //返回按钮监听
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //弹出菜单监听
        popupmenu.setOnClickListener(new PopupMenuOnclick(this,mixinfo.postInfo.getUserId(),mixinfo.postInfo.getId()+""));


        commentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showReplyPopupMenu(view);
                return false;
            }
        });
    }

//    /**
//     * 载入评论
//     */
//    public void AddComment(String postId,int commentNum){
//        commentListAdapter=new CommentListAdapter(this,DetailsFromCommentActivity.class);
//        commentList.setAdapter(commentListAdapter);
//        if(commentNum!=0){
//            String token=SharedPreferencesUtils.getStoredMessage(this,"token");
//            GetCommentListRequest getCommentListRequest=new GetCommentListRequest(token,postId,"1",String.valueOf(commentNum));
//            RetrofitUtils.postGetCommentList(this,getCommentListRequest);
//        }
//    }

    /**
     * @param view popupMenu需要依附的view
     *             弹出菜单
     */
    private void showReplyPopupMenu(View view) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(DetailsFromCommentActivity.this, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.reply_popupemu, popupMenu.getMenu());

        popupMenu.show();
        // 通过上面这几行代码，就可以把控件显示出来了
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.reply:
                        bottomView.setVisibility(View.VISIBLE);
                        DialogUtils.showSoftInputFromWindow(editText);
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
    private void collectPost(){
        String token= SharedPreferencesUtils.getStoredMessage(this,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,mixinfo.postInfo.getId()+"");
        RetrofitUtils.postCollectPost(postIsLikeRequest,mixinfo.is_collect);

    }

//    public class SendCommentOnclick implements View.OnClickListener {
//
//        SendCommentOnclick() {
//
//        }
//        @Override
//        public void onClick(View v) {
//            if(editText.getText().toString()!=null && !editText.getText().toString().trim().equals("")){
//                String writeNote="";
//                if(checkBox.isChecked()){
//                    writeNote="仅楼主可见";
//                }
//                //进行编码
//                try {
//                    writeNote = writeNote+URLEncoder.encode(editText.getText().toString().trim(), "utf-8");//utf-8编码
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                String token= SharedPreferencesUtils.getStoredMessage(DetailsFromCommentActivity.this,"token");
//                CommentAddRequest commentAddRequest=new CommentAddRequest(token,writeNote,mixinfo.postInfo.getId()+"");
//                RetrofitUtils.postAddComment(DetailsFromCommentActivity.this,commentAddRequest,commentCount);
//                DialogUtils.hideEdit(bottomView,editText);
//            }
//            else {
//                Toast.makeText(DetailsFromCommentActivity.this,"评论内容不能为空！",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


    /**
     * 监听返回按键的事件处理
     *
     * @param keyCode 点击事件的代码
     * @param event   事件
     * @return 有无处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(bottomView.getVisibility() == View.VISIBLE){
                DialogUtils.hideEdit(bottomView,editText);
            }
            else{
                this.finish();
            }
        }

        return true;
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                DialogUtils.hideEdit(bottomView,editText);
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            if (event.getY() > top) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


}
