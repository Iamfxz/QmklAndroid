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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.android.papers.qmkl_android.activity.LoginActivity;
import com.android.papers.qmkl_android.db.DownloadDB;
import com.android.papers.qmkl_android.impl.PostFile;
import com.android.papers.qmkl_android.impl.PostFileDetail;
import com.android.papers.qmkl_android.impl.PostFileUrl;
import com.android.papers.qmkl_android.model.FileDetailRes;
import com.android.papers.qmkl_android.model.FileRes;
import com.android.papers.qmkl_android.model.FileUrlRes;
import com.android.papers.qmkl_android.model.PaperFile;
import com.android.papers.qmkl_android.requestModel.FileRequest;
import com.android.papers.qmkl_android.umengUtil.umengApplication.UMapplication;
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

import static com.android.papers.qmkl_android.util.ConstantUtils.LOGIN_INVALID;
import static com.android.papers.qmkl_android.util.ConstantUtils.SERVER_FILE_ERROR;
import static com.android.papers.qmkl_android.util.ConstantUtils.SERVER_REQUEST_FAILURE;

/**
 * A simple {@link Fragment} subclass.
 * 主页面四个tab之一: 资源页面
 */
public class ResourceFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener, AbsListView.OnScrollListener {

    //文件总数据
    private FileRes mData;
    private List<String> list;//文件名列表
    private boolean isFirst = true;//第一次刷新数据

    //记录上次滚动之后的第一个可见item和最后一个item
    int mFirstVisibleItem = -1;
    int mLastVisibleItem = -1;

    //数据适配器
    private FileAdapter mAdapter;

    //地址变化
    private String BasePath = "/";
    private StringBuffer path;//最终请求路径

    //请求结果
    final int errorCode = 404;
    final int successCode = 200;
    final int tokenInvalidCode = 301;

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
        mAdapter = new FileAdapter();
        lvFolder.setAdapter(mAdapter);
        lvFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //不加载动画
                mFirstVisibleItem = -1;
                mLastVisibleItem = -1;
                if (CommonUtils.isFastDoubleClick()) {
                    //当快速点击时候，弹出1s的动画 TODO 可否使用锁的方式达到数据同步？
                    doZLoadingDailog();
                } else {
                    list = new ArrayList<>(mData.getData().keySet());
                    final String folder = list.get(position);
                    //点击的是文件夹
                    if (PaperFileUtils.typeWithFileName(folder).equals("folder")) {
                        loadPaperData(folder, loadFolder, collegeName);//指定文件夹路径
                    } else {
                        loadPaperData(folder, loadFile, collegeName);//点击的是具体某个可以下载的文件
                        doZLoadingDailog();
                        System.out.println("你点击了：" + folder);
                    }
                    //返回顶部
                    lvFolder.setSelection(0);
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
                System.out.println("正在刷新主页面");
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
                }, 1000);
            }
        });
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.autoRefresh();
            }
        }, 100);

        lvFolder.setOnScrollListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //五个悬浮按钮，从上往下
        FloatingActionButton fabUpload = view.findViewById(R.id.fab11);
        FloatingActionButton fabChangeSchool = view.findViewById(R.id.fab12);
        FloatingActionButton fabRefresh = view.findViewById(R.id.fab13);
        FloatingActionButton fabReturnTop = view.findViewById(R.id.fab14);
        FloatingActionButton fabReturnBottom = view.findViewById(R.id.fab15);
        FloatingActionButton fabPreviousMenu = view.findViewById(R.id.fab16);

        //悬浮菜单及按钮监听
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(getContext(), "fabUpload Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        fabChangeSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(getContext(), "fabChangeSchool Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ptrFrame.autoRefresh();
            }
        });

        fabReturnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvFolder.setSelection(0);
            }
        });

        fabReturnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvFolder.setSelection(mAdapter.getCount());
            }
        });

        fabPreviousMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path.toString().equals("/"))
                    Toast.makeText(getContext(), "当前已经是根目录了", Toast.LENGTH_SHORT).show();
                loadPaperData(null, loadPreviousFolder, collegeName);//返回上级文件夹
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
        searchView.setFocusable(true);
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
                } else {
                    Toast.makeText(getContext(), "找不到您搜索的《" + query + "》课程或文件", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchView.setSuggestions(mData.getData().keySet().toArray(new String[mData.getData().keySet().size()]));
                searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String query = (String) parent.getItemAtPosition(position);
                        searchView.setQuery(query, true);
                        searchView.closeSearch();
                    }
                });
                return false;
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
            ViewPropertyAnimator animator = view.animate().setDuration(500)
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
     * @param requestCode 3——加载具体文件；
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
                BasePath += folder;
                BasePath += "/";
                path = new StringBuffer(BasePath);
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
                if (!path.toString().equals("/"))
                    folder = path.substring(PaperFileUtils.last2IndexOf(path.toString()) + 1, path.lastIndexOf("/"));
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
                path = new StringBuffer(BasePath);
                break;
            default:
                path = new StringBuffer(BasePath);
                break;
        }
        System.out.println("当前路径：" + path);

        //设置页面标题
        if (path.toString().equals("/"))
            title.setText(collegeName);
        else if (requestCode == loadFolder || requestCode == loadPreviousFolder)
            title.setText(folder);

        if (folder == null || PaperFileUtils.typeWithFileName(folder).equals("folder")) {
//            System.out.println("正在加载文件夹资源");
            String token = SharedPreferencesUtils.getStoredMessage(Objects.requireNonNull(this.getContext()), "token");
            if (token != null) {
                //创建Retrofit对象
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(this.getContext().getString(R.string.base_url))// 设置 网络请求 Url,1.0.0版本
                        .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
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
                            //请求成功
                            handler.sendEmptyMessage(1);
                        }else if(resultCode == errorCode ){
                            handler.sendEmptyMessage(4);
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
     * 单个item的视图绑定
     */
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
     * 加载动画
     */
    private void doZLoadingDailog() {
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
                        PaperFile paperFile = new PaperFile(path, size, Objects.requireNonNull(response.body()));

                        //查询数据库是否已经下载过
                        paperFile.setDownload(DownloadDB.getInstance(getContext()).isDownloaded(path));

                        Intent intent = new Intent(getActivity(), FileDetailActivity.class);
                        intent.putExtra("FileDetail", paperFile);
                        startActivity(intent);
                    } else if (resultCode == tokenInvalidCode){
                        handler.sendEmptyMessage(3);
                    }else {
                        System.out.println("文件详细信息返回码无法解析");
                    }
                }

                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<FileDetailRes> call, @NonNull Throwable t) {
                    //TODO
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
                        handler.sendEmptyMessage(4);
                    } else if (resultCode == successCode) {
                        String url;
                        url = Objects.requireNonNull(response.body()).getData().getUrl();
                        //存储路径为path的文件的url,方便后面获取
                        SharedPreferencesUtils.setStoredMessage(getContext(), path, url);
                    } else if(resultCode == tokenInvalidCode){
                        handler.sendEmptyMessage(3);
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

    private class FileAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            int size = 0;
            if (mData == null) {
                return size;
            }
            try {
                size = mData.getData().keySet().size();
            }catch (Exception e){
                e.printStackTrace();
            }
            return size;

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

            mItemViewHolder holder;
            list = new ArrayList<>(mData.getData().keySet());
            String folderName = list.get(position);
            //通过下面的条件判断语句，来循环利用。如果convertView = null ，表示屏幕上没有可以被重复利用的对象。
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.lv_item_folder, null);
                holder = new mItemViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (mItemViewHolder) convertView.getTag();
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

    static class mItemViewHolder {
        @BindView(R.id.tv_folder_name)
        TextView tvFolderName;
        @BindView(R.id.img_folder_icon)
        ImageView imgFolderIcon;
        @BindView(R.id.tv_folder_size)
        TextView tvFolderSize;
        @BindView(R.id.img_folder_arrow)
        ImageView imgFolderArrow;

        mItemViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     *      进入下一个Activity
     * @param clazz 活动类名
     */
    public void nextActivity(Class clazz) {
        final Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
        try {
            Objects.requireNonNull(getActivity()).finish();
        }catch (Exception e){
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
                    mAdapter.notifyDataSetChanged();//UI界面就刷新
                    result = true;
                    break;
                case 2:
                    //请求失败回调
                    nextActivity(LoginActivity.class);
                    Toast.makeText(getContext(),SERVER_REQUEST_FAILURE,Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    //登陆失效
                    nextActivity(LoginActivity.class);
                    Toast.makeText(getContext(),LOGIN_INVALID,Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    //服务器获取结果为404
                    nextActivity(LoginActivity.class);
                    Toast.makeText(getContext(),SERVER_FILE_ERROR,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return result;
        }
    });
}