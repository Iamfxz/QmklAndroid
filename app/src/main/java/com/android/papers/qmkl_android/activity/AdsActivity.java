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
                nextActivity(AdsDetailsActivity.class);
            }
        });

        new Thread(new CountDownTimer(3,skip)).start();
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "点击跳过");
                isSkip=true;
                postLogin(getApplication(),SharedPreferencesUtils.getStoredMessage(getApplication(),"token"));
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
                        if(isClicked==false && isSkip==false){
                            postLogin(getApplication(),SharedPreferencesUtils.getStoredMessage(getApplication(),"token"));
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

    //判断当前token是否可以登录并启动下一启动活动
    public void postLogin(final Context context, String token){
        if(token!=null){
            //创建Retrofit对象
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.base_url))// 设置 网络请求 Url,0.0.4版本
                    .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                    .build();

            //创建 网络请求接口 的实例
            PostLogin request = retrofit.create(PostLogin.class);

            //对 发送请求 进行封装(账号和密码)
            Call<ResponseInfo> call = request.getTokenCall(new TokenLoginRequest(token));

            //发送网络请求(异步)
            call.enqueue(new Callback<ResponseInfo>() {
                //请求成功时回调
                @Override
                public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                    int resultCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                    System.out.println(resultCode);
                    if(resultCode == errorCode){
                        nextActivity(LoginActivity.class);
                    }else if (resultCode == successCode){
                        SharedPreferencesUtils.setStoredMessage(getApplicationContext(),"token",response.body().getData().toString());
                        nextActivity(MainActivity.class);
                    }else{
                        nextActivity(LoginActivity.class);
                    }
                }
                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                    nextActivity(LoginActivity.class);
                }
            });
        }
        else{
            nextActivity(LoginActivity.class);
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

}
