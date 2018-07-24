package com.android.papers.qmkl_android.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.activity.FileFolderActivity;
import com.android.papers.qmkl_android.activity.WebViewActivity;
import com.android.papers.qmkl_android.model.PaperData;
import com.android.papers.qmkl_android.util.LogUtils;
import com.android.papers.qmkl_android.util.RetrofitUtils;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;
import com.android.papers.qmkl_android.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import okhttp3.Request;

/**
 * A simple {@link Fragment} subclass.
 * 主页面四个tab之一: 资源页面
 */
public class ResourceFragment extends Fragment {

    //为方便将Fragment在Tag中改为Activity,方便LogCat的过滤
    private static final String Tag = "ResourceActivityTag";

    //文件总数据
    private PaperData mData;

    //数据适配器
    private AcademyAdapter mAdapter;

    private ImageView uploadImg;


    /**
     * Butter Knife 用法详见  http://jakewharton.github.io/butterknife/
     */
    @BindView(R.id.uploadImage_Academy)
    ImageView uploadImageAcademy;
    @BindView(R.id.lv_academy)
    ListView lvAcademy;
    @BindView(R.id.ptr_frame)
    PtrFrameLayout ptrFrame;

    @OnClick(R.id.uploadImage_Academy)
    public void clickUploadImage() {
        // TODO: 16/5/6 在这里添加打开上传网页
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resource, container, false);
        ButterKnife.bind(this, view);


        //上传资源按钮
        uploadImg = (ImageView) view.findViewById(R.id.uploadImage_Academy);
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转至上传web
                Intent toWebIntent = new Intent(getActivity(), WebViewActivity.class);
                toWebIntent.putExtra("url", "http://robinchen.mikecrm.com/f.php?t=ZmhFim");
                toWebIntent.putExtra("title", "上传你的资源");
                startActivity(toWebIntent);
            }
        });

        //适配器的使用
        mAdapter = new AcademyAdapter();
        lvAcademy.setAdapter(mAdapter);
        lvAcademy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FileFolderActivity.class);
                PaperData.Folders folder = mData.getFolders().get(position);
                intent.putExtra("folder", folder);
                FileFolderActivity.BasePath = mData.getBase();
                startActivity(intent);
            }
        });

        //下拉刷新
        //StoreHouse风格的头部实现
        final StoreHouseHeader header = new StoreHouseHeader(getActivity());
        //显示相关工具类，用于获取用户屏幕宽度、高度以及屏幕密度。同时提供了dp和px的转化方法。
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, 0);
        header.initWithString("finalExam");
        header.setTextColor(R.color.black);
        ptrFrame.setPinContent(true);//刷新时，保持内容不动，仅头部下移,默认,false
        ptrFrame.setHeaderView(header);
        ptrFrame.addPtrUIHandler(header);
        ptrFrame.setPtrHandler(new PtrHandler() {
            /**
             * 检查是否可以执行下来刷新，比如列表为空或者列表第一项在最上面时。
             */
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                System.out.println("MainActivity.checkCanDoRefresh");
                // 默认实现，根据实际情况做改动
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            //需要加载数据时触发
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                System.out.println("ResourceFragement.onRefreshBegin");
                ptrFrame.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        //TODO 在这里使用loadPaperData()函数加载数据
                        loadPaperData();
                        ptrFrame.refreshComplete();
                        //mPtrFrame.autoRefresh();//自动刷新
                    }
                },1000);
            }
        });
        /*ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                //loadPaperData();

                OkHttpClientManager.getAsyn(getResources().getString(R.string.data_url),
                        new OkHttpClientManager.ResultCallback<PaperData>() {

                            @Override
                            public void onError(Request request, Exception e) {
                                ToastUtils.showShort(getActivity(), "获取数据失败,请确认网络连接正常");

                                frame.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        frame.refreshComplete();
                                    }
                                }, 100);
                            }

                            @Override
                            public void onResponse(final PaperData response) {

                                frame.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (response != null) {

                                            if (mData == null || !mData.equals(response)) {

                                                LogUtils.d(Tag, "加载新数据");

                                                mData = response;
                                                mAdapter.notifyDataSetChanged();
                                            } else {
                                                LogUtils.d(Tag, "无最新数据,使用原本数据");
                                            }

                                        }

                                        if (ptrFrame != null) {
                                            ptrFrame.refreshComplete();
                                        }

                                    }
                                }, 1000);

                            }
                        });

            }
        });*/
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
              ptrFrame.autoRefresh();
            }
        }, 100);

        return view;
    }


    //加载文件数据
    private void loadPaperData() {
        System.out.println("正在加载文件资源");
        String token = SharedPreferencesUtils.getStoredMessage(this.getContext(),"token");
        System.out.println(token);
        RetrofitUtils.postFile(this.getContext(),token,
                "/","福州大学");
    }

    //TODO 重写适配器
    private class AcademyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mData == null) {
                return 0;
            }
            return mData.getFolders().size();
        }

        @Override
        public Object getItem(int position) {
            return mData.getFolders().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {

                convertView = View.inflate(getActivity(), R.layout.lv_item_academy, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();

            }

            PaperData.Folders folder = mData.getFolders().get(position);
            holder.tvAcademyName.setText(folder.getName());

            return convertView;
        }
    }

    static class ViewHolder {
        @BindView(R.id.tv_academy_name)
        TextView tvAcademyName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

