package com.android.papers.qmkl_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.TokenLoginRequest;
import com.android.papers.qmkl_android.util.CountDownTimer;
import com.android.papers.qmkl_android.util.SDCardUtils;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//第二启动页 (放广告的页面 缺省)
public class AdsActivity extends Activity {

    private static final int errorCode=404;
    private static final int successCode = 200;
    private static final String TAG = "AdsActivity";
    private String newAdName;
    private boolean isLogin =false;

    boolean isClicked=false,isSkip=false;
    @BindView(R.id.iv_ad)
    ImageView ivAd;
    @BindView(R.id.skip)
    Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        ButterKnife.bind(this);

    //设置窗体全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        newAdName = SharedPreferencesUtils.getStoredMessage(getApplicationContext(), "AdName");

        File adImageFile = new File(SDCardUtils.getADImage(newAdName));

        if (adImageFile.exists()) {
            ivAd.setImageURI(Uri.fromFile(adImageFile));
        } else  {
            getSharedPreferences("AppConfig", MODE_PRIVATE).edit().putInt("ad_version", 0);
        }

        ivAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked=true;
                Log.d(TAG, "点击广告");
                Intent intent=new Intent(AdsActivity.this,AdsDetailsActivity.class);
                intent.putExtra("url",SharedPreferencesUtils.getStoredMessage(getApplicationContext(), "fallback"));
                intent.putExtra("title",SharedPreferencesUtils.getStoredMessage(getApplicationContext(),"adtitle"));
                startActivity(intent);
                finish();
            }
        });

        new Thread(new CountDownTimer(3,skip)).start();
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "点击跳过");
                isSkip=true;
                if(SharedPreferencesUtils.getStoredMessage(getApplication(),"hasLogin").equals("false")){
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
                        if(isSkip==false && isClicked==false){
                            if(SharedPreferencesUtils.getStoredMessage(getApplication(),"hasLogin").equals("false")){
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

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

}
