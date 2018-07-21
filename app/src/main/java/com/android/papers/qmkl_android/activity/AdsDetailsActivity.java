package com.android.papers.qmkl_android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.requestModel.TokenLoginRequest;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdsDetailsActivity extends Activity {

    private static final int errorCode=404;
    private static final int successCode = 200;
    private static final String TAG = "AdsDetailsActivity";
    private boolean isLogin =false;
    private String adUrl;
    @BindView(R.id.ads_webview)
    WebView ads_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_ads);
        ButterKnife.bind(this);

        adUrl = SharedPreferencesUtils.getStoredMessage(getApplicationContext(), "fallback");

        ads_webview.getSettings().setJavaScriptEnabled(true);
        ads_webview.loadUrl(adUrl);
        /*网页*/
        ads_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http:") || url.startsWith("https:") ) {
                    view.loadUrl(url);
                    return false;
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        postLogin(getApplication(),SharedPreferencesUtils.getStoredMessage(getApplication(),"token"));
    }


    public void nextActivity(Class clazz) {
        final Intent intent = new Intent(AdsDetailsActivity.this, clazz);
        startActivity(intent);
        finish();
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
