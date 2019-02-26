package com.example.robin.papers.studentCircle.studentCircleActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.robin.papers.R;
import com.example.robin.papers.model.CommentListData;
import com.example.robin.papers.requestModel.PostRequest;
import com.example.robin.papers.studentCircle.adapter.CommentListAdapter;
import com.example.robin.papers.studentCircle.view.CommentListView;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCommentActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView backBtn;

    public static CommentListView commentList;
    public static int page=1;
    public static ArrayList<CommentListData> data;
    public static CommentListAdapter adapterData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycomment);
        ButterKnife.bind(this);
        InData();
        AddListener();

    }

    private void AddListener() {
        //返回按钮监听
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page=1;
                adapterData=null;
                finish();
            }
        });
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String token= SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"token");
                RetrofitUtils.postQueryAPost(MyCommentActivity.this,data.get(position).getPostId()+"",token,MyCommentActivity.this);
            }
        });
    }

    private void InData() {
        commentList=findViewById(R.id.comment_list);
        data=new ArrayList<CommentListData>();
        adapterData=new CommentListAdapter(this,MyCommentActivity.class);
        commentList.setAdapter(adapterData);

        String token= SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"token");
        PostRequest postRequest=new PostRequest(token,String.valueOf(page));
        ZLoadingDialog dialog = new ZLoadingDialog(this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                .setHintText("Loading")
                .setCanceledOnTouchOutside(false)
                .show();
        RetrofitUtils.postGetMyComment(this,postRequest,data,dialog);
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
            page=1;
            adapterData=null;
            this.finish();
        }

        return true;
    }
}
