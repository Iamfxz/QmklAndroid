package com.example.robin.papers.studentCircle.studentCircleActivity;

import android.os.Bundle;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.tools.ImageLoaders;
import com.example.robin.papers.studentCircle.view.NoScrollListView;
import com.example.robin.papers.util.ConstantUtils;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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
        AddComment(mixinfo.postInfo.getId()+"",mixinfo.postInfo.getCommentNum());
        AddToolbar();
    }

    /**
     * 载入用户信息
     */
    public void InData(PostInfo postInfo, boolean isLike) {

        ImageLoaders.setsendimg(ConstantUtils.postUrl+postInfo.getUserId(),headerImg);//头像
        username.setText(postInfo.getNickName());
        dateTime.setText(postInfo.getCreateTime());
        userContent.setText(postInfo.getContent());
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
        likeIcon.setOnClickListener(new LikeOnclick(likeCount,likeIcon,likeIcon2,postIsLikeRequest));
        likeIcon2.setOnClickListener(new LikeOnclick(likeCount,likeIcon,likeIcon2,postIsLikeRequest));
        likeCount.setOnClickListener(new LikeOnclick(likeCount,likeIcon,likeIcon2,postIsLikeRequest));
        likeText.setOnClickListener(new LikeOnclick(likeCount,likeIcon,likeIcon2,postIsLikeRequest));

        //发表留言监听，弹出下方输入框
        commentIcon.setOnClickListener(new CommentOnclick());
        commentIcon2.setOnClickListener(new CommentOnclick());
        commentCount.setOnClickListener(new CommentOnclick());
        commentText.setOnClickListener(new CommentOnclick());

        //发送留言监听
        sendBtn.setOnClickListener(new SendCommentOnclick());

        //返回按钮监听
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //弹出菜单监听
        popupmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

    }

    /**
     * 载入评论
     */
    public void AddComment(String postId,int commentNum){
        commentListAdapter=new CommentListAdapter(this,DetailsFromCommentActivity.class);
        commentList.setAdapter(commentListAdapter);
        if(commentNum!=0){
            String token=SharedPreferencesUtils.getStoredMessage(this,"token");
            GetCommentListRequest getCommentListRequest=new GetCommentListRequest(token,postId,"1",String.valueOf(commentNum));
            RetrofitUtils.postGetCommentList(this,getCommentListRequest);
        }
    }

    /**
     * @param view popupMenu需要依附的view
     *             弹出菜单
     */
    private void showPopupMenu(View view) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(DetailsFromCommentActivity.this, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.popupemu, popupMenu.getMenu());
        //根据是否收藏显示不同菜单
        boolean isCollect=mixinfo.is_collect;
        if(isCollect){
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
                        collectPost();
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
    public class LikeOnclick implements View.OnClickListener {
        private TextView like_count;
        private ImageView like,like2;
        private PostIsLikeRequest postIsLikeRequest;
        LikeOnclick(TextView like_count, ImageView like, ImageView like2,PostIsLikeRequest postIsLikeRequest) {
            this.like_count = like_count;
            this.like = like;
            this.like2 = like2;
            this.postIsLikeRequest=postIsLikeRequest;
        }

        @Override
        public void onClick(View v) {
            RetrofitUtils.postLike(DetailsFromCommentActivity.this,postIsLikeRequest,like,like2,like_count,mixinfo.is_like);
            if(mixinfo.is_like){
                mixinfo.postInfo.setLikeNum(mixinfo.postInfo.getLikeNum()-1);
            }
            else {
                mixinfo.postInfo.setLikeNum(mixinfo.postInfo.getLikeNum()+1);
            }
            mixinfo.is_like=!mixinfo.is_like;
        }
    }

    public class CommentOnclick implements View.OnClickListener {
        CommentOnclick() {
        }
        @Override
        public void onClick(View v) {
            bottomView.setVisibility(View.VISIBLE);
            showSoftInputFromWindow(editText);
        }
    }

    public class SendCommentOnclick implements View.OnClickListener {

        SendCommentOnclick() {

        }
        @Override
        public void onClick(View v) {
            if(editText.getText().toString()!=null && editText.getText().toString().trim()!=""){
                String token= SharedPreferencesUtils.getStoredMessage(DetailsFromCommentActivity.this,"token");
                CommentAddRequest commentAddRequest=new CommentAddRequest(token,editText.getText().toString().trim(),mixinfo.postInfo.getId()+"");
                RetrofitUtils.postAddComment(DetailsFromCommentActivity.this,commentAddRequest,commentCount);
                hideEdit();
            }
            else {
                Toast.makeText(DetailsFromCommentActivity.this,"评论内容不能为空！",Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void showSoftInputFromWindow(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    public void hideEdit() {
        if (bottomView.getVisibility() == View.VISIBLE) {
            bottomView.setVisibility(View.GONE);
            editText.setText("");
            InputMethodManager inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0); //强制隐藏键盘
        }
    }

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
                hideEdit();
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
                hideEdit();
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
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
//            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
            if (event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


}
