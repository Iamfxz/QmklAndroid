package com.android.papers.qmkl_android.ui;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.activity.FileDetailActivity;
import com.android.papers.qmkl_android.db.DownloadDB;
import com.android.papers.qmkl_android.impl.PostFile;
import com.android.papers.qmkl_android.impl.PostFileDetail;
import com.android.papers.qmkl_android.impl.PostFileUrl;
import com.android.papers.qmkl_android.model.FileDetailRes;
import com.android.papers.qmkl_android.model.FileRes;
import com.android.papers.qmkl_android.model.FileUrlRes;
import com.android.papers.qmkl_android.model.PaperFile;
import com.android.papers.qmkl_android.requestModel.FileRequest;
import com.android.papers.qmkl_android.util.CommonUtils;
import com.android.papers.qmkl_android.util.PaperFileUtils;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;
import com.github.clans.fab.FloatingActionButton;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
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

/**
 * A simple {@link Fragment} subclass.
 * 主页面四个tab之一: 资源页面
 */
public class ResourceFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener,AbsListView.OnScrollListener{

    //为方便将Fragment在Tag中改为Activity,方便LogCat的过滤
    private static final String TAG = "ResourceActivityTag";

    //文件总数据
    private FileRes mData;
    private List<String> list;//文件名列表

    //记录上次滚动之后的第一个可见item和最后一个item
    int mFirstVisibleItem = -1;
    int mLastVisibleItem = -1;

    //数据适配器
    private FolderAdapter mAdapter;

    //地址变化
    private String BasePath = "/";
    private StringBuffer path;//临时路径

    //请求结果
    final int errorCode = 404;
    final int successCode = 200;

    //学校名称
    private String collegeName;
    private GestureDetector gesture; //手势识别

    //搜索框，开源框架，github地址https://github.com/MiguelCatalan/MaterialSearchView
    private MaterialSearchView searchView;

    //悬浮按钮，开源框架https://github.com/Clans/FloatingActionButton
    private FloatingActionButton fab11;
    private FloatingActionButton fab12;

    //显示学校名称或当前所在文件夹
    private TextView title;

    //是否退出程序，连续点两次返回则退出
    private static Boolean isExit = false;
    /**
     * Butter Knife 用法详见  http://jakewharton.github.io/butterknife/
     */
    @BindView(R.id.lv_folder)
    ListView lvFolder;
    @BindView(R.id.ptr_frame)
    PtrFrameLayout ptrFrame;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 为fragement加载布局
        View view = inflater.inflate(R.layout.fragment_resource, container, false);
        ButterKnife.bind(this, view);

        //设置学校名称
        title = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar).findViewById(R.id.title);
        collegeName = SharedPreferencesUtils.getStoredMessage(Objects.requireNonNull(this.getContext()), "college");
        title.setText(collegeName);

        //文件列表设置
        mAdapter = new FolderAdapter();
        lvFolder.setAdapter(mAdapter);
        lvFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (CommonUtils.isFastDoubleClick()) {
                    //当快速点击时候，弹出1s的动画 TODO 可否使用锁的方式达到数据同步？
                    final ZLoadingDialog dialog = new ZLoadingDialog(Objects.requireNonNull(getContext()));
                    dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                            .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                            .setHintText("Loading...")
                            .setCanceledOnTouchOutside(false);
                    dialog.show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 1000); // 延时1秒
                } else {
                    list = new ArrayList<>(mData.getData().keySet());
                    final String folder = list.get(position);
                    //点击的是文件夹
                    if (PaperFileUtils.typeWithFileName(folder).equals("folder")) {
                        ptrFrame.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadPaperData(folder, 0, collegeName);//指定文件夹路径
                                ptrFrame.refreshComplete();
                            }
                        }, 100);
                    } else {//点击的是具体某个可以下载的文件
                        loadPaperData(folder, 0, collegeName);
                        System.out.println("你点击了：" + folder);
                    }
                }

            }
        });

        //下拉刷新,StoreHouse风格的头部实现
        final StoreHouseHeader header = new StoreHouseHeader(getActivity());

        //显示相关工具类，用于获取用户屏幕宽度、高度以及屏幕密度。同时提供了dp和px的转化方法。
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, 0);
        header.initWithString("finalExam");//刷新时候的字样
        header.setTextColor(R.color.black);
        ptrFrame.setHeaderView(header);
        ptrFrame.addPtrUIHandler(header);
        ptrFrame.setPtrHandler(new PtrHandler() {
            /**
             * 检查是否可以执行下来刷新，比如列表为空或者列表第一项在最上面时。
             */
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // 默认实现，根据实际情况做改动
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            //需要加载数据时触发
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                System.out.println("正在刷新主页面");
                ptrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BasePath = "/";
                        loadPaperData(null, 0, collegeName);//全部文件夹
                        ptrFrame.refreshComplete();
                    }
                }, 1000);
            }
        });
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.autoRefresh();
            }
        }, 100);

        //根据父窗体getActivity()为fragment设置手势识别
        gesture = new GestureDetector(this.getActivity(), new MyOnGestureListener());
        //为fragment添加OnTouchListener监听器
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);//返回手势识别触发的事件
            }
        });

        lvFolder.setOnScrollListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        fab11 = view.findViewById(R.id.fab11);
        fab12 = view.findViewById(R.id.fab12);
        //悬浮菜单及按钮监听
        fab11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"FAB11 Clicked!",Toast.LENGTH_SHORT).show();;
            }
        });
        fab12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"FAB12 Clicked!",Toast.LENGTH_SHORT).show();;
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //listview第一次载入时，两者都为-1
        boolean shouldAnimate = (mFirstVisibleItem != -1) && (mLastVisibleItem != -1);
        //滚动时最后一个item的位置
        int lastVisibleItem = firstVisibleItem + visibleItemCount -1;
        if(shouldAnimate){//第一次不需要加载动画
            int indexAfterFist =0;
            //如果出现这种情况，说明是在向上scroll，如果scroll比较快的话，一次可能出现多个新的view，我们需要用循环
            //去获取所有这些view，然后执行动画效果
            while(firstVisibleItem + indexAfterFist < mFirstVisibleItem){
                View animateView = view.getChildAt(indexAfterFist);//获取item对应的view
                doAnimate(animateView, false);
                indexAfterFist ++;
            }

            int indexBeforeLast = 0;
            //向下scroll, 情况类似，只是计算view的位置时不一样
            while(lastVisibleItem - indexBeforeLast > mLastVisibleItem){
                View animateView = view.getChildAt(lastVisibleItem - indexBeforeLast - firstVisibleItem);
                doAnimate(animateView, true);
                indexBeforeLast ++;
            }
        }

        mFirstVisibleItem = firstVisibleItem;
        mLastVisibleItem = lastVisibleItem;
    }
    private void doAnimate(View view, boolean scrollDown)
    {
        //我们这里先写一个最简单地动画，GROW
        try{
            ViewPropertyAnimator animator = view.animate().setDuration(500)
                    .setInterpolator(new AccelerateDecelerateInterpolator());
            view.setPivotX(view.getWidth()/2);
            view.setPivotY(view.getHeight()/2);
            view.setScaleX(0.01f);
            view.setScaleY(0.01f);

            animator.scaleX(1.0f).scaleY(1.0f);
            animator.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO 设置手势识别监听器，暂时无法使用成功，以后回头来看
    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override//此方法必须重写且返回真，否则onFling不起效
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if ((e1.getX() - e2.getX() > 120) && Math.abs(velocityX) > 200) {
                System.out.println("向左");
                return true;
            } else if ((e2.getX() - e1.getX() > 120) && Math.abs(velocityX) > 200) {
                System.out.println("向右");
                return true;
            }
            return false;
        }
    }



    public void onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();//关闭搜索框
            }else if (path.toString().equals("/")){
                exitBy2Click();
            }else {
                loadPaperData(null, 1, collegeName);//退回上一个文件夹
            }
        }
    }

    //双击返回键退出app
    private void exitBy2Click() {
        Timer tExit = null;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(this.getContext(), "再按一次退出期末考啦", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            Objects.requireNonNull(this.getActivity()).finish();
            System.exit(0);
        }
    }


    /**
     * 请求并加载文件资源，主要用于主界面的资源页面
     *
     * @param folder "/"表示请求主界面所有文件 "/cad/"表示请求其中的cad文件夹，以此类推
     * @param requestCode 1表示用于回退到上一个文件夹；0表示加载folder
     * @param collegeName 学校名字，用于判断获取哪个学校的文件列表
     */
    private void loadPaperData(final String folder, int requestCode, String collegeName) {
        if (requestCode == 1) {
            if (!path.toString().equals("/"))
                BasePath = path.substring(0, PaperFileUtils.last2IndexOf(path.toString()) + 1);
            path = new StringBuffer(BasePath);
        } else {
            if (folder != null) {
                if (!PaperFileUtils.typeWithFileName(folder).equals("folder")) {
                    //这是一个具体的文件，不需要以"/"结尾
                    path = new StringBuffer(BasePath + folder);
                } else {
                    //这是一个文件夹，需要改变BasePath的地址
                    BasePath += folder;
                    BasePath += "/";
                    path = new StringBuffer(BasePath);
                }
            } else {
                path = new StringBuffer(BasePath);
            }
        }
        System.out.println("当前路径：" + path);

        if(!path.toString().equals("/")&& folder!=null){
            if(PaperFileUtils.typeWithFileName(folder).equals("folder"))
                title.setText(folder);
        }else {
            title.setText(collegeName);
        }

        if (folder == null || PaperFileUtils.typeWithFileName(folder).equals("folder")) {
            System.out.println("正在加载文件夹资源");
            String token = SharedPreferencesUtils.getStoredMessage(Objects.requireNonNull(this.getContext()), "token");
            if (token != null) {
                //创建Retrofit对象
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(this.getContext().getString(R.string.base_url))// 设置 网络请求 Url,1.0.0版本
                        .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                        .build();

                //创建 网络请求接口 的实例
                PostFile request = retrofit.create(PostFile.class);

                //对 发送请求 进行封装
                FileRequest fileRequest = new FileRequest(path.toString(), collegeName, token);
                Call<FileRes> call = request.getCall(fileRequest);

                //发送网络请求(异步)
                call.enqueue(new Callback<FileRes>() {
                    //请求成功时回调
                    @Override
                    public void onResponse(@NonNull Call<FileRes> call, @NonNull Response<FileRes> response) {
                        int resultCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                        mData = response.body();
                        if (resultCode == errorCode) {
                            System.out.println("文件请求失败");
                        } else if (resultCode == successCode) {
                            System.out.println("文件请求成功");
                            if(mData != null){
                                mData.sort();
                            }
                            mAdapter.notifyDataSetChanged();
                            handler.sendEmptyMessage(1);
                        } else {
                            System.out.println("文件请求发生未知错误");
                        }
                    }

                    //请求失败时回调
                    @Override
                    public void onFailure(@NonNull Call<FileRes> call, @NonNull Throwable t) {
                        System.out.println("服务器请求失败");
                    }
                });
            } else {
                //TODO 跳转回登陆界面
                System.out.println("请重新登陆");
            }
        } else {//如果是某个具体文件，则应该使用这个请求获得url地址
            postFileUrl(path.toString(), collegeName);
            postFileDetail(path.toString(), collegeName);
        }

    }

    //TODO The content of the adapter has changed but ListView did not receive a notification 闪退BUG
    //handler为线程之间通信的桥梁
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //根据上面的提示，当Message为1，表示数据处理完了，可以通知主线程了
                case 1:
                    //UI界面就刷新
                    break;

                default:
                    break;
            }
        }

    };

    private void postFileDetail(final String path, final String collegeName) {
        String token = SharedPreferencesUtils.getStoredMessage(Objects.requireNonNull(this.getContext()), "token");
        if (token != null) {
            //创建Retrofit对象
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(this.getContext().getString(R.string.base_url))// 设置 网络请求 Url,1.0.0版本
                    .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                    .build();

            //创建 网络请求接口 的实例
            final PostFileDetail request = retrofit.create(PostFileDetail.class);

            //对 发送请求 进行封装(账号和密码)
            Call<FileDetailRes> call = request.getCall(new FileRequest(path, collegeName, token));
            //发送网络请求(异步)
            call.enqueue(new Callback<FileDetailRes>() {
                //请求成功时回调
                @Override
                public void onResponse(@NonNull Call<FileDetailRes> call, @NonNull Response<FileDetailRes> response) {
                    int resultCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                    System.out.println("文件详细信息请求结果：" + resultCode);
                    if (resultCode == errorCode) {
                        System.out.println(Objects.requireNonNull(response.body()).getMsg());
                    } else if (resultCode == successCode) {
                        System.out.println("文件详细信息请求成功");

                        String size = Objects.requireNonNull(response.body()).getData().getSize();
                        Long updateAt = Objects.requireNonNull(response.body()).getData().getUpdateAt();
                        Long createAt = Objects.requireNonNull(response.body()).getData().getCreateAt();
                        PaperFile paperFile = new PaperFile(path, size, updateAt, createAt);
                        //查询数据库是否已经下载过
                        paperFile.setDownload(DownloadDB.getInstance(getContext()).isDownloaded(path));

                        Intent intent = new Intent(getActivity(), FileDetailActivity.class);
                        intent.putExtra("FileDetail", paperFile);
                        startActivity(intent);
                    } else {
                        System.out.println("文件详细信息请求异常");
                    }
                }

                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<FileDetailRes> call, @NonNull Throwable t) {
                    SharedPreferencesUtils.setStoredMessage(getContext(), "hasLogin", "false");
                }
            });
        } else {
            //TODO 重新登陆
            SharedPreferencesUtils.setStoredMessage(getContext(), "hasLogin", "false");
        }
    }

    private void postFileUrl(final String path, final String collegeName) {
        String token = SharedPreferencesUtils.getStoredMessage(Objects.requireNonNull(this.getContext()), "token");
        if (token != null) {
            //创建Retrofit对象
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(this.getContext().getString(R.string.base_url))// 设置 网络请求 Url,1.0.0版本
                    .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                    .build();

            //创建 网络请求接口 的实例
            PostFileUrl request = retrofit.create(PostFileUrl.class);

            //对 发送请求 进行封装(账号和密码)
            Call<FileUrlRes> call = request.getCall(new FileRequest(path, collegeName, token));

            //发送网络请求(异步)
            call.enqueue(new Callback<FileUrlRes>() {
                //请求成功时回调
                @Override
                public void onResponse(@NonNull Call<FileUrlRes> call, @NonNull Response<FileUrlRes> response) {
                    int resultCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                    System.out.println("文件URL请求结果" + resultCode);
                    if (resultCode == errorCode) {
                        System.out.println("文件URL请求失败");
                    } else if (resultCode == successCode) {
                        String url;
                        url = Objects.requireNonNull(response.body()).getData().getUrl();
                        System.out.println("文件URL是" + url);
                        //存储路径为path的文件的url
                        SharedPreferencesUtils.setStoredMessage(getContext(), path, url);
                    } else {
                        System.out.println("文件URL请求异常");
                    }
                }

                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<FileUrlRes> call, @NonNull Throwable t) {
                    SharedPreferencesUtils.setStoredMessage(getContext(), "hasLogin", "false");
                }
            });
        } else {
            //TODO 重新登陆
            SharedPreferencesUtils.setStoredMessage(getContext(), "hasLogin", "false");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private class FolderAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            int size = 0;
            if (mData == null) {
                return size;
            }
<<<<<<< HEAD

            return mData.getData().keySet().size();

=======
            try {
                size = mData.getData().keySet().size();
            }catch (Exception e){
                e.printStackTrace();
            }
            return size;
>>>>>>> 0b187b2aee899faed7b277f076c2188dbc499c26
        }

        @Override
        public Object getItem(int position) {
            return new ArrayList<>(mData.getData().keySet()).get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            list = new ArrayList<>(mData.getData().keySet());
            String folderName = list.get(position);
            //通过下面的条件判断语句，来循环利用。如果convertView = null ，表示屏幕上没有可以被重复利用的对象。
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.lv_item_folder, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //从Data中取出数据填充到ListView列表项中
            holder.tvFolderName.setText(folderName);
            holder.imgFolderIcon.setImageDrawable(getResources().getDrawable(PaperFileUtils.parseImageResource(PaperFileUtils.typeWithFileName(folderName))));
            if (!PaperFileUtils.typeWithFileName(folderName).equals("folder")) {
                holder.tvFolderSize.setText(mData.getData().get(folderName));
                holder.tvFolderSize.setVisibility(View.VISIBLE);
                holder.imgFolderArrow.setVisibility(View.INVISIBLE);
            } else {
                holder.tvFolderSize.setVisibility(View.INVISIBLE);
                holder.imgFolderArrow.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        @BindView(R.id.tv_folder_name)
        TextView tvFolderName;
        @BindView(R.id.img_folder_icon)
        ImageView imgFolderIcon;
        @BindView(R.id.tv_folder_size)
        TextView tvFolderSize;
        @BindView(R.id.img_folder_arrow)
        ImageView imgFolderArrow;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 菜单栏
     *  @param menu     菜单
     * @param inflater 不知如何解释
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Objects.requireNonNull(getActivity()).getMenuInflater().inflate(R.menu.fragment_resource_menu, menu);

        //搜索框架的相关设置
        searchView = getActivity().findViewById(R.id.search_view);
        MenuItem item = menu.findItem(R.id.search_item);
        searchView.setMenuItem(item);
        searchView.setBackground(new ColorDrawable(Objects.requireNonNull(getContext()).getResources().getColor(R.color.bar_color)));
        searchView.setVoiceSearch(false);
        searchView.setEllipsize(true);
        searchView.setHint("课程名称或文件名称");
        //设置可搜索的内容
//        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(queryIsExist(query)){
                    loadPaperData(query,0,collegeName);
                    Toast.makeText(getContext(),"您搜索的是《"+query+"》",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(),"找不到您搜索的《"+query+"》课程或文件",Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchView.setSuggestions(mData.getData().keySet().toArray(new String[mData.getData().keySet().size()]));
                if(queryIsExist(newText)){
                    loadPaperData(newText,0,collegeName);
                    searchView.closeSearch();
                }
                return false;
            }
        });
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Do something when the suggestion list is clicked.
                String suggestion = "C语言";

                searchView.setQuery(suggestion, true);
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });



    }

    /**
     *      判断query是否在当前目录中
     * @param query 说需要查找的字符串
     * @return 查找结果，true代表有
     */
    private boolean queryIsExist(String query) {
        String queryFolder = query.trim();
        Set<String> strings = mData.getData().keySet();
        for (String key: strings){
            if(key.equals(queryFolder))
                return true;
        }
        return false;
    }

}