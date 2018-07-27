package com.android.papers.qmkl_android.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.papers.qmkl_android.activity.AdsActivity;
import com.android.papers.qmkl_android.activity.FileDetailActivity;
import com.android.papers.qmkl_android.activity.LoginActivity;
import com.android.papers.qmkl_android.activity.MainActivity;
import com.android.papers.qmkl_android.impl.PostAds;
<<<<<<< HEAD
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.model.AdData;
=======
import com.android.papers.qmkl_android.impl.PostFileDetail;
import com.android.papers.qmkl_android.impl.PostFileUrl;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.model.AdData;
import com.android.papers.qmkl_android.model.FileDetailRes;
import com.android.papers.qmkl_android.model.FileRes;
import com.android.papers.qmkl_android.model.FileUrlRes;
import com.android.papers.qmkl_android.model.PaperFile;
>>>>>>> 3a07bad6294a63fffcae0673ec8cfcf56b1328fb
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.LoginRequest;
import com.android.papers.qmkl_android.requestModel.TokenLoginRequest;
import com.zyao89.view.zloading.ZLoadingDialog;

import java.io.File;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitUtils {
    //实例化Retrofit对象
    private static Retrofit retrofit  = new Retrofit.Builder()
            .baseUrl("http://120.77.32.233/qmkl1.0.0/")// 设置 网络请求 Url,1.0.0版本
            .addConverterFactory(GsonConverterFactory.create())//设置使用Gson解析(记得加入依赖)
            .build();
    private static final int errorCode=404;
    private static final int successCode = 200;
    private static final String TAG = ".RetrofitUtils";
    private static String oldAdName,newAdName,adPath;
    //获取广告
    public static void postAd(final Context context, final Activity startAct){


        //创建 网络请求接口 的实例
        PostAds request = retrofit.create(PostAds.class);

        //发送网络请求(异步)
        request.getCall().enqueue(new Callback<ResponseInfo<AdData>>() {
            //请求成功时回调
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<AdData>> call, @NonNull Response<ResponseInfo<AdData>> response) {
                //广告页当前不可用
                if (Integer.parseInt(Objects.requireNonNull(response.body()).getCode())!=successCode||
                        !Objects.requireNonNull(response.body()).getData().isEnabled()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            nextActivity(context,startAct);
                        }
                    }).start();
                    Log.d(TAG, "广告不可用");
                    return;
                }
                //广告页当前可用
                oldAdName = SharedPreferencesUtils.getStoredMessage(context, "AdName");
                newAdName = Objects.requireNonNull(response.body()).getData().getUpdatedAt();
                adPath = Objects.requireNonNull(response.body()).getData().getUrl();
                SharedPreferencesUtils.setStoredMessage(context,"fallback",
<<<<<<< HEAD
                        response.body().getData().getFallback());
                SharedPreferencesUtils.setStoredMessage(context,"adtitle",
                        response.body().getData().getTitle());
                Log.d(TAG, response.body().getData().getTitle());
=======
                        Objects.requireNonNull(response.body()).getData().getFallback());
>>>>>>> 3a07bad6294a63fffcae0673ec8cfcf56b1328fb
                //此前尚未缓存过广告数据、广告数据已更新、广告数据被删除，重新缓存
                if (oldAdName == null || !oldAdName.equals(newAdName) || !checkLocalADImage()) {
                    SharedPreferencesUtils.setStoredMessage(context, "AdName", newAdName);
                    Log.d(TAG, "此前尚未缓存过广告数据或者广告数据已更新，重新缓存");
                    //下载广告页
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d(TAG, "原广告名："+oldAdName);
                                Log.d(TAG, "现广告名："+newAdName);
                                Log.d(TAG, "广告路径："+adPath);
                                Log.d(TAG, "文件路径："+SDCardUtils.getADImage(newAdName));
                                DownLoader.downloadFile(new File(SDCardUtils.getADImage(newAdName)),
                                        adPath);
                                //缓存成功，进入广告页
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        nextActivity(context,startAct,AdsActivity.class);
                                    }
                                }).start();
                            } catch (Exception e) {
<<<<<<< HEAD
                                //缓存失败，进入登录界面或者主界面
                                Toast.makeText(startAct,"缓存广告失败,请反馈给开发者",Toast.LENGTH_SHORT).show();
                                try {
                                    Thread.sleep(2000);
                                    } catch (InterruptedException e2) {
                                        e.printStackTrace();
                                    }
                                    nextActivity(context,startAct);
                             }
=======
                                e.printStackTrace();
                            }
>>>>>>> 3a07bad6294a63fffcae0673ec8cfcf56b1328fb
                        }
                    }).start();
                }
                //广告页已准备就绪，进入广告页
                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            nextActivity(context,startAct,AdsActivity.class);
                        }
                    }).start();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<AdData>> call, @NonNull Throwable t) {
                Toast.makeText(context,"服务器请求失败",Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        nextActivity(context,startAct,LoginActivity.class);
                    }
                }).start();
            }
        });
    }


        //登录调用API发送登录数据给服务器
        public static void postLogin(final Activity startActivity, final Context context, LoginRequest r, final ZLoadingDialog dialog){

<<<<<<< HEAD
            //创建Retrofit对象
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.base_url))// 设置 网络请求 Url,1.0.0版本
                    .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                    .build();
=======
>>>>>>> 3a07bad6294a63fffcae0673ec8cfcf56b1328fb

        //创建 网络请求接口 的实例
        PostLogin request = retrofit.create(PostLogin.class);

        //对 发送请求 进行封装(账号和密码)
        Call<ResponseInfo> call = request.getCall(r);

        //发送网络请求(异步)
        call.enqueue(new Callback<ResponseInfo>() {
            //请求成功时回调
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                int resultCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                System.out.println(resultCode);
                if(resultCode == errorCode){
                    Toast.makeText(context,"请检查账号密码是否准确",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else if (resultCode == successCode){
                    dialog.dismiss();
                    //token存储到本地
                    String token = Objects.requireNonNull(response.body()).getData().toString();

                    //接下来进入登录界面
                    SharedPreferencesUtils.setStoredMessage(context,"token",token);
                    Log.d(TAG, "已保存正确token值");

                    //TODO token每次登陆要刷新
                    Intent intent = new Intent(startActivity,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    startActivity.finish();

                }else{
                    //TODO 子线程更新UI界面
                    Toast.makeText(context,"发生未知错误,请反馈给开发者",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Toast.makeText(context,"服务器请求失败",Toast.LENGTH_SHORT).show();
            }
        });
    }


    //判断当前token是否可以登录
    public static void postLogin(final Context context, String token){
        if(token!=null){

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
                        SharedPreferencesUtils.setStoredMessage(context,"hasLogin","false");
                    }else if (resultCode == successCode){
                        SharedPreferencesUtils.setStoredMessage(context,"token", Objects.requireNonNull(response.body()).getData().toString());
                        SharedPreferencesUtils.setStoredMessage(context,"hasLogin","true");
                    }else{
                        SharedPreferencesUtils.setStoredMessage(context,"hasLogin","false");
                    }
                }
                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                    Log.d(TAG, "请求失败");
                    SharedPreferencesUtils.setStoredMessage(context,"hasLogin","false");
                }
            });
        }
        else{
            SharedPreferencesUtils.setStoredMessage(context,"hasLogin","false");
        }
    }

    /**
     *
     * 获取远程信息失败或者广告版本为最新时, 检查本地广告图片是否存在
     *
     */
    private static boolean checkLocalADImage() {
        Log.d(TAG, "检测本地广告图像是否存在");
        File adImageFile = new File(SDCardUtils.getADImage(newAdName));
        return adImageFile.exists();
    }

    private static void nextActivity(final Context context, final Activity startAct){
        if(SharedPreferencesUtils.getStoredMessage(context,"hasLogin").equals("false")){
            startAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent=new Intent(startAct,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    startAct.finish();
                }
            });
        }
        else {
            startAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent=new Intent(startAct,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    startAct.finish();
                }
            });
        }
    }

    private static void nextActivity(final Context context, final Activity startAct,final Class endAct) {
        startAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(startAct,endAct);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                startAct.finish();
            }
        });
    }
}
