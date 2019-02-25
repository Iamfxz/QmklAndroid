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
import android.widget.ListView;
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
import com.example.robin.papers.studentCircle.adapter.MixListAdapter;
import com.example.robin.papers.studentCircle.model.CollectionInfo;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.tools.ImageLoaders;
import com.example.robin.papers.util.ConstantUtils;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 *
 */
public class DetailsActivity extends BaseActivity {

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
    ListView commentList;
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

    private Mixinfo mixinfo;
    private CollectionInfo collectionInfo;
    private int index;
    public Class sourceClass;
    public static CommentListAdapter commentListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_details);
        ButterKnife.bind(this);

        index= (int) getIntent().getSerializableExtra("index");
        sourceClass=(Class) getIntent().getSerializableExtra("sourceClass");
        if(sourceClass == MixListAdapter.class) {
            mixinfo= MixShowActivity.data.get(index);
            InData(mixinfo.postInfo,mixinfo.is_like);
            AddComment(String.valueOf(mixinfo.postInfo.getId()),mixinfo.postInfo.getCommentNum(),MixShowActivity.data);
            AddListener(mixinfo.postInfo.getId()+"",mixinfo.is_collect);
        }
        else if(sourceClass == CollectionListAdapter.class){
            collectionInfo= MyCollectionActivity.data.get(index);
            InData(collectionInfo.collectionListData.getPostResult(),collectionInfo.is_like);
            AddComment(String.valueOf(collectionInfo.collectionListData.getPostResult().getId()),collectionInfo.collectionListData.getPostResult().getCommentNum(),MyCollectionActivity.data);
            AddListener(collectionInfo.collectionListData.getPostResult().getId()+"",collectionInfo.is_collect);
        }

        AddToolbar();
    }

    /**
     * 载入用户信息
     */
    public void InData(PostInfo postInfo,boolean isLike) {

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

        //是否显示软键盘以及下方评论区
        if((boolean)getIntent().getSerializableExtra("isComment")){
            bottomView.setVisibility(View.VISIBLE);
            showSoftInputFromWindow(editText);
        }
        else{
            bottomView.setVisibility(View.GONE);
        }


    }


    /**
     * 载入评论
     */
    public void AddComment(String postId,int commentNum, ArrayList data){
        commentListAdapter=new CommentListAdapter(this,index,sourceClass);
        commentList.setAdapter(commentListAdapter);
        if(commentNum!=0){
            String token=SharedPreferencesUtils.getStoredMessage(this,"token");
            GetCommentListRequest getCommentListRequest=new GetCommentListRequest(token,postId,"1",String.valueOf(commentNum));
            RetrofitUtils.postGetCommentList(this,getCommentListRequest,data,index,sourceClass);
        }
    }

    /**
     * @param postId 帖子ID
     * @param isCollect app使用者是否收藏
     */
    public void AddListener(String postId, final boolean isCollect){
        String token= SharedPreferencesUtils.getStoredMessage(this,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,postId);
        //添加点赞监听
        likeIcon.setOnClickListener(new LikeOnclick(likeCount,likeIcon,likeIcon2,index,postIsLikeRequest));
        likeIcon2.setOnClickListener(new LikeOnclick(likeCount,likeIcon,likeIcon2,index,postIsLikeRequest));
        likeCount.setOnClickListener(new LikeOnclick(likeCount,likeIcon,likeIcon2,index,postIsLikeRequest));
        likeText.setOnClickListener(new LikeOnclick(likeCount,likeIcon,likeIcon2,index,postIsLikeRequest));

        //发表留言监听，弹出下方输入框
        commentIcon.setOnClickListener(new CommentOnclick(index));
        commentIcon2.setOnClickListener(new CommentOnclick(index));
        commentCount.setOnClickListener(new CommentOnclick(index));
        commentText.setOnClickListener(new CommentOnclick(index));

        //发送留言监听
        sendBtn.setOnClickListener(new SendCommentOnclick(index));

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
                showPopupMenu(v,isCollect);
            }
        });

    }


    /**
     * @param view popupMenu需要依附的view
     *             弹出菜单
     */
    private void showPopupMenu(View view,boolean isCollect) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(DetailsActivity.this, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.popupemu, popupMenu.getMenu());
        //根据是否收藏显示不同菜单
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
        RetrofitUtils.postCollectPost(DetailsActivity.this,postIsLikeRequest,mixinfo,index);
    }
    public class LikeOnclick implements View.OnClickListener {
        private TextView like_count;
        private ImageView like,like2;
        private int index;
        private PostIsLikeRequest postIsLikeRequest;
        private Mixinfo mixinfo;
        private CollectionInfo collectionInfo;
        LikeOnclick(TextView like_count, ImageView like, ImageView like2,int index,PostIsLikeRequest postIsLikeRequest) {
            if(sourceClass==MixListAdapter.class){
                mixinfo=MixShowActivity.data.get(index);
            }
            else if(sourceClass== CollectionListAdapter.class){
                collectionInfo=MyCollectionActivity.data.get(index);
            }
            this.like_count = like_count;
            this.like = like;
            this.like2 = like2;
            this.index = index;
            this.postIsLikeRequest=postIsLikeRequest;
        }

        @Override
        public void onClick(View v) {
            if(sourceClass==MixListAdapter.class){
                RetrofitUtils.postLike(DetailsActivity.this,postIsLikeRequest,like,like2,like_count,mixinfo.is_like);
                if(mixinfo.is_like){
                    mixinfo.postInfo.setLikeNum(mixinfo.postInfo.getLikeNum()-1);
                }
                else {
                    mixinfo.postInfo.setLikeNum(mixinfo.postInfo.getLikeNum()+1);
                }
                mixinfo.is_like=!mixinfo.is_like;
                MixShowActivity.data.set(index,mixinfo);
                MixShowActivity.adapterData.notifyDataSetChanged();
            }
            else if(sourceClass== CollectionListAdapter.class){
                RetrofitUtils.postLike(DetailsActivity.this,postIsLikeRequest,like,like2,like_count,collectionInfo.is_like);
                if(collectionInfo.is_like){
                    collectionInfo.collectionListData.getPostResult().setLikeNum(collectionInfo.collectionListData.getPostResult().getLikeNum()-1);
                }
                else {
                    collectionInfo.collectionListData.getPostResult().setLikeNum(collectionInfo.collectionListData.getPostResult().getLikeNum()+1);
                }
                collectionInfo.is_like=!collectionInfo.is_like;
                MyCollectionActivity.data.set(index,collectionInfo);
                MyCollectionActivity.adapterData.notifyDataSetChanged();
            }
        }
    }

    public class CommentOnclick implements View.OnClickListener {
        private int index;
        CommentOnclick(int index) {
            this.index = index;
        }
        @Override
        public void onClick(View v) {
            bottomView.setVisibility(View.VISIBLE);
            showSoftInputFromWindow(editText);
        }
    }

    public class SendCommentOnclick implements View.OnClickListener {
        private int index;
        SendCommentOnclick(int index) {
            this.index = index;
        }
        @Override
        public void onClick(View v) {
            if(editText.getText().toString()!=null && editText.getText().toString().trim()!=""){
                String token= SharedPreferencesUtils.getStoredMessage(DetailsActivity.this,"token");
                CommentAddRequest commentAddRequest=new CommentAddRequest(token,editText.getText().toString().trim(),mixinfo.postInfo.getId()+"");
                RetrofitUtils.postAddComment(DetailsActivity.this,commentAddRequest,index,mixinfo,commentCount,sourceClass);
                hideEdit();
            }
            else {
                Toast.makeText(DetailsActivity.this,"评论内容不能为空！",Toast.LENGTH_SHORT).show();
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
