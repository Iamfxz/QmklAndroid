package com.example.robin.papers.studentCircle.studentCircleActivity;

import android.os.Bundle;

import com.example.robin.papers.R;
import com.example.robin.papers.model.CollectionListData;
import com.example.robin.papers.requestModel.PostRequest;
import com.example.robin.papers.studentCircle.adapter.CollectionListAdapter;
import com.example.robin.papers.studentCircle.model.CollectionInfo;
import com.example.robin.papers.studentCircle.view.CollectionListView;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.ArrayList;


public class MyCollectionActivity extends BaseActivity {

    public static int page=1;
    public static CollectionListView collectionList;
    public static ArrayList<CollectionInfo> data;
    public static CollectionListAdapter adapterData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycollection);

        InData();

    }

    @Override
    public void InData() {
        collectionList=findViewById(R.id.collection_list);
        data = new ArrayList<CollectionInfo>();
        adapterData = new CollectionListAdapter(this, data);
        collectionList.setAdapter(adapterData);
        String token= SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"token");
        PostRequest postRequest=new PostRequest(token,String.valueOf(page));
        ZLoadingDialog dialog = new ZLoadingDialog(this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                .setHintText("Loading")
                .setCanceledOnTouchOutside(false)
                .show();
        RetrofitUtils.postGetCollection(this,postRequest,data,dialog);
    }
}
