package com.example.robin.papers.ui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robin.papers.R;
import com.example.robin.papers.activity.FileDetailActivity;
import com.example.robin.papers.activity.LoginActivity;
import com.example.robin.papers.activity.UpLoadActivity;
import com.example.robin.papers.activity.UserInfoActivity;
import com.example.robin.papers.db.DownloadDB;
import com.example.robin.papers.impl.PostAllColleges;
import com.example.robin.papers.impl.PostFile;
import com.example.robin.papers.impl.PostFileDetail;
import com.example.robin.papers.impl.PostFileUrl;
import com.example.robin.papers.model.AcademiesOrCollegesRes;
import com.example.robin.papers.model.FileDetailRes;
import com.example.robin.papers.model.FileRes;
import com.example.robin.papers.model.FileUrlRes;
import com.example.robin.papers.model.PaperFile;
import com.example.robin.papers.requestModel.FileRequest;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.example.robin.papers.util.CircleDrawable;
import com.example.robin.papers.util.ConstantUtils;
import com.example.robin.papers.util.PaperFileUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;
import com.example.robin.papers.util.ToastUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.promeg.pinyinhelper.Pinyin;
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.jaren.lib.view.LikeView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.umeng.analytics.MobclickAgent;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.robin.papers.util.CommonUtils.isFastDoubleClick;
import static com.example.robin.papers.util.ConstantUtils.DEFAULT_TIMEOUT;

/**
 * A simple {@link Fragment} subclass.
 * 主页面四个tab之一: 资源页面
 */
public class ResourceFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener, AbsListView.OnScrollListener {

    final static String TAG = "ResourceFragment";
    private static String mPackageName = "com.example.robin.papers.ui.ResourceFragment";
    //文件总数据
    private FileRes mData;
    private List<String> list;//文件名列表，有点乱，还需要理清楚 TODO
    private boolean isFirst = true;//第一次刷新数据

    //记录上次滚动之后的第一个可见item和最后一个item
    int mFirstVisibleItem = -1;
    int mLastVisibleItem = -1;

    //数据适配器
    private FileAdapter mAdapter;

    //地址变化
    private String BasePath = "/";//"/"表示主页面
    private StringBuffer path;//最终请求路径

    //请求结果
    final int errorCode = 404;//请求错误
    final int successCode = 200;//请求成功
    final int tokenInvalidCode = 301;//登陆失效
    final int normalErrorCode = 202;//常规错误，可以直接抛出msg给用户

    //loadPaperData()方法的请求码，含义看函数头注释
    final int loadFolder = 0;
    final int loadPreviousFolder = 1;
    final int loadRefresh = 2;
    final int loadFile = 3;
    final int loadMainFolder = 4;

    //学校名称
    private String collegeName;

    //搜索框，开源框架，github地址https://github.com/MiguelCatalan/MaterialSearchView
    private MaterialSearchView searchView;
    MenuItem searchItem;

    //侧边快捷搜索条
    WaveSideBar sideBar;

    //显示学校名称或当前所在文件夹
    private TextView title;
    //下拉选择学校
    private ImageView chooseSchool;

    //是否退出程序，连续点两次返回则退出
    private static Boolean isExit = false;

    //判断应不应该有列表加载动画
    boolean shouldAnimate = false;

    //用于存放用户上次在主界面的位置
    int lastPosition = 0;
    //成功记录用户上次在主界面的位置
    boolean recordSuccess = false;
    //收藏文件的数目
    int countStore = 0;
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

        initOnCreateView();


        // 为fragement加载布局
        View view = inflater.inflate(R.layout.fragment_resource, container, false);
        ButterKnife.bind(this, view);

        Log.d(TAG, SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "hasLogin"));
        initView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //五个悬浮按钮，从上往下
        FloatingActionButton fabUserInfo = view.findViewById(R.id.fab10);
        FloatingActionButton fabUpload = view.findViewById(R.id.fab11);
        FloatingActionButton fabReturnToMain = view.findViewById(R.id.fab13);
        FloatingActionButton fabPreviousMenu = view.findViewById(R.id.fab16);

        //悬浮菜单及按钮监听
        fabUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserInfoActivity.class);
                startActivity(intent);
            }
        });
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpLoadActivity.class));
            }
        });
        fabReturnToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasePath = "/";
                loadPaperData(null, loadMainFolder, collegeName);//返回主页面
            }
        });
        fabPreviousMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path.toString().equals("/"))
                    Toast.makeText(getContext(), "当前已经是主目录了", Toast.LENGTH_SHORT).show();
                loadPaperData(null, loadPreviousFolder, collegeName);//返回上级文件夹
            }
        });

        sideBar = Objects.requireNonNull(getActivity()).findViewById(R.id.side_bar);
        sideBar.setIndexItems("#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
                "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
        sideBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String index) {
                int section;
                int position = 0;
//                Log.d("WaveSideBar", index);
                if (index.charAt(0) >= 'A' && index.charAt(0) <= 'Z') {
                    section = index.charAt(0) - 'A';
                    position = mAdapter.getPositionForSection(section);
                }
                lvFolder.setSelection(position);
            }
        });
    }


    private void initView() {
        //设置学校名称
        title = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar).findViewById(R.id.title);
        collegeName = SharedPreferencesUtils.getStoredMessage(Objects.requireNonNull(this.getContext()), "college");
        title.setText(collegeName);
        chooseSchool = getActivity().findViewById(R.id.toolbar).findViewById(R.id.choose_school);
        chooseSchool.setVisibility(View.VISIBLE);
        setChooseSchoolListener();

        //文件列表设置
        mAdapter = new FileAdapter();
        lvFolder.setAdapter(mAdapter);
        lvFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //不加载动画
                mFirstVisibleItem = -1;
                mLastVisibleItem = -1;
//                list = new ArrayList<>(mData.getData().keySet());

                final String folder = list.get(position);

                if (searchView.isSearchOpen()) {
                    searchView.closeSearch();
                }

                //点击的是文件夹 TODO 添加在加载数据时候的加载动画
                if (PaperFileUtils.typeWithFileName(folder).equals("folder")) {
                    loadPaperData(folder, loadFolder, collegeName);//指定文件夹路径
                } else {
                    if (!isFastDoubleClick()) {
                        loadPaperData(folder, loadFile, collegeName);//点击的是具体某个可以下载的文件
                    }
                }

            }
        });

        //下拉刷新,StoreHouse风格的头部实现
        final StoreHouseHeader header = new StoreHouseHeader(getActivity());
        isFirst = true;

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
//                System.out.println("正在刷新主页面");
                ptrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isFirst) {
                            BasePath = "/";
                            isFirst = false;
                            loadPaperData(null, loadMainFolder, collegeName);//主界面文件夹
                        } else {
                            loadPaperData(null, loadRefresh, collegeName);//刷新页面
                        }
                        ptrFrame.refreshComplete();
                    }
                }, 0);
            }
        });
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.autoRefresh();
            }
        }, 250);

        lvFolder.setOnScrollListener(this);
    }

    //初始化界面时对标题栏做的一些准备工作
    private void initOnCreateView() {
        RelativeLayout layout = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar_layout);
        layout.setPadding(CircleDrawable.dip2px(Objects.requireNonNull(getContext()), 40), 0, 0, 0);
        Log.d(TAG, "CircleDrawable.dip2px(getContext(),40)=" + CircleDrawable.dip2px(getContext(), 40));
    }

    public void setChooseSchoolListener() {

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZLoadingDialog dialog = new ZLoadingDialog(v.getContext());
                dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                        .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                        .setHintText("loading...")
                        .setCanceledOnTouchOutside(false)
                        .show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.dialog);

                postAllColleges(builder, title, dialog);
            }
        });

        chooseSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZLoadingDialog dialog = new ZLoadingDialog(v.getContext());
                dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                        .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                        .setHintText("loading...")
                        .setCanceledOnTouchOutside(false)
                        .show();

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.dialog);

                postAllColleges(builder, title, dialog);

            }
        });

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //listview第一次载入时，两者都为-1
        shouldAnimate = (mFirstVisibleItem != -1) && (mLastVisibleItem != -1);
        //滚动时最后一个item的位置
        int lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
        if (shouldAnimate) {//第一次不需要加载动画
            int indexAfterFist = 0;
            //如果出现这种情况，说明是在向上scroll，如果scroll比较快的话，一次可能出现多个新的view，我们需要用循环
            //去获取所有这些view，然后执行动画效果
            while (firstVisibleItem + indexAfterFist < mFirstVisibleItem) {
                View animateView = view.getChildAt(indexAfterFist);//获取item对应的view
                doAnimate(animateView);
                indexAfterFist++;
            }

            int indexBeforeLast = 0;
            //向下scroll, 情况类似，只是计算view的位置时不一样
            while (lastVisibleItem - indexBeforeLast > mLastVisibleItem) {
                View animateView = view.getChildAt(lastVisibleItem - indexBeforeLast - firstVisibleItem);
                doAnimate(animateView);
                indexBeforeLast++;
            }
        }

        mFirstVisibleItem = firstVisibleItem;
        mLastVisibleItem = lastVisibleItem;
    }

    /**
     * 菜单栏
     *
     * @param menu     菜单
     * @param inflater LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化；作用类似于findViewById()
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Objects.requireNonNull(getActivity()).getMenuInflater().inflate(R.menu.fragment_resource_menu, menu);

        //搜索框架的相关设置
        searchView = getActivity().findViewById(R.id.search_view);
        searchItem = menu.findItem(R.id.search_item);
        searchView.setMenuItem(searchItem);
        searchView.setBackground(new ColorDrawable(Objects.requireNonNull(getContext()).getResources().getColor(R.color.bar_color)));
        searchView.setVoiceSearch(false);
        searchView.setEllipsize(true);
        searchView.setFocusable(false);
        searchView.setHint("课程名称或文件名称");
        //设置可搜索的内容
//        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (queryIsExist(query)) {
                    Toast.makeText(getContext(), "您搜索的是《" + query + "》", Toast.LENGTH_SHORT).show();
                    if (PaperFileUtils.typeWithFileName(query).equals("folder"))
                        loadPaperData(query, loadFolder, collegeName);//加载文件夹
                    else
                        loadPaperData(query, loadFile, collegeName);//加载具体文件
                    searchView.closeSearch();
                } else if (lvFolder.getCount() > 0) {
                    String defaultQuery = lvFolder.getItemAtPosition(0).toString();//默认搜索第一个
                    if (queryIsExist(defaultQuery)) {
                        if (PaperFileUtils.typeWithFileName(defaultQuery).equals("folder"))
                            loadPaperData(defaultQuery, loadFolder, collegeName);//加载文件夹
                        else
                            loadPaperData(defaultQuery, loadFile, collegeName);//加载具体文件
                    }
                    Toast.makeText(getContext(), "您最终搜索的是《" + defaultQuery + "》", Toast.LENGTH_SHORT).show();
                    searchView.closeSearch();
                } else {
                    Toast.makeText(getContext(), "找不到您搜索的《" + query + "》课程或文件", Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                searchView.setSuggestions(mData.getData().keySet().toArray(new String[mData.getData().keySet().size()]));
                if (mAdapter != null) {
                    Filter filter = mAdapter.getFilter();
                    if (newText == null || newText.length() == 0) {
                        filter.filter(null);
                    } else {
                        filter.filter(newText);
                    }
                }
                return true;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
//                searchView.setAdapter(mAdapter);
                lvFolder.setSelection(0);
//                ptrFrame.autoRefresh();
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    /**
     * 列表的加载动画
     *
     * @param view 视图
     */
    private void doAnimate(View view) {
        //动画，GROW
        try {
            ViewPropertyAnimator animator = view.animate().setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator());
            view.setPivotX(view.getWidth() / 2);
            view.setPivotY(view.getHeight() / 2);
            view.setScaleX(0.01f);
            view.setScaleY(0.01f);

            animator.scaleX(1.0f).scaleY(1.0f);
            animator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求并加载文件资源，主要用于主界面的资源页面
     *
     * @param folder      "/"表示请求主界面所有文件 "/cad/"表示请求其中的cad文件夹，以此类推
     * @param requestCode 4——加载主页面文件；
     *                    3——加载具体文件；
     *                    2——刷新当前页面;
     *                    1——用于回退到上一个文件夹；
     *                    0——加载folder
     * @param collegeName 学校名字，用于判断获取哪个学校的文件列表
     *                    <p>
     *                    区分BasePath和path：
     *                    前者是当前的地址，后者是改变后的地址
     */
    private void loadPaperData(String folder, final int requestCode, String collegeName) {

        switch (requestCode) {
            case loadFolder:
                //加载文件夹folder，需要改变BasePath的地址
                if(BasePath.equals("/")){
                    lastPosition = lvFolder.getFirstVisiblePosition();
                }
                BasePath += folder;
                BasePath += "/";
                path = new StringBuffer(BasePath);
                chooseSchool.setVisibility(View.GONE);
                break;
            case loadPreviousFolder:
                //回退前
                if (!path.toString().equals("/"))
                    BasePath = path.substring(0, PaperFileUtils.last2IndexOf(path.toString()) + 1);
                else
                    BasePath = path.toString();
                //回退后地址
                path = new StringBuffer(BasePath);
                //回退后所在文件夹folder
                if (!path.toString().equals("/")){
                    folder = path.substring(PaperFileUtils.last2IndexOf(path.toString()) + 1, path.lastIndexOf("/"));
                    recordSuccess = false;
                }
                else{//回到主界面
                    chooseSchool.setVisibility(View.VISIBLE);
                    recordSuccess = true;
                }
                break;
            case loadRefresh:
                //刷新
                path = new StringBuffer(BasePath);
                break;
            case loadFile:
                //加载具体的文件file，不需要以"/"结尾，BasePath不变
                path = new StringBuffer(BasePath + folder);
                break;
            case loadMainFolder:
                //加载主界面
                chooseSchool.setVisibility(View.VISIBLE);
                path = new StringBuffer(BasePath);
                break;
            default:
                path = new StringBuffer(BasePath);
                break;
        }
        System.out.println("当前路径：" + path);

        //设置页面标题
        if (path.toString().equals("/")) {
            title.setText(collegeName);
//            searchItem.setVisible(true);
        } else if (requestCode == loadFolder || requestCode == loadPreviousFolder) {
            title.setText(folder);
//            searchItem.setVisible(false);
        }


        if (folder == null || PaperFileUtils.typeWithFileName(folder).equals("folder")) {
//            System.out.println("正在加载文件夹资源");
            String token = SharedPreferencesUtils.getStoredMessage(Objects.requireNonNull(this.getContext()), "token");
            if (token != null) {
                //创建Retrofit对象
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .retryOnConnectionFailure(false)
                        .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(this.getContext().getString(R.string.base_url))// 设置 网络请求 Url,1.0.0版本
                        .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                        .client(okHttpClient)
                        .build();

                //创建 网络请求接口 的实例
                final PostFile request = retrofit.create(PostFile.class);

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
                        if (resultCode == tokenInvalidCode) {
                            //登陆信息失效
                            handler.sendEmptyMessage(3);

                        } else if (resultCode == successCode) {
                            //请求数据成功
                            handler.sendEmptyMessage(1);
                        } else if (resultCode == errorCode) {
                            handler.sendEmptyMessage(4);
                        } else if (resultCode == normalErrorCode) {
                            Message message = Message.obtain();
                            message.obj = Objects.requireNonNull(response.body()).getMsg();
                            message.what = 5;
                            handler.sendMessage(message);
                        } else {
                            handler.sendEmptyMessage(6);
                        }
                    }

                    //请求失败时回调
                    @Override
                    public void onFailure(@NonNull Call<FileRes> call, @NonNull Throwable t) {
                        handler.sendEmptyMessage(2);
                    }
                });
            } else {
                //token为空，登陆失效
                handler.sendEmptyMessage(3);
            }
        } else {//如果是某个具体文件，则应该使用这个请求获得url地址
            postFileUrl(path.toString(), collegeName);
            postFileDetail(path.toString(), collegeName);
        }

    }


    /**
     * 判断query是否在当前目录中
     *
     * @param query 说需要查找的字符串
     * @return 查找结果，true代表有
     */
    private boolean queryIsExist(String query) {
        String queryFolder = query.trim();
        Set<String> strings = mData.getData().keySet();
        for (String key : strings) {
            if (key.equals(queryFolder))
                return true;
        }
        return false;
    }


    /**
     * 处理返回按键事件
     *
     * @param keyCode 事件代码
     */
    public void onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();//关闭搜索框
            } else if (path.toString().equals("/")) {
                exitBy2Click();
            } else {
                loadPaperData(null, loadPreviousFolder, collegeName);//返回上级文件夹
            }
        }
    }

    /**
     * 双击返回键退出app
     */
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(this.getContext(), "再点一次可以退出app", Toast.LENGTH_SHORT).show();
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
     * 请求具体可以下载文件的大小等数据，例如某word文件
     *
     * @param path        文件路径
     * @param collegeName 文件所属大学名字
     */
    private void postFileDetail(final String path, final String collegeName) {
        String token = SharedPreferencesUtils.getStoredMessage(Objects.requireNonNull(this.getContext()), "token");
        if (token != null) {
            //创建Retrofit对象
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(false)
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(this.getContext().getString(R.string.base_url))// 设置 网络请求 Url,1.0.0版本
                    .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                    .client(okHttpClient)
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
//                    System.out.println("文件详细信息请求结果：" + resultCode);
                    if (resultCode == errorCode) {
                        handler.sendEmptyMessage(4);
//                        System.out.println(Objects.requireNonNull(response.body()).getMsg());
                    } else if (resultCode == successCode) {
//                        System.out.println("文件详细信息请求成功");

                        String size = Objects.requireNonNull(response.body()).getData().getSize();
                        PaperFile paperFile = new PaperFile(path, size, Objects.requireNonNull(response.body()));

                        //查询数据库是否已经下载过
                        paperFile.setDownload(DownloadDB.getInstance(getContext()).isDownloaded(path));

                        Intent intent = new Intent(getActivity(), FileDetailActivity.class);
                        intent.putExtra("FileDetail", paperFile);
                        startActivity(intent);
                    } else if (resultCode == tokenInvalidCode) {
                        handler.sendEmptyMessage(3);
                    } else if (resultCode == normalErrorCode) {
                        Message message = Message.obtain();
                        message.obj = Objects.requireNonNull(response.body()).getMsg();
                        message.what = 5;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(6);
//                        System.out.println("文件详细信息返回码无法解析");
                    }
                }

                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<FileDetailRes> call, @NonNull Throwable t) {
                    handler.sendEmptyMessage(2);
                }
            });
        } else {
            //token为空重新登录
            handler.sendEmptyMessage(3);
        }
    }

    /**
     * 请求具体可以下载文件的url地址，用于下载，例如某word文件
     *
     * @param path        文件路径
     * @param collegeName 文件所属大学名字
     */
    private void postFileUrl(final String path, final String collegeName) {
        String token = SharedPreferencesUtils.getStoredMessage(Objects.requireNonNull(this.getContext()), "token");
        if (token != null) {
            //创建Retrofit对象
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(false)
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(this.getContext().getString(R.string.base_url))// 设置 网络请求 Url,1.0.0版本
                    .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                    .client(okHttpClient)
                    .build();

            //创建 网络请求接口 的实例
            final PostFileUrl request = retrofit.create(PostFileUrl.class);

            //对 发送请求 进行封装(账号和密码)
            Call<FileUrlRes> call = request.getCall(new FileRequest(path, collegeName, token));

            //发送网络请求(异步)
            call.enqueue(new Callback<FileUrlRes>() {
                //请求成功时回调
                @Override
                public void onResponse(@NonNull Call<FileUrlRes> call, @NonNull Response<FileUrlRes> response) {
                    int resultCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
//                    System.out.println("文件URL请求结果" + resultCode);
                    if (resultCode == errorCode) {
                        handler.sendEmptyMessage(4);
                    } else if (resultCode == successCode) {
                        String url;
                        url = Objects.requireNonNull(response.body()).getData().getUrl();
                        //存储路径为path的文件的url,方便后面获取
                        SharedPreferencesUtils.setStoredMessage(getContext(), path, url);
                    } else if (resultCode == tokenInvalidCode) {
                        handler.sendEmptyMessage(3);
                    } else if (resultCode == normalErrorCode) {
                        Message message = Message.obtain();
                        message.obj = Objects.requireNonNull(response.body()).getMsg();
                        message.what = 5;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(6);
                    }
                }

                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<FileUrlRes> call, @NonNull Throwable t) {
                    handler.sendEmptyMessage(2);
                }
            });
        } else {
            handler.sendEmptyMessage(3);
        }
    }

    /**
     * 文件信息界面获取学校信息
     *
     * @param builder  选择对话框实例
     * @param textView 页面标题
     * @param dialog   加载动画
     */
    public void postAllColleges(final AlertDialog.Builder builder, final TextView textView, final ZLoadingDialog dialog) {
        //创建Retrofit对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantUtils.BaseUrl)// 设置 网络请求 Url,1.0.0版本
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .client(okHttpClient)
                .build();

        //监听返回键，返回则取消加载动画
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface DialogInterface, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return false;
            }
        });
        PostAllColleges request = retrofit.create(PostAllColleges.class);
        Call<AcademiesOrCollegesRes> call = request.getCall();
        call.enqueue(new Callback<AcademiesOrCollegesRes>() {

            @Override
            public void onResponse(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull final Response<AcademiesOrCollegesRes> response) {
                int resultCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
//                System.out.println("look at here:" + resultCode);
                if (resultCode == ConstantUtils.SUCCESS_CODE) {
                    ConstantUtils.colleges = Objects.requireNonNull(response.body()).getData();
                    // 设置参数
                    TextView tv = new TextView(getActivity());
                    tv.setText(ConstantUtils.CHOOSE_COLLEGE);    //内容
                    tv.setTextColor(Color.BLACK);//颜色
                    tv.setTextSize(20);

                    tv.setPadding(30, 15, 10, 10);//位置
                    builder.setCustomTitle(tv);//不是setTitle()

                    builder.setItems(ConstantUtils.colleges, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            collegeName = Objects.requireNonNull(response.body()).getData()[which];
                            textView.setText(collegeName);
                            SharedPreferencesUtils.setStoredMessage(Objects.requireNonNull(getContext()), "college", collegeName);
//                            System.out.println("look at here:" + collegeName);
                            loadPaperData(null, loadMainFolder, collegeName);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    //触摸外部可以取消
//                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                } else if (resultCode == tokenInvalidCode) {
                    handler.sendEmptyMessage(3);
                } else if (resultCode == normalErrorCode) {
                    Message message = Message.obtain();
                    message.obj = Objects.requireNonNull(response.body()).getMsg();
                    message.what = 5;
                    handler.sendMessage(message);
                } else if (resultCode == errorCode) {
                    handler.sendEmptyMessage(4);
                } else {
                    handler.sendEmptyMessage(6);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull Throwable t) {
                Log.d(TAG, "请求失败");
                Toast.makeText(UMapplication.getContext(), ConstantUtils.SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    private class FileAdapter extends BaseAdapter implements Filterable, SectionIndexer {

        private final Object mLock = new Object();//锁住数据

        // A copy of the original mObjects array, initialized from and then used instead as soon as
        // the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
        private ArrayList<String> mOriginalValues;//用于保存原始数据
        private ArrayFilter mFilter;//过滤器

        @Override
        public int getCount() {
            int size = 0;
            if (list == null) {
                return size;
            }
            try {
                size = list.size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final mItemViewHolder holder;
            //通过下面的条件判断语句，来循环利用。如果convertView = null ，表示屏幕上没有可以被重复利用的对象。
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.lv_item_folder, null);
                holder = new mItemViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (mItemViewHolder) convertView.getTag();
            }

            //监听点击收藏按钮
            if(BasePath.equals("/")){
                if(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), collegeName+getItem(position).toString()) != null){
                    //设置为收藏
                    holder.imgSticked.setVisibility(View.VISIBLE);
                    holder.imgUnsticked.setVisibility(View.GONE);
                }else {
                    //设置为未收藏
                    holder.imgSticked.setVisibility(View.GONE);
                    holder.imgUnsticked.setVisibility(View.VISIBLE);
                }
                //取消收藏
                holder.imgSticked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.imgSticked.getVisibility() == View.VISIBLE){
                            holder.imgSticked.setVisibility(View.GONE);
                            holder.imgUnsticked.setVisibility(View.VISIBLE);
                            //取消收藏设置
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), collegeName+getItem(position).toString(),null);
                            //自定义显示时间
                            @SuppressLint("ShowToast") Toast myToast = Toast.makeText(UMapplication.getContext(),"取消收藏"+getItem(position).toString(),Toast.LENGTH_LONG);
                            ToastUtils.showMyToast(myToast,1500);
                            handler.sendEmptyMessage(1);//更新界面
                        }
                    }
                });

                //添加收藏
                holder.imgUnsticked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.imgUnsticked.getVisibility() == View.VISIBLE){
                            holder.imgSticked.setVisibility(View.VISIBLE);
                            holder.imgUnsticked.setVisibility(View.GONE);
                            //存储收藏信息，存储数据结构为键值对例如<"福州大学安全管理","1">
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(),collegeName+getItem(position).toString(),"1");
                            //自定义显示时间
                            @SuppressLint("ShowToast") Toast myToast = Toast.makeText(UMapplication.getContext(),"收藏"+getItem(position).toString(),Toast.LENGTH_LONG);
                            ToastUtils.showMyToast(myToast,1500);
                            //收藏项置顶
                            String item = list.get(position);
                            list.remove(position);
                            list.add(0,item);
                            handler.sendEmptyMessage(1);//更新界面
                        }
                    }
                });
            }else{
                holder.imgSticked.setVisibility(View.GONE);
                holder.imgUnsticked.setVisibility(View.GONE);
            }

            //从Data中取出数据填充到ListView列表项中
            holder.tvFolderName.setText(list.get(position));
            holder.imgFolderIcon.setImageDrawable(getResources().getDrawable(PaperFileUtils.parseImageResource(PaperFileUtils.typeWithFileName(list.get(position)))));

            if (!PaperFileUtils.typeIsFolder(list.get(position))) {
                holder.tvFolderSize.setText(mData.getData().get(list.get(position)));
                holder.tvFolderSize.setVisibility(View.VISIBLE);
                holder.imgFolderArrow.setVisibility(View.INVISIBLE);
            } else {
                holder.tvFolderSize.setVisibility(View.INVISIBLE);
                holder.imgFolderArrow.setVisibility(View.VISIBLE);
            }

            //设置26个字母头和#
            if(position == 0 && countStore != 0){
                holder.tvFolderHead.setVisibility(View.VISIBLE);
                holder.tvFolderHead.setText("★我的收藏");
                sideBar.setIndexItems("★", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
                        "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
            } else if (position == countStore && getPositionForSection(getSectionForPosition(position)) == -1){
                holder.tvFolderHead.setVisibility(View.VISIBLE);
                holder.tvFolderHead.setText("#");
                if(position == 0){
                    sideBar.setIndexItems("#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
                            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
                }
            } else if (position == getPositionForSection(getSectionForPosition(position))) {
                holder.tvFolderHead.setVisibility(View.VISIBLE);

                //把section index转化为大写字母
                char c = (char) (getSectionForPosition(position) + 65);
                if (c >= 'A' && c <= 'Z') {
                    holder.tvFolderHead.setText(String.valueOf(c));
                }
            }else {
                holder.tvFolderHead.setVisibility(View.GONE);
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new ArrayFilter();
            }
            return mFilter;
        }

        @Override
        public Object[] getSections() {
            return null;
        }

        //关键方法，通过section index获取在ListView中的位置
        @Override
        public int getPositionForSection(int arg0) {
            //根据参数arg0，加上65后得到对应的大写字母
            char c = (char) (arg0 + 65);
            //循环遍历ListView中的数据，遇到第一个首字母为上面的就是要找的位置
            for (int i = 0; i < getCount(); i++) {
                if(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),collegeName+list.get(i)) != null){
                    continue;
                }
                if (Pinyin.toPinyin(list.get(i), "").toUpperCase().charAt(0) == c) {
                    return i;
                }
            }
            return -1;//找不到返回-1
        }

        //关键方法，通过在ListView中的位置获取Section index
        @Override
        public int getSectionForPosition(int arg0) {
            //获取该位置的名首字母
            try {
                char c = Pinyin.toPinyin(list.get(arg0), "").toUpperCase().charAt(0);
                //如果该字母在A和Z之间，则返回A到Z的索引，从0到25
                if (c >= 'A' && c <= 'Z') {
                    return c - 'A';
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //如果首字母不是A到Z的字母，则返回26，该类型将会被分类到#下面
            return 26;
        }

        /**
         * 前缀过滤
         * TODO 修改成中文模糊匹配
         */
        private class ArrayFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence query) {
                FilterResults results = new FilterResults();
//                System.out.println("performFiltering");
                // 保存原始数据
                if (mOriginalValues == null) {
                    synchronized (mLock) {
                        mOriginalValues = new ArrayList<>(mData.getData().keySet());
                    }
                }

                //如果为空则回复原始数据,否则开始过滤
                if (query == null || query.length() == 0) {
                    ArrayList<String> originalList;
                    synchronized (mLock) {
                        originalList = new ArrayList<>(mData.getData().keySet());
                    }
                    results.values = originalList;
                    results.count = originalList.size();
                } else {
                    //要搜索的内容转化为小写,去除空格
                    String queryString = query.toString().replace(" ", "").toLowerCase();
                    //转化为中文拼音且小写，去除空格
                    String queryPinyin = Pinyin.toPinyin(query.toString().replace(" ", ""), "").toLowerCase();

                    ArrayList<String> values;
                    synchronized (mLock) {
                        values = new ArrayList<>(mData.getData().keySet());//拷贝数据并上锁
                    }

                    final int count = values.size();
                    final ArrayList<String> newValues = new ArrayList<>();

                    for (int i = 0; i < count; i++) {
                        final String value = values.get(i);//单个数据项的名字
                        final String valuePinyin = Pinyin.toPinyin(value, "").toLowerCase();//转化为拼音用于搜索

                        // 搜索匹配算法
                        if (value.equals(queryString)) {//完全匹配
                            newValues.add(value);
                            break;
                        } else if (valuePinyin.equals(queryPinyin)) {//拼音完全匹配
                            newValues.add(value);
                            break;
                        } else if (value.contains(queryString)) {//判断是否包含搜索字符串的中文
                            newValues.add(value);
                        } else if (valuePinyin.contains(queryPinyin)) {//判断是否包含搜索字符串的拼音
                            newValues.add(value);
                        }

                    }

                    results.values = newValues;
                    results.count = newValues.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                list = (List<String>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }

    static class mItemViewHolder {
        @BindView(R.id.tv_folder_name)
        TextView tvFolderName;
        @BindView(R.id.img_folder_icon)
        ImageView imgFolderIcon;
        @BindView(R.id.tv_folder_size)
        TextView tvFolderSize;
        @BindView(R.id.img_folder_arrow)
        ImageView imgFolderArrow;
        @BindView(R.id.tv_folder_head)
        TextView tvFolderHead;
        @BindView(R.id.sticked)
        ImageView imgSticked;
        @BindView(R.id.unsticked)
        ImageView imgUnsticked;


        mItemViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 进入下一个Activity
     *
     * @param clazz 活动类名
     */
    public void nextActivity(Class clazz) {
        try {
            final Intent intent = new Intent(getActivity(), clazz);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * handler为线程之间通信的桥梁
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean result = false;
            switch (msg.what) {
                //根据上面的提示，当Message为1，表示数据处理完了，可以通知主线程了
                case 1:
                    //获取数据成功
                    if (mData != null) {
                        mData.sort();
                    }
                    list = new ArrayList<>(mData.getData().keySet());
                    list = setStoreItem(list);
                    //如果加载数据完成自动返回上次位置（仅限主列表）
                    if(recordSuccess){
                        System.out.println("recordSuccess:"+recordSuccess);
                        lvFolder.setAdapter(mAdapter);
                        lvFolder.setSelection(lastPosition);
                        recordSuccess = false;
                    }else {
                        lvFolder.setSelection(0);
                    }
                    mAdapter.notifyDataSetChanged();//UI界面就刷新
                    result = true;
                    break;
                case 2:
                    //请求失败回调
                    nextActivity(LoginActivity.class);
                    Toast.makeText(getContext(), ConstantUtils.SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    //登陆失效
                    nextActivity(LoginActivity.class);
                    Toast.makeText(getContext(), ConstantUtils.LOGIN_INVALID, Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    //服务器获取结果为404
                    nextActivity(LoginActivity.class);
                    Toast.makeText(getContext(), ConstantUtils.SERVER_FILE_ERROR, Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    //202常规异常 TODO 未测试
                    Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                case 6:
                    //未知异常
                    nextActivity(LoginActivity.class);
                    Toast.makeText(getContext(), ConstantUtils.UNKNOWN_ERROR, Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
            return result;
        }
    });

    private List<String> setStoreItem(List<String> list) {
        countStore = 0;
        for(int i = 0; i<list.size();i++){
            if(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),collegeName+list.get(i)) != null){
                countStore++;
                String temp = list.get(i);
                list.remove(i);
                list.add(0,temp);
            }
        }

        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPackageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        //如果当前页面对用户不可见则关闭搜索框
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        shouldAnimate = false;
        MobclickAgent.onPageEnd(mPackageName);
    }
}