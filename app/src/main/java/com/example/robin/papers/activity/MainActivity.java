package com.example.robin.papers.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robin.papers.R;
import com.example.robin.papers.ui.DownloadedFragment;
import com.example.robin.papers.ui.ResourceFragment;
import com.example.robin.papers.ui.StudentsCircleFragment;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.example.robin.papers.util.ActivityManager;
import com.example.robin.papers.util.CircleDrawable;
import com.example.robin.papers.util.SDCardUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;
import com.example.robin.papers.util.ConstantUtils;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import ezy.boost.update.ICheckAgent;
import ezy.boost.update.IUpdateChecker;
import ezy.boost.update.IUpdateParser;
import ezy.boost.update.OnFailureListener;
import ezy.boost.update.UpdateError;
import ezy.boost.update.UpdateInfo;
import ezy.boost.update.UpdateManager;
import ezy.boost.update.UpdateUtil;

import static com.example.robin.papers.util.CommonUtils.isFastDoubleClick;
import static com.example.robin.papers.util.ConstantUtils.mCheckUrl;


/**
 * 登录后的主界面
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";

    //底部的tab控件
    private FragmentTabHost mTabHost;
    private LayoutInflater mLayoutInflater;
    public static Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    NavigationView navigationView;
    View navHeaderView;
    LinearLayout userInfo;
    public static CircleImageView headImg;
    public static TextView userName;
    public static TextView userCollegeName;
    //4个切换的页面的fragment.
    private Class mFragmentArray[] = {
            ResourceFragment.class,//资源页面
            DownloadedFragment.class,//已下载页面
            StudentsCircleFragment.class,//学生圈界面
//            DiscoveryFragment.class//发现界面
    };


    //tab栏图标
    private int mImageArray[] = {
            R.drawable.tab_resource,
            R.drawable.tab_downloaded,
            R.drawable.tab_students,
//            R.drawable.tab_discovery
    };

    //tab栏的字
    private String mTextArray[] = {"资源", "已下载", "趣聊", "发现"};
    private static Boolean isExit = false; //是否退出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityManager.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView(getApplicationContext());

        //设置顶部工具栏
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //设置侧滑
        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        toggle.setDrawerIndicatorEnabled(false);

        Log.d("头像路径", SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(getApplicationContext(), "avatar")));

        //获取头像文件，先转化为100*100的drawable文件
        Drawable drawable = UMapplication.getContext().getResources().getDrawable(R.drawable.menu);
//        CircleDrawable circleDrawable = new CircleDrawable(UMapplication.getContext().getResources().getDrawable(R.drawable.academy), this, 44);

        toolbar.setNavigationIcon(drawable);


        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);

        //引入header和menu
        navigationView.inflateHeaderView(R.layout.nav_header);
        navigationView.inflateMenu(R.menu.nav_menu);

        navigationView.setItemIconTintList(null);

        //设置menu的监听事件
        navigationView.setNavigationItemSelectedListener(this);

        //获取头部布局
        navHeaderView = navigationView.getHeaderView(0);

        //初始化头像等内容
        userInfo = navHeaderView.findViewById(R.id.user_info);
        headImg = navHeaderView.findViewById(R.id.head_img);
        userName = navHeaderView.findViewById(R.id.user_name);
        userCollegeName = navHeaderView.findViewById(R.id.user_college_name);
        Drawable drawable2 = Drawable.createFromPath(SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(getApplicationContext(), "avatar")));
        headImg.setImageDrawable(drawable2);
        userName.setText(SharedPreferencesUtils.getStoredMessage(this, "nickname"));
        userCollegeName.setText(SharedPreferencesUtils.getStoredMessage(this, "college"));

        //因为修改默认按钮，这个图标的点击事件会消失，点击图标不能打开侧边栏，所以添加点击事件
        //设置点击事件，点击弹出menu界面
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //刷新头像等信息
                Drawable drawable = Drawable.createFromPath(SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(getApplicationContext(), "avatar")));
                headImg.setImageDrawable(drawable);
                userName.setText(SharedPreferencesUtils.getStoredMessage(view.getContext(), "nickname"));
                userCollegeName.setText(SharedPreferencesUtils.getStoredMessage(view.getContext(), "college"));
                drawer.openDrawer(GravityCompat.START);
            }
        });

        //设置监听事件
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                startActivity(intent);

            }
        });


        //自动检查是否需要更新
        if(Build.VERSION.SDK_INT >= 26){
            //有无更新权限
            boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if(haveInstallPermission){
                UpdateSetting(false);
            }else {
                Toast.makeText(UMapplication.getContext(),"安装应用需要打开未知来源权限，请去设置中开启权限",Toast.LENGTH_LONG).show();
            }
        }else {
            UpdateSetting(false);
        }
    }


    /**
     * isManual hasUpdate isForce isSilent isIgnorable
     * 检查是否需要更新
     * check(true, true, false, false, true, 998);
     * 不能忽视的更新
     * check(true, true, false, false, false, 998);
     * 强制更新
     * check(true, true, true, false, true, 998);
     * 没有新版本更新
     * check(true, false, false, false, true, 998);
     * 检查更新静默
     * check(true, true, false, true, true, 998);
     *
     *                 isManual    是否手动检查更新(有无提示)
     *                 hasUpdate   是否有新版本
     *                 isForce     是否强制安装：不安装无法使用app
     *                 isSilent    是否静默下载：有新版本时不提示直接下载,
     *                 只能为false，否则java.lang.NoClassDefFoundError，
     *                 从 com.android.support:support-compat:27.0.0 开始，
     *                 NotificationCompat 已经从 v7 移动到 v4 了
     *                 isIgnorable 是否已经忽略版本
     */
    void UpdateSetting(boolean isManual) {
        //版本更新设置
        UpdateManager.setDebuggable(true);
        UpdateManager.setWifiOnly(false);
        UpdateManager.create(this)
                .setUrl(mCheckUrl+getVersioncode())
                .setManual(isManual)
                .setParser(new IUpdateParser() {
            @Override
            public UpdateInfo parse(String source) throws Exception {
                UpdateInfo info;
                try{
                    Gson gson = new Gson();
                    info = gson.fromJson(source, UpdateInfo.class);
                }catch (Exception e){
                    throw new Exception(e);
                }
                Log.e("UpdateInfo" , source);
                info.isSilent = false;//强制为false,框架坏了，无法静默更新
                if (getVersioncode() >= info.versionCode) {
                    info.hasUpdate = false;
                    info.isAutoInstall = false;
                }
                Log.e("ezy.update versionCode",getVersioncode()+"----" + info.versionCode);
                Log.e("ezy.update hasUpdate", String.valueOf(info.hasUpdate));
                Log.e("ezy.update versionCode", String.valueOf(info.versionCode));
                Log.e("ezy.update AutoInstall", String.valueOf(info.isAutoInstall));
                Log.e("ezy.update md5", String.valueOf(info.md5));
                Log.e("ezy.update isForce", String.valueOf(info.isForce));
                Log.e("ezy.update isIgnorable", String.valueOf(info.isIgnorable));
                Log.e("ezy.update url", String.valueOf(info.url));
                return info;
            }
        }).check();
    }

    /**
     * 初始化主界面的视图
     */
    private void initView(Context context) {
        mLayoutInflater = LayoutInflater.from(this);

        //设置底部tab控件
        mTabHost = findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.real_tab_content);
        mTabHost.getTabWidget().setShowDividers(0);

        int count = mFragmentArray.length;
        for (int i = 0; i < count; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextArray[i])
                    .setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_background);
        }
    }

    private View getTabItemView(int index) {
        @SuppressLint("InflateParams") View view = mLayoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageArray[index]);
        return view;
    }

    private int getVersioncode() {
        PackageManager packageManager = MainActivity.this.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = 0;
        try {
            packageInfo = packageManager.getPackageInfo(MainActivity.this.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e(TAG, String.valueOf(versionCode));
        return versionCode;
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

        if (getVisibleFragment() instanceof ResourceFragment && navigationView.getVisibility() == View.INVISIBLE) {
            ((ResourceFragment) getVisibleFragment()).onKeyDown(keyCode);
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (navigationView.getVisibility() == View.VISIBLE) {
                //当左边的菜单栏是可见的，则关闭
                drawer.closeDrawer(navigationView);
            } else {
                exitBy2Click();
            }
        }

        return true;
    }


    /**
     * @return 当前显示的fragment
     */
    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    //双击返回键退出app
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出期末考啦", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            Log.d("退出", "退出期末考啦");
            ActivityManager.AppExit(getApplicationContext());
        }
    }

    //设置侧滑页面的监听事件
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.my_dynamic:
                if(item.isChecked()){
                    item.setChecked(false);
                }else {
                    item.setChecked(true);
                    if(!isFastDoubleClick()){
                        Toast.makeText(MainActivity.this,"趣聊板块正在紧张刺激的开发当中，敬请期待~",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.my_collection:
                if(item.isChecked()){
                    item.setChecked(false);
                }else {
                    item.setChecked(true);
                    if(!isFastDoubleClick()){
                        Toast.makeText(MainActivity.this,"趣聊板块正在紧张刺激的开发当中，敬请期待~",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.my_comment:
                if(item.isChecked()){
                    item.setChecked(false);
                }else {
                    item.setChecked(true);
                    if(!isFastDoubleClick()){
                        Toast.makeText(MainActivity.this,"趣聊板块正在紧张刺激的开发当中，敬请期待~",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.check_update:
                if(item.isChecked()){
                    item.setChecked(false);
                }else {
                    item.setChecked(true);
                    if(!isFastDoubleClick()){
                        if(Build.VERSION.SDK_INT >= 26){
                            //有无更新权限
                            boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                            if(haveInstallPermission){
                                UpdateSetting(true);
                            }else {
                                Toast.makeText(UMapplication.getContext(),"安装应用需要打开未知来源权限，请去设置中开启权限",Toast.LENGTH_LONG).show();
                            }
                        }else {
                            UpdateSetting(true);
                        }
                    }
                }
                break;
            case R.id.feedback:
                if(item.isChecked()){
                    item.setChecked(false);
                }else {
                    item.setChecked(true);
                    if(!isFastDoubleClick()){
                        intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", "http://cn.mikecrm.com/LGpy5Kn");
                        intent.putExtra("title", "意见反馈");
                        startActivity(intent);
                    }
                }
                break;
            case R.id.join_us:
                if(item.isChecked()){
                    item.setChecked(false);
                }else {
                    item.setChecked(true);
                    if(!isFastDoubleClick()){
                        intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", "http://cn.mikecrm.com/6lMhybb");
                        intent.putExtra("title", "加入我们");
                        startActivity(intent);
                    }
                }
                break;

        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
