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
    public static  RelativeLayout bottomView;
    private EditText editText;
    private ImageView backBtn;
    private TextView postAddBtn;
    private int height_top = 0;
    private int keyHeight = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            Log.d(TAG, "page= "+page);
        setContentView(R.layout.activity_mix_show);
        findID();
        InData();
        AddToolbar();
        AddClickListener();
        keyHeight = (int) Height / 3;
    }



    @Override
    protected void findID() {
        super.findID();
        mixlist = (PullToZoomListView) findViewById(R.id.mixlist);
        mixlist.getHeaderView().setImageResource(R.drawable.glide1);
        mixlist.getHeaderView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        bottomView = (RelativeLayout) findViewById(R.id.bottomView);
        editText = (EditText) findViewById(R.id.editText);
        mixlist.addOnLayoutChangeListener(new LayoutChangeListener());
        backBtn = (ImageView) findViewById(R.id.id_toolbar).findViewById(R.id.iv_back);
        postAddBtn=findViewById(R.id.id_toolbar).findViewById(R.id.add_post);
    }

    public void InData() {
        data = new ArrayList<Mixinfo>();
        String token= SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"token");
        adapterData = new MixListAdapter(this, data);
        MixShowActivity.mixlist.setAdapter(adapterData);
        //使用com.zyao89:zloading:1.1.2引用別人的加载动画
        ZLoadingDialog dialog = new ZLoadingDialog(this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                .setHintText("Loading")
                .setCanceledOnTouchOutside(false)
                .show();
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



    private class LayoutChangeListener implements View.OnLayoutChangeListener {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                //软键盘弹起
            } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                //软键盘消失
                bottomView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
