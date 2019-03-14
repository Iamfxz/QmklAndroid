package com.example.robin.papers.studentCircle.studentCircleActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.robin.papers.R;
import com.example.robin.papers.requestModel.PostRequest;
import com.example.robin.papers.studentCircle.adapter.CollectionListAdapter;
import com.example.robin.papers.studentCircle.adapter.DynamicListAdapter;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.view.CollectionListView;
import com.example.robin.papers.studentCircle.view.DynamicListView;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.example.robin.papers.util.DialogUtils;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDynamicActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView backBtn;
    public static int page=1;
    public static DynamicListView dynamicList;
    public static ArrayList<Mixinfo> data;
    public static DynamicListAdapter adapterData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydynamic);
        ButterKnife.bind(this);
        InData();
        //返回按钮监听
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page=1;
                adapterData=null;
                finish();
            }
        });

    }

    public void InData() {
        dynamicList=findViewById(R.id.dynamic_list);
        data = new ArrayList<Mixinfo>();
        adapterData = new DynamicListAdapter(this, data);
        dynamicList.setAdapter(adapterData);
        String token= SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"token");
        PostRequest postRequest=new PostRequest(token,String.valueOf(page));

        ZLoadingDialog dialog= DialogUtils.getZLoadingDialog(this);
        RetrofitUtils.postGetMyDynamic(this,postRequest,data,dialog);
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
