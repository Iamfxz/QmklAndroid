package com.example.robin.papers.studentCircle.studentCircleActivity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.robin.papers.R;
import com.example.robin.papers.model.CollectionListData;
import com.example.robin.papers.requestModel.PostRequest;
import com.example.robin.papers.studentCircle.adapter.CollectionListAdapter;
import com.example.robin.papers.studentCircle.model.CollectionInfo;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.view.CollectionListView;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.example.robin.papers.util.DialogUtils;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyCollectionActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView backBtn;
    public static int page=1;
    public static CollectionListView collectionList;
    public static ArrayList<Mixinfo> data;
    public static CollectionListAdapter adapterData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycollection);
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
        collectionList=findViewById(R.id.collection_list);
        data = new ArrayList<Mixinfo>();
        adapterData = new CollectionListAdapter(this, data);
        collectionList.setAdapter(adapterData);
        String token= SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"token");
        PostRequest postRequest=new PostRequest(token,String.valueOf(page));

        ZLoadingDialog dialog= DialogUtils.getZLoadingDialog(this);
        RetrofitUtils.postGetCollection(this,postRequest,data,dialog);
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
