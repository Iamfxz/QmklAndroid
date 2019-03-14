package com.example.robin.papers.studentCircle.studentCircleActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.robin.papers.R;
import com.example.robin.papers.requestModel.PostRequest;
import com.example.robin.papers.studentCircle.adapter.MixListAdapter;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.view.PullToZoomListView;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.example.robin.papers.util.DialogUtils;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.ArrayList;


public class MixShowActivity extends BaseActivity  {

    public static final String TAG ="MixShowActivity";
    public static PullToZoomListView mixlist;
    public static MixListAdapter adapterData;
    public static ArrayList<Mixinfo> data;
    public static int page=1;
    private ImageView backBtn;
    private TextView postAddBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_show);
        findID();
        InData();
        AddToolbar();
        AddClickListener();
    }



    @Override
    protected void findID() {
        super.findID();
        mixlist = findViewById(R.id.mixlist);
        //首页顶部图片从服务器获取
        //设置默认顶部图片
        mixlist.getHeaderView().setImageResource(R.drawable.glide1);
        RetrofitUtils.postHomePage(this,mixlist.getHeaderView());
        mixlist.getHeaderView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        backBtn = findViewById(R.id.id_toolbar).findViewById(R.id.iv_back);
        postAddBtn=findViewById(R.id.id_toolbar).findViewById(R.id.add_post);
    }

    public void InData() {
        data = new ArrayList<>();
        String token= SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"token");
        adapterData = new MixListAdapter(this, data);
        MixShowActivity.mixlist.setAdapter(adapterData);

        ZLoadingDialog dialog=DialogUtils.getZLoadingDialog(this);
        PostRequest postRequest=new PostRequest(token,String.valueOf(page));
        RetrofitUtils.postAllPost(MixShowActivity.this,postRequest,data,dialog);

    }

    /**
     * 添加点击监听器
     */
    private void AddClickListener(){
        //返回按钮监听
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page=1;
                adapterData=null;
                finish();
            }
        });
        //写说说按钮监听
        postAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MixShowActivity.this,PostAddActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
