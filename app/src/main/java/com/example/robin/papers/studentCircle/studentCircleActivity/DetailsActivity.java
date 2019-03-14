package com.example.robin.papers.studentCircle.studentCircleActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
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
import com.example.robin.papers.studentCircle.adapter.DynamicListAdapter;
import com.example.robin.papers.studentCircle.adapter.MixListAdapter;
import com.example.robin.papers.studentCircle.listener.CollectionOnclick;
import com.example.robin.papers.studentCircle.listener.CommentOnclick;
import com.example.robin.papers.studentCircle.listener.LikeOnclick;
import com.example.robin.papers.studentCircle.listener.PopupMenuOnclick;
import com.example.robin.papers.studentCircle.model.CollectionInfo;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.tools.DataManagerUtils;
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
 * 帖子详情活动，此页可从趣聊主页点击帖子、我的收藏点击帖子、我的动态点击帖子、我的评论点击进入
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
    @BindView(R.id.collection)
    ImageView collection;
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

    private Mixinfo mixinfo;
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

        mixinfo= DataManagerUtils.getMixinfo(sourceClass,index);

        InData(mixinfo);
        AddComment(String.valueOf(mixinfo.postInfo.getId()),mixinfo.postInfo.getCommentNum());
        AddListener(mixinfo.postInfo.getId()+"");

        AddToolbar();
    }

    /**
     * 载入用户信息
     */
    public void InData(Mixinfo mixinfo) {

        ImageLoaders.setsendimg(ConstantUtils.postUrl+mixinfo.postInfo.getUserId(),headerImg);//头像
        username.setText(mixinfo.postInfo.getNickName());
        dateTime.setText(mixinfo.postInfo.getCreateTime());
        userContent.setText(URLEncodingUtils.UTF8Decoder(mixinfo.postInfo.getContent()));
        likeCount.setText(mixinfo.postInfo.getLikeNum()+"");
        commentCount.setText(mixinfo.postInfo.getCommentNum()+"");
        if(mixinfo.is_like){
            likeIcon.setImageResource(R.drawable.like2);
            likeIcon2.setImageResource(R.drawable.like2);
        }
        else {
            likeIcon.setImageResource(R.drawable.like1);
            likeIcon2.setImageResource(R.drawable.like1);
        }
        if(mixinfo.is_collect){
            collection.setImageResource(R.drawable.collection);
        }
        else {
            collection.setImageResource(R.drawable.uncollection);
        }
        //是否显示软键盘以及下方评论区
        if((boolean)getIntent().getSerializableExtra("isComment")){
            bottomView.setVisibility(View.VISIBLE);
            DialogUtils.showSoftInputFromWindow(editText);
        }
        else{
            bottomView.setVisibility(View.GONE);
        }
    }

    /**
     * 载入评论
     */
    public void AddComment(String postId,int commentNum){

        commentListAdapter=new CommentListAdapter(this,index,sourceClass);

        commentList.setAdapter(commentListAdapter);

        if(commentNum!=0){
            String token=SharedPreferencesUtils.getStoredMessage(this,"token");
            GetCommentListRequest getCommentListRequest=new GetCommentListRequest(token,postId,"1",String.valueOf(commentNum));
            RetrofitUtils.postGetCommentList(this,getCommentListRequest,sourceClass,index);
        }
    }

    /**
     * @param postId 帖子ID
     */
    public void AddListener(String postId){
        String token= SharedPreferencesUtils.getStoredMessage(this,"token");
        PostIsLikeRequest postIsLikeRequest=new PostIsLikeRequest(token,postId);
        //添加点赞监听
        likeIcon.setOnClickListener(new LikeOnclick(DetailsActivity.this,sourceClass,likeCount,likeIcon,likeIcon2,index,postIsLikeRequest));
        likeIcon2.setOnClickListener(new LikeOnclick(DetailsActivity.this,sourceClass,likeCount,likeIcon,likeIcon2,index,postIsLikeRequest));
        likeCount.setOnClickListener(new LikeOnclick(DetailsActivity.this,sourceClass,likeCount,likeIcon,likeIcon2,index,postIsLikeRequest));
        likeText.setOnClickListener(new LikeOnclick(DetailsActivity.this,sourceClass,likeCount,likeIcon,likeIcon2,index,postIsLikeRequest));
        //发表留言监听，弹出下方输入框
        commentIcon.setOnClickListener(new CommentOnclick(bottomView,editText));
        commentIcon2.setOnClickListener(new CommentOnclick(bottomView,editText));
        commentCount.setOnClickListener(new CommentOnclick(bottomView,editText));
        commentText.setOnClickListener(new CommentOnclick(bottomView,editText));
        //发送留言监听
        sendBtn.setOnClickListener(new SendCommentOnclick(index));
        //返回按钮监听
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //收藏监听
        collection.setOnClickListener(new CollectionOnclick(this,sourceClass, index, collection));

        //弹出菜单监听
        popupmenu.setOnClickListener(new PopupMenuOnclick(this,mixinfo.postInfo.getUserId(),mixinfo.postInfo.getId()+""));

//        commentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                showReplyPopupMenu(view,mixinfo.commentListData.get(position).getNickName());
//                return false;
//            }
//        });
    }


    /**
     * @param view popupMenu需要依附的view
     *             弹出菜单
     * @param replyName 要回复用户的昵称
     */
    private void showReplyPopupMenu(View view, final String replyName) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(DetailsActivity.this, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.reply_popupemu, popupMenu.getMenu());

        popupMenu.show();
        // 通过上面这几行代码，就可以把控件显示出来了
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.reply:
                        editText.setText("@"+replyName+"：");
                        editText.setSelection(editText.getText().toString().length());
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



    public class SendCommentOnclick implements View.OnClickListener {
        private int index;
        SendCommentOnclick(int index) {
            this.index = index;
        }
        @Override
        public void onClick(View v) {
            if(URLEncodingUtils.stringIsNull(editText.getText().toString())){
                String writeNote="";
                if(checkBox.isChecked()){
                    writeNote="仅楼主可见";
                }
                //进行编码
                try {
                    writeNote = writeNote+URLEncoder.encode(editText.getText().toString().trim(), "utf-8");//utf-8编码
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String token= SharedPreferencesUtils.getStoredMessage(DetailsActivity.this,"token");
                CommentAddRequest commentAddRequest=new CommentAddRequest(token,writeNote,mixinfo.postInfo.getId()+"");
                RetrofitUtils.postAddComment(DetailsActivity.this,commentAddRequest,index,mixinfo,commentCount,sourceClass);
                DialogUtils.hideEdit(bottomView,editText);
            }
            else {
                Toast.makeText(DetailsActivity.this,"评论内容不能为空！",Toast.LENGTH_SHORT).show();
            }
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
