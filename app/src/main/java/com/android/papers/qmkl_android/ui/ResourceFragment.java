package com.android.papers.qmkl_android.ui;


import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.activity.WebViewActivity;
import com.android.papers.qmkl_android.impl.PostFile;
import com.android.papers.qmkl_android.model.FileRes;
import com.android.papers.qmkl_android.requestModel.FileRequest;

import com.android.papers.qmkl_android.util.PaperFileUtils;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;

/**
 * A simple {@link Fragment} subclass.
 * 主页面四个tab之一: 资源页面
 */
public class ResourceFragment extends Fragment {

    //为方便将Fragment在Tag中改为Activity,方便LogCat的过滤
    private static final String Tag = "ResourceActivityTag";

    //文件总数据
    private FileRes mData;

    //数据适配器
    private AcademyAdapter mAdapter;

    private ImageView uploadImg;

    //地址变化
    private String Basepath = "/";

    private int index = 0;//1表示主界面，2表示2级界面


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
        System.out.println("initData");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resource, container, false);
        ButterKnife.bind(this, view);


        //TODO 上传资源按钮
        uploadImg = view.findViewById(R.id.uploadImage_Academy);
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

        //TODO 点击文件夹后的操作
        mAdapter = new AcademyAdapter();
        lvAcademy.setAdapter(mAdapter);
        lvAcademy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(), FileFolderActivity.class);
//                String folder = mData.getData().get(position);
//                System.out.println("/"+folder+"/");
//                intent.putExtra("folder", folder);
//                startActivity(intent);
                final String folder = mData.getData().get(position);
                ptrFrame.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        loadPaperData(folder);//指定文件夹路径
                        ptrFrame.refreshComplete();
                    }
                },100);
            }
        });

        //下拉刷新
        //StoreHouse风格的头部实现
        final StoreHouseHeader header = new StoreHouseHeader(getActivity());
        //显示相关工具类，用于获取用户屏幕宽度、高度以及屏幕密度。同时提供了dp和px的转化方法。
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, 0);
        header.initWithString("finalExam");
        header.setTextColor(R.color.black);
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
                        Basepath = "/";
                        loadPaperData( null);//全部文件夹
                        ptrFrame.refreshComplete();
                    }
                },1000);
            }
        });
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
              ptrFrame.autoRefresh();
            }
        }, 100);

        return view;
    }

    //加载文件数据
    private void loadPaperData(String folder) {
        if(folder != null){
            Basepath += folder;
            Basepath += "/";
            System.out.println(Basepath);
        }
        System.out.println("正在加载文件资源");
        String token = SharedPreferencesUtils.getStoredMessage(Objects.requireNonNull(this.getContext()),"token");
        System.out.println(token);
        String collegeName = "福州大学";
        final int errorCode = 404;
        final int successCode = 200;
        if(token!=null){
            //创建Retrofit对象
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(this.getContext().getString(R.string.base_url))// 设置 网络请求 Url,1.0.0版本
                    .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                    .build();

            //创建 网络请求接口 的实例
            PostFile request = retrofit.create(PostFile.class);

            //对 发送请求 进行封装
            FileRequest fileRequest = new FileRequest(Basepath,collegeName,token);
            Call<FileRes> call = request.getCall(fileRequest);

            //发送网络请求(异步)
            call.enqueue(new Callback<FileRes>() {
                //请求成功时回调
                @Override
                public void onResponse(@NonNull Call<FileRes> call, @NonNull Response<FileRes> response) {
                    int resultCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                    mData = response.body();
                    if(resultCode == errorCode){
                        System.out.println("文件请求失败");
                    }else if (resultCode == successCode){
                        System.out.println("文件请求成功");
                        handler.sendEmptyMessage(1);
                    }else{
                        System.out.println("文件请求发生未知错误");
                    }
                }
                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<FileRes> call, @NonNull Throwable t) {
                    System.out.println( "文件资源请求失败");
                }
            });
        }
        else{
            //TODO 跳转回登陆界面
            System.out.println("请重新登陆");
        }
    }

    //handler为线程之间通信的桥梁
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:  //根据上面的提示，当Message为1，表示数据处理完了，可以通知主线程了
                    mAdapter.notifyDataSetChanged();        //这个方法一旦调用，UI界面就刷新了
                    break;

                default :
                    break;
            }
        }

    };

    private class AcademyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mData == null) {
                return 0;
            }
            return mData.getData().size();
        }

        @Override
        public Object getItem(int position) {
            return mData.getData().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            String folderName = mData.getData().get(position);
            //通过下面的条件判断语句，来循环利用。如果convertView = null ，表示屏幕上没有可以被重复利用的对象。
            if (convertView == null) {

                convertView = View.inflate(getActivity(), R.layout.lv_item_folder, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //从Data中取出数据填充到ListView列表项中
            //创建View


            holder.tvFolderName.setText(folderName);
            holder.imgFolderIcon.setImageDrawable(getResources().getDrawable(PaperFileUtils.parseImageResource(PaperFileUtils.typeWithFileName(folderName))));

            return convertView;
        }
    }

    static class ViewHolder {
        @BindView(R.id.tv_folder_name)
        TextView tvFolderName;
        @BindView(R.id.img_folder_icon)
        ImageView imgFolderIcon;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

