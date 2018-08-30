package com.example.robin.papers.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.robin.papers.R;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.example.robin.papers.util.ActivityManager;
import com.example.robin.papers.util.CountDownTimer;
import com.example.robin.papers.util.SDCardUtils;
import com.example.robin.papers.util.SharedPreferencesUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

//第二启动页 (放广告的页面 缺省)
public class AdsActivity extends Activity {

    private static final String TAG = "AdsActivity";

    //点击广告图片，点击跳过按钮
    boolean isClicked=false,isSkip=false;
    @BindView(R.id.iv_ad)
    ImageView ivAd;
    @BindView(R.id.skip)
    Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载布局，活动压入堆栈
        ActivityManager.addActivity(this);
        setContentView(R.layout.activity_ads);
        ButterKnife.bind(this);

        //设置窗体全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //根据广告名称获取广告地址
        String newAdName = SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "AdName");
        File adImageFile = new File(SDCardUtils.getADImage(newAdName));

        //如果广告地址存在则加载，否则广告版本设置为0
        if (adImageFile.exists()) {
            ivAd.setImageURI(Uri.fromFile(adImageFile));
        } else  {
            getSharedPreferences("AppConfig", MODE_PRIVATE).edit().putInt("ad_version", 0).apply();
        }

        //广告图片点击处理
        ivAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked=true;
                Log.d(TAG, "点击广告"+SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "fallback"));
                Intent intent=new Intent(AdsActivity.this,WebViewActivity.class);
                intent.putExtra("url",SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "fallback"));
                intent.putExtra("title",SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"adtitle"));
                startActivity(intent);
                finish();
            }
        });

        //广告页面跳过按钮点击处理，停留3秒
        new Thread(new CountDownTimer(3,skip,0)).start();
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "点击跳过");
                isSkip=true;
                if(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"hasLogin").equals("false")){
                    nextActivity(LoginActivity.class);
                }
                else {
                    nextActivity(MainActivity.class);
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!isSkip && !isClicked){
                            //未登陆设置进入登陆页面，其他进入程序
                            if(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(),"hasLogin").equals("false")){
                                nextActivity(LoginActivity.class);
                            }
                            else {
                                nextActivity(MainActivity.class);
                            }
                        }
                    }
                });
            }
        }).start();

    }


    /**
     *      进入下一个Activity
     * @param clazz 活动类名
     */
    public void nextActivity(Class clazz) {
        final Intent intent = new Intent(AdsActivity.this, clazz);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
