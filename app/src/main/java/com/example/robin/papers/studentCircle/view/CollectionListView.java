package com.example.robin.papers.studentCircle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.robin.papers.R;
import com.example.robin.papers.requestModel.PostRequest;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyCollectionActivity;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;

public class CollectionListView extends ListView implements
        AbsListView.OnScrollListener{
    public  Context context;
    public static View mFooterView;

    public CollectionListView(Context context) {
        super(context);
        this.context=context;
        init(context);
    }

    public CollectionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init(context);
    }

    public CollectionListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init(context);
    }

    private void init(Context context) {
        //初始化底部加载动画
        mFooterView = View.inflate(context, R.layout.view_footer, null);
        super.setOnScrollListener(this);
    }
    @Override
    public void onScroll(AbsListView paramAbsListView, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    }

    public void onScrollStateChanged(AbsListView paramAbsListView, int scrollState) {
        switch (scrollState) {
            // 当不滚动时
            case OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部
                if (paramAbsListView.getLastVisiblePosition() == (paramAbsListView.getCount() - 1)) {
                    if(getFooterViewsCount()==0){
                        MyCollectionActivity.collectionList.addFooterView(mFooterView);
                        String token= SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"token");
                        PostRequest postRequest=new PostRequest(token,String.valueOf(++MyCollectionActivity.page));
                        RetrofitUtils.postGetCollection(context,postRequest,MyCollectionActivity.data,null);
                    }
                }
                break;
        }
    }


}
