package com.example.robin.papers.util;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robin.papers.R;
import com.example.robin.papers.activity.AdsActivity;
import com.example.robin.papers.activity.AuthPerUserInfo;
import com.example.robin.papers.activity.LoginActivity;
import com.example.robin.papers.activity.MainActivity;
import com.example.robin.papers.activity.PerfectInfoActivity;
import com.example.robin.papers.activity.UserInfoActivity;
import com.example.robin.papers.impl.PostPicture;
import com.example.robin.papers.impl.PostQmkl;
import com.example.robin.papers.model.AcademiesOrCollegesRes;
import com.example.robin.papers.model.AdData;
import com.example.robin.papers.model.CollectionListData;
import com.example.robin.papers.model.CommentListData;
import com.example.robin.papers.model.MyCommentListData;
import com.example.robin.papers.model.PostInfo;
import com.example.robin.papers.model.ResponseInfo;
import com.example.robin.papers.model.UserInfoRes;
import com.example.robin.papers.requestModel.AuthPerInfoRequest;
import com.example.robin.papers.requestModel.CommentAddRequest;
import com.example.robin.papers.requestModel.ExitLoginRequest;
import com.example.robin.papers.requestModel.GetCodeRequest;
import com.example.robin.papers.requestModel.GetCommentListRequest;
import com.example.robin.papers.requestModel.LoginRequest;
import com.example.robin.papers.requestModel.PerfectInfoRequest;
import com.example.robin.papers.requestModel.PostAddRequest;
import com.example.robin.papers.requestModel.PostIsLikeRequest;
import com.example.robin.papers.requestModel.PostRequest;
import com.example.robin.papers.requestModel.QueryAcademiesRequest;
import com.example.robin.papers.requestModel.RegisterRequest;
import com.example.robin.papers.requestModel.SetNewPswRequest;
import com.example.robin.papers.requestModel.Token;
import com.example.robin.papers.requestModel.TokenRequest;
import com.example.robin.papers.requestModel.UMengLoginRequest;
import com.example.robin.papers.requestModel.UpdateUserRequest;
import com.example.robin.papers.studentCircle.adapter.CollectionListAdapter;
import com.example.robin.papers.studentCircle.adapter.DynamicListAdapter;
import com.example.robin.papers.studentCircle.adapter.MixListAdapter;
import com.example.robin.papers.studentCircle.model.CollectionInfo;
import com.example.robin.papers.studentCircle.studentCircleActivity.DetailsActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.DetailsFromCommentActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MixShowActivity;
import com.example.robin.papers.studentCircle.model.Mixinfo;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyCollectionActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyCommentActivity;
import com.example.robin.papers.studentCircle.studentCircleActivity.MyDynamicActivity;
import com.example.robin.papers.studentCircle.tools.DataManagerUtils;
import com.example.robin.papers.studentCircle.tools.ImageLoaders;
import com.example.robin.papers.studentCircle.view.CollectionListView;
import com.example.robin.papers.studentCircle.view.CommentListView;
import com.example.robin.papers.studentCircle.view.DynamicListView;
import com.example.robin.papers.studentCircle.view.PullToZoomListView;
import com.example.robin.papers.umengUtil.umengApplication.UMapplication;
import com.youth.banner.Banner;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.robin.papers.util.ConstantUtils.*;



/*
 * （一）、postAd(final Activity startAct)
 * 参数：开始任务的活动
 * 功能：向服务发送请求获取广告内容，并判断广告是否可用：
 *       可用下载广告并进入广告页面，不可用直接进入主页面或登录界面
 * （二）、
 *
 *
 *
 * */


/**
 *
 */
public class RetrofitUtils {

    //实例化Retrofit对象
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
            .build();
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BaseUrl)// 设置 网络请求 Url,1.0.0版本，BaseUrl = "http://120.77.32.233/";
            .addConverterFactory(GsonConverterFactory.create())//设置使用Gson解析(记得加入依赖)
            .client(okHttpClient)
            .build();


    private static final String TAG = ".RetrofitUtils";
    private static String oldAdName, newAdName, adPath, avatarPath;


    //获取广告
    public static void postAd(final Activity startAct) {
        PostPicture request = retrofit.create(PostPicture.class);
        request.getAdPage().enqueue(new Callback<ResponseInfo<AdData>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<AdData>> call, @NonNull Response<ResponseInfo<AdData>> response) {
                if (response.body() != null) {
                    //广告页当前不可用
                    if (Objects.requireNonNull(response.body()).getCode() != SUCCESS_CODE ||
                            !Objects.requireNonNull(response.body()).getData().isEnabled()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                nextActivity(startAct);
                            }
                        }).start();
                        Log.d(TAG, "广告不可用");
                        return;
                    }
                    //广告页当前可用
                    oldAdName = SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "AdName");
                    newAdName = getNumbers(Objects.requireNonNull(response.body()).getData().getUpdatedAt());
                    Log.d(TAG, "createAt:" + Objects.requireNonNull(response.body()).getData().getUpdatedAt());
                    Log.d(TAG, "createAt保留数字:" + getNumbers(Objects.requireNonNull(response.body()).getData().getUpdatedAt()));
                    adPath = Objects.requireNonNull(response.body()).getData().getUrl();
                    SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "fallback",
                            Objects.requireNonNull(response.body()).getData().getFallback());
                    SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "adtitle",
                            Objects.requireNonNull(response.body()).getData().getTitle());
                    Log.d(TAG, Objects.requireNonNull(response.body()).getData().getTitle());

                    //此前尚未缓存过广告数据、广告数据已更新、广告数据被删除，重新缓存
                    if (oldAdName == null || !oldAdName.equals(newAdName) || !checkLocalADImage()) {
                        SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "AdName", newAdName);
                        Log.d(TAG, "此前尚未缓存过广告数据或者广告数据已更新，重新缓存");
                        //下载广告页
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Log.d(TAG, "原广告名：" + oldAdName);
                                    Log.d(TAG, "现广告名：" + newAdName);
                                    Log.d(TAG, "广告路径：" + adPath);
                                    Log.d(TAG, "文件路径：" + SDCardUtils.getADImage(newAdName));
                                    DownLoader.downloadFile(new File(SDCardUtils.getADImage(newAdName)),
                                            adPath);
                                    //缓存成功，进入广告页
                                    if (checkLocalADImage()) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(2000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                Log.d(TAG, "run: ");
                                                nextActivity(startAct, AdsActivity.class);

                                            }
                                        }).start();
                                    } else {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(2000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                nextActivity(startAct);
                                            }
                                        }).start();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    //缓存失败，进入登录界面或者主界面
                                    Toast.makeText(UMapplication.getContext(), CACHE_AD_ERROR, Toast.LENGTH_SHORT).show();
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e2) {
                                        e2.printStackTrace();
                                    }
                                    nextActivity(startAct);
                                }
                            }
                        }).start();
                    }
                    //广告页已准备就绪，进入广告页
                    else {
                        Log.d(TAG, "广告页已准备就绪，进入广告页");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                nextActivity(startAct, AdsActivity.class);
                            }
                        }).start();
                    }
                } else {
                    startAct.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                            nextActivity(startAct, LoginActivity.class);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<AdData>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        nextActivity(startAct, LoginActivity.class);
                    }
                }).start();
            }
        });
    }

    //登录调用API发送登录数据给服务器
    public static void postLogin(final Activity startActivity, final Context context, LoginRequest r, final ZLoadingDialog dialog) {
        //创建 网络请求接口 的实例
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo> call = request.getUserLogin(r);
        call.enqueue(new Callback<ResponseInfo>() {
            //请求成功时回调
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.body() != null) {
                    int resultCode = Objects.requireNonNull(response.body()).getCode();
                    System.out.println(resultCode);
                    if (resultCode == ERROR_CODE) {
                        Toast.makeText(UMapplication.getContext(), CHECK_ACCOUNT_AND_PSW, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else if (resultCode == SUCCESS_CODE) {
                        //token存储到本地
                        String token = Objects.requireNonNull(response.body()).getData().toString();
                        //接下来进入登录界面
                        SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "token", token);
                        SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "hasLogin", "true");
                        Log.d(TAG, "已保存正确token值");
                        //获取用户信息
                        RetrofitUtils.postUserInfo(context, startActivity, token, dialog);
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

            }

            //请求失败时回调
            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Log.d(TAG, "PostLogin请求失败");
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    //判断当前token是否可以登录
    public static void postLogin(final Context context, String token) {
        if (token != null) {
            PostQmkl request = retrofit.create(PostQmkl.class);
            Call<ResponseInfo> call = request.getUserLogin2(new TokenRequest(token));
            call.enqueue(new Callback<ResponseInfo>() {
                @Override
                public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                    if (response.body() != null) {
                        int resultCode = Objects.requireNonNull(response.body()).getCode();
                        System.out.println(resultCode);
                        if (resultCode == SUCCESS_CODE) {
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "token", Objects.requireNonNull(response.body()).getData().toString());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "hasLogin", "true");
                            postUserInfo(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "token"));
                        } else {
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "hasLogin", "false");
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                    Toast.makeText(context, SERVER_REQUEST_FAILURE, Toast.LENGTH_LONG).show();
                    SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "hasLogin", "false");
                }
            });
        } else {
            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "hasLogin", "false");
        }
    }

    //通过token值返回当前登录用户的信息
    private static void postUserInfo(final Context context, final Activity startActivity, String token, final ZLoadingDialog dialog) {

        if (token != null) {
            final PostQmkl request = retrofit.create(PostQmkl.class);
            Call<UserInfoRes> call = request.getUserInfo(new TokenRequest(token));
            call.enqueue(new Callback<UserInfoRes>() {
                @Override
                public void onResponse(@NonNull Call<UserInfoRes> call, @NonNull final Response<UserInfoRes> response) {
                    if (response.body() != null) {
                        //本地头像不存在或头像已上传更新，重新缓存头像信息并显示
                        if (!checkLocalAvatarImage() || SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "avatar") == null
                                || (SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "avatar") != null
                                && !SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "avatar").equals(Objects.requireNonNull(response.body()).getData().getAvatar()))) {
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "nickname", Objects.requireNonNull(response.body()).getData().getNickname());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "academy", Objects.requireNonNull(response.body()).getData().getAcademy());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "avatar", Objects.requireNonNull(response.body()).getData().getAvatar());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "college", Objects.requireNonNull(response.body()).getData().getCollege());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "enterYear", Objects.requireNonNull(response.body()).getData().getEnteYear());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "gender", Objects.requireNonNull(response.body()).getData().getGender());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "id", String.valueOf(Objects.requireNonNull(response.body()).getData().getId()));
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "phone", Objects.requireNonNull(response.body()).getData().getPhone());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "username", Objects.requireNonNull(response.body()).getData().getUsername());
                            avatarPath = UMapplication.getContext().getString(R.string.user_info_url) + Objects.requireNonNull(response.body()).getData().getAvatar();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Log.d(TAG, SDCardUtils.getAvatarImage(Objects.requireNonNull(response.body()).getData().getAvatar() + "\navatarPath:" + avatarPath));
                                        Log.d(TAG, "用户头像名称：" + SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "avatar"));
                                        DownLoader.downloadFile(new File(SDCardUtils.getAvatarImage(Objects.requireNonNull(response.body()).getData().getAvatar())),
                                                avatarPath);
                                        dialog.dismiss();
                                        Intent intent = new Intent(startActivity, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        UMapplication.getContext().startActivity(intent);
                                        startActivity.finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        //本地照片存在且未更新头像
                        else {
                            Log.d(TAG, "本地照片存在且未更新头像");
                            dialog.dismiss();
                            Intent intent = new Intent(startActivity, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            startActivity.finish();
                        }
                    } else {
                        Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }

                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<UserInfoRes> call, @NonNull Throwable t) {
                    Log.d(TAG, "PostUserInfo请求失败");
                    Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } else {
            Toast.makeText(UMapplication.getContext(), LOGIN_FIRST, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    //通过token值返回当前登录用户的信息
    public static void postUserInfo(String token) {
        if (token != null) {
            PostQmkl request = retrofit.create(PostQmkl.class);
            Call<UserInfoRes> call = request.getUserInfo(new TokenRequest(token));
            call.enqueue(new Callback<UserInfoRes>() {
                @Override
                public void onResponse(@NonNull Call<UserInfoRes> call, @NonNull final Response<UserInfoRes> response) {
                    if (response.body() != null) {
                        //本地头像不存在或头像已上传更新，重新缓存头像信息并显示
                        if (!checkLocalAvatarImage() || SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "avatar") == null
                                || (SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "avatar") != null
                                && !SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "avatar").equals(Objects.requireNonNull(response.body()).getData().getAvatar()))) {
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "nickname", Objects.requireNonNull(response.body()).getData().getNickname());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "academy", Objects.requireNonNull(response.body()).getData().getAcademy());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "avatar", Objects.requireNonNull(response.body()).getData().getAvatar());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "college", Objects.requireNonNull(response.body()).getData().getCollege());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "enterYear", Objects.requireNonNull(response.body()).getData().getEnteYear());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "gender", Objects.requireNonNull(response.body()).getData().getGender());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "id", String.valueOf(Objects.requireNonNull(response.body()).getData().getId()));
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "phone", Objects.requireNonNull(response.body()).getData().getPhone());
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "username", Objects.requireNonNull(response.body()).getData().getUsername());
                            avatarPath = UMapplication.getContext().getString(R.string.user_info_url) + Objects.requireNonNull(response.body()).getData().getAvatar();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Log.d(TAG, SDCardUtils.getAvatarImage(Objects.requireNonNull(response.body()).getData().getAvatar() + "\navatarPath:" + avatarPath));
                                        Log.d(TAG, "用户头像名称：" + SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "avatar"));
                                        DownLoader.downloadFile(new File(SDCardUtils.getAvatarImage(Objects.requireNonNull(response.body()).getData().getAvatar())),
                                                avatarPath);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        //本地照片存在且未更新头像
                        else {
                            Log.d(TAG, "本地照片存在且未更新头像");
                        }
                    } else {
                        Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                    }
                }

                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<UserInfoRes> call, @NonNull Throwable t) {
                    Log.d(TAG, "请求失败");
                    Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(UMapplication.getContext(), LOGIN_FIRST, Toast.LENGTH_SHORT).show();
        }
    }

    //传入token值和用户信息并更新
    public static void postUpdateUser(final int flag, final UpdateUserRequest userInfo, final AlertDialog alertDialog, final TextView textView, final ZLoadingDialog dialog, final boolean isBackSchool) {

        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo> call = request.getUserUpdateInfo(userInfo);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
                if (response.body() != null) {
                    Log.d(TAG, Objects.requireNonNull(response.body()).getMsg());
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        switch (flag) {
                            //修改昵称
                            case NICKNAME:
                                textView.setText(userInfo.getUser().getNickname());
                                MainActivity.userName.setText(userInfo.getUser().getNickname());
                                SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "nickname", userInfo.getUser().getNickname());
                                alertDialog.dismiss();
                                break;
                            //修改性别
                            case GENDER:
                                textView.setText(userInfo.getUser().getGender());
                                SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "gender", userInfo.getUser().getGender());
                                break;
                            //修改入学年份
                            case ENTER_YEAR:
                                textView.setText(userInfo.getUser().getEnterYear());
                                SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "enterYear", userInfo.getUser().getEnterYear());
                                break;
                            //修改所在大学
                            case COLLEGE:
                                //返回上一学校
                                if (isBackSchool) {
                                    textView.setText(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "lastCollege"));
                                    MainActivity.userCollegeName.setText(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "lastCollege"));
                                    SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "college", SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "lastCollege"));
                                }
                                //修改为新学校
                                else {
                                    textView.setText(userInfo.getUser().getCollege());
                                    MainActivity.userCollegeName.setText(userInfo.getUser().getCollege());
                                    SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "lastCollege",
                                            SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "college"));
                                    SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "college", userInfo.getUser().getCollege());
                                }

                                break;
                            //修改所在学院
                            case ACADEMY:
                                textView.setText(userInfo.getUser().getAcademy());
                                SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "academy", userInfo.getUser().getAcademy());
                                break;
                        }
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
                if (dialog != null) dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Log.d(TAG, "请求失败");
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                if (dialog != null) dialog.dismiss();
            }
        });
    }

    //传入用户token值该学校所有专业
    public static void postAllAcademies(QueryAcademiesRequest academiesRequest, final AlertDialog.Builder builder, final TextView college, final TextView academy, final ZLoadingDialog dialog, boolean isUpCollege) {
        //监听返回键
        if (isUpCollege) {
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface DialogInterface, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        UpdateUserRequest userRequest = UserInfoActivity.getUserRequest(UMapplication.getContext(), college.getText().toString(), COLLEGE);
                        RetrofitUtils.postUpdateUser(COLLEGE, userRequest, null, college, dialog, true);
                    }
                    return false;
                }
            });
        } else {
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface DialogInterface, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                    }
                    return false;
                }
            });
        }
        if (academies == null) {
            PostQmkl request = retrofit.create(PostQmkl.class);
            Call<AcademiesOrCollegesRes> call = request.getAcademyListCollege(academiesRequest);
            call.enqueue(new Callback<AcademiesOrCollegesRes>() {
                @Override
                public void onResponse(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull final Response<AcademiesOrCollegesRes> response) {
                    if (response.body() != null) {
                        int responseCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                        if (responseCode == SUCCESS_CODE) {
                            academies = Objects.requireNonNull(response.body()).getData();
                            // 设置参数
                            builder.setTitle(CHOOSE_ACADEMY)
                                    .setItems(academies, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            UpdateUserRequest userRequest = UserInfoActivity.getUserRequest(UMapplication.getContext(), academies[which], ACADEMY);
                                            RetrofitUtils.postUpdateUser(ACADEMY, userRequest, null, academy, dialog, false);
                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                        } else {
                            Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull Throwable t) {
                    Log.d(TAG, "请求失败");
                    Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } else {
            // 设置参数
            builder.setTitle(CHOOSE_ACADEMY)
                    .setItems(academies, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            UpdateUserRequest userRequest = UserInfoActivity.getUserRequest(UMapplication.getContext(), academies[which], ACADEMY);
                            RetrofitUtils.postUpdateUser(ACADEMY, userRequest, null, academy, dialog, false);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    //传入用户token值获取所有学校
    public static void postAllColleges(final AlertDialog.Builder builder, final TextView college, final TextView academy, final ZLoadingDialog dialog) {
        //监听返回键
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface DialogInterface, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return false;
            }
        });
        if (colleges == null) {
            PostQmkl request = retrofit.create(PostQmkl.class);
            Call<AcademiesOrCollegesRes> call = request.getCollegeList();
            call.enqueue(new Callback<AcademiesOrCollegesRes>() {
                @Override
                public void onResponse(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull final Response<AcademiesOrCollegesRes> response) {
                    if (response.body() != null) {
                        int responseCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                        if (responseCode == SUCCESS_CODE) {
                            colleges = Objects.requireNonNull(response.body()).getData();
                            // 设置参数
                            builder.setTitle(CHOOSE_COLLEGE)
                                    .setItems(colleges, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            UpdateUserRequest userRequest = UserInfoActivity.getUserRequest(UMapplication.getContext(), colleges[which], COLLEGE);
                                            RetrofitUtils.postUpdateUser(COLLEGE, userRequest, null, college, dialog, false);
                                            academies = null;
                                            QueryAcademiesRequest academiesRequest = new QueryAcademiesRequest(
                                                    colleges[which]
                                            );
                                            postAllAcademies(academiesRequest, builder, college, academy, dialog, true);
                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            //触摸外部可取消弹框
                            //alertDialog.setCanceledOnTouchOutside(false);
                            //添加弹框取消事件，弹框取消时，加载等待的进度框也消失
                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    dialog.dismiss();
                                    Log.d(TAG, "onDismiss: ");
                                }
                            });
                            alertDialog.show();
                        } else {
                            Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull Throwable t) {
                    Log.d(TAG, "请求失败");
                    Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } else {
            builder.setTitle(CHOOSE_COLLEGE)
                    .setItems(colleges, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            UpdateUserRequest userRequest = UserInfoActivity.getUserRequest(UMapplication.getContext(), colleges[which], COLLEGE);
                            RetrofitUtils.postUpdateUser(COLLEGE, userRequest, null, college, dialog, false);
                            academies = null;
                            QueryAcademiesRequest academiesRequest = new QueryAcademiesRequest(
                                    colleges[which]
                            );
                            postAllAcademies(academiesRequest, builder, college, academy, dialog, true);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            //触摸外部可取消弹框
            //alertDialog.setCanceledOnTouchOutside(false);
            //添加弹框取消事件，弹框取消时，加载等待的进度框也消失
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    dialog.dismiss();
                    Log.d(TAG, "onDismiss: ");
                }
            });
            alertDialog.show();
        }
    }

    /**
     * 用户注册界面获取学校信息
     *
     * @param builder 选择对话框实例
     * @param college 大学文本
     * @param academy 专业文本
     * @param dialog  加载动画
     */
    public static void postAllColleges(final AlertDialog.Builder builder, final EditText college, final EditText academy, final ZLoadingDialog dialog) {
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
        if (colleges == null) {
            PostQmkl request = retrofit.create(PostQmkl.class);
            Call<AcademiesOrCollegesRes> call = request.getCollegeList();
            call.enqueue(new Callback<AcademiesOrCollegesRes>() {
                @Override
                public void onResponse(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull final Response<AcademiesOrCollegesRes> response) {
                    if (response.body() != null) {
                        int responseCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                        if (responseCode == SUCCESS_CODE) {
                            colleges = Objects.requireNonNull(response.body()).getData();
                            // 设置参数
                            builder.setTitle(CHOOSE_COLLEGE)
                                    .setItems(colleges, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            college.setText(Objects.requireNonNull(response.body()).getData()[which]);
                                            academy.setText("");
                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            //触摸外部可取消弹框
                            //alertDialog.setCanceledOnTouchOutside(false);
                            //添加弹框取消事件，弹框取消时，加载等待的进度框也消失
                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    dialog.dismiss();
                                    Log.d(TAG, "onDismiss: ");
                                }
                            });
                            alertDialog.show();
                        } else {
                            Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull Throwable t) {
                    Log.d(TAG, "请求失败");
                    Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } else {
            builder.setTitle(CHOOSE_COLLEGE)
                    .setItems(colleges, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            college.setText(colleges[which]);
                            academy.setText("");
                        }
                    });
            AlertDialog alertDialog = builder.create();
            //触摸外部可取消弹框
            //alertDialog.setCanceledOnTouchOutside(false);
            //添加弹框取消事件，弹框取消时，加载等待的进度框也消失
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    dialog.dismiss();
                    Log.d(TAG, "onDismiss: ");
                }
            });
            alertDialog.show();
            dialog.dismiss();
        }
    }

    //用户注册界面获取学院信息
    //传入用户token值该学校所有专业
    public static void postAllAcademies(String collegeName, final AlertDialog.Builder builder, final EditText academy, final ZLoadingDialog dialog) {

        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<AcademiesOrCollegesRes> call = request.getAcademyListCollege(new QueryAcademiesRequest(collegeName));
        call.enqueue(new Callback<AcademiesOrCollegesRes>() {
            @Override
            public void onResponse(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull final Response<AcademiesOrCollegesRes> response) {
                if (response.body() != null) {
                    final int responseCode = Integer.parseInt(Objects.requireNonNull(response.body()).getCode());
                    if (responseCode == SUCCESS_CODE) {
                        academies = Objects.requireNonNull(response.body()).getData();
                        // 设置参数
                        builder.setTitle(CHOOSE_ACADEMY)
                                .setItems(academies, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        academy.setText(Objects.requireNonNull(response.body()).getData()[which]);
                                    }
                                });

                        AlertDialog alertDialog = builder.create();
                        //触摸外部可取消弹框
                        //alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

            }

            @Override
            public void onFailure(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull Throwable t) {
                Log.d(TAG, "请求失败");
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    //TODO 此函数一直进入onFailure，具体原因未知
    //传入用户名退出登录
    //此函数一直进入onFailure，具体原因未知
    public static void postExitLogin(String username, final Activity startAct, final ZLoadingDialog dialog) {

        PostQmkl request = retrofit.create(PostQmkl.class);
        ExitLoginRequest exitLoginRequest = new ExitLoginRequest(username);
        Call<String> call = request.getUserOut(exitLoginRequest);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.body() != null) {
                    SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "hasLogin", "false");
                    SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "token", null);

                    nextActivity(startAct, LoginActivity.class);
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d(TAG, "请求失败");
                SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "hasLogin", "false");
                SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "token", null);
                nextActivity(startAct, LoginActivity.class);
                dialog.dismiss();
            }
        });
    }

    //传入用户token和图片，上传用户头像
    public static void postUserAvatar(String imagePath, final ImageView avatarView, final Bitmap bitmap, final ZLoadingDialog dialog) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        final File avatar = new File(imagePath);
        String token = SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "token");

        //设置Content-Type:application/octet-stream
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), avatar);
        //设置Content-Disposition:form-data; name="photo"; filename="xuezhiqian.png"
        MultipartBody.Part avatarRequest = MultipartBody.Part.createFormData("avatar", avatar.getName(), photoRequestBody);

        RequestBody tokenRequest = RequestBody.create(MediaType.parse("multipart/form-data"), token);
        Call<ResponseInfo<String>> call = request.getUserUpdateAvatar(avatarRequest, tokenRequest);
        call.enqueue(new Callback<ResponseInfo<String>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<String>> call, @NonNull final Response<ResponseInfo<String>> response) {
                if (response.body() != null) {
                    SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "avatar", Objects.requireNonNull(response.body()).getData());
                    avatarPath = UMapplication.getContext().getString(R.string.user_info_url) + Objects.requireNonNull(response.body()).getData();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DownLoader.downloadFile(new File(SDCardUtils.getAvatarImage(Objects.requireNonNull(response.body()).getData())),
                                        avatarPath);
                                Log.d(TAG, "上传成功");
                                avatarView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        avatarView.setImageBitmap(bitmap);
                                    }
                                });
                                final Drawable drawable = Drawable.createFromPath(SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "avatar")));
//                                final CircleDrawable circleDrawable = new CircleDrawable(drawable, UMapplication.getContext(), 44);
//                            final Drawable drawable = Drawable.createFromPath(SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "avatar")));

//                                MainActivity.toolbar.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        MainActivity.toolbar.setNavigationIcon(circleDrawable);
//                                    }
//                                });
                                MainActivity.headImg.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "设置头像");
                                        MainActivity.headImg.setImageDrawable(drawable);
                                    }
                                });
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<String>> call, @NonNull Throwable t) {
                Log.d(TAG, "请求失败");
                Toast.makeText(UMapplication.getContext(), UPLOAD_IMG_FAILURE, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    //用户注册之后上传用户头像
    //传入用户token和图片，上传用户头像
    private static void postUserAvatar(String imagePath, final ZLoadingDialog dialog) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        final File avatar = new File(imagePath);
        String token = SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "token");

        //设置Content-Type:application/octet-stream
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), avatar);
        //设置Content-Disposition:form-data; name="photo"; filename="xuezhiqian.png"
        MultipartBody.Part avatarRequest = MultipartBody.Part.createFormData("avatar", avatar.getName(), photoRequestBody);

        RequestBody tokenRequest = RequestBody.create(MediaType.parse("multipart/form-data"), token);
        Call<ResponseInfo<String>> call = request.getUserUpdateAvatar(avatarRequest, tokenRequest);
        call.enqueue(new Callback<ResponseInfo<String>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<String>> call, @NonNull final Response<ResponseInfo<String>> response) {
                if (response.body() != null) {
                    SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "avatar", Objects.requireNonNull(response.body()).getData());
                    avatarPath = UMapplication.getContext().getString(R.string.user_info_url) + Objects.requireNonNull(response.body()).getData();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DownLoader.downloadFile(new File(SDCardUtils.getAvatarImage(Objects.requireNonNull(response.body()).getData())),
                                        avatarPath);
                                Log.d(TAG, "上传成功");

                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<String>> call, @NonNull Throwable t) {
                Log.d(TAG, "请求失败");
                Toast.makeText(UMapplication.getContext(), UPLOAD_IMG_FAILURE, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    //用户注册接口一、找回密码接口一
    //发送手机号并向该手机发送短信验证码，在CountDownTimer中更新按钮状态
    public static void postGetCode(String phone, final TextView sendCodeBtn, final String msg) {

        PostQmkl request = retrofit.create(PostQmkl.class);
        GetCodeRequest codeRequest = new GetCodeRequest(phone, msg);

        Call<ResponseInfo<String>> call = request.getSmsSend(codeRequest);
        call.enqueue(new Callback<ResponseInfo<String>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<String>> call, @NonNull Response<ResponseInfo<String>> response) {
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        if (msg.equals(FORGET_PSW_MSG)) {
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "setPswToken", Objects.requireNonNull(response.body()).getData());
                        } else if (msg.equals(REGISTER_MSG)) {
                            SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "registerToken", Objects.requireNonNull(response.body()).getData());
                        }
                        Toast.makeText(UMapplication.getContext(), VER_CODE_SEND, Toast.LENGTH_SHORT).show();
                        new Thread(new CountDownTimer(60, sendCodeBtn, 1, UMapplication.getContext())).start();
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<String>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");

            }
        });
    }

    //找回密码接口二
    //找回密码，传入手机号、验证码和新密码，找回后返回登录界面
    public static void postSetNewPsw(final Activity startAct, String phone, String verCode, String newPsw) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        String setPswToken = SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "setPswToken");
        String SHApassword = SHAArithmetic.encode(newPsw);//密码加密
        SetNewPswRequest pswRequest = new SetNewPswRequest(setPswToken, verCode, phone, SHApassword);

        Call<ResponseInfo> call = request.getUserUpdatePassword(pswRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        nextActivity(startAct, LoginActivity.class);
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");

            }
        });
    }

    //用户注册接口二
    //发送该请求后进入完善资料界面，本接口为测试手机与验证码的正确性
    public static void postRegister(final Activity startAct, final String phone, String verCode) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        String registerToken = SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "registerToken");

        RegisterRequest registerRequest = new RegisterRequest(registerToken, verCode, phone);

        Call<ResponseInfo> call = request.getUserVerCode(registerRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "phone", phone);
                        nextActivity(startAct, PerfectInfoActivity.class);
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");

            }
        });
    }

    //用户注册接口三
    //传入用户信息，完成注册
    public static void postPerfectInfo(final Context context, PerfectInfoRequest perfectInfoRequest, final Activity startAct) {
        PostQmkl request = retrofit.create(PostQmkl.class);

        Call<ResponseInfo<String>> call = request.getUserAllInfo(perfectInfoRequest);
        call.enqueue(new Callback<ResponseInfo<String>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<String>> call, @NonNull Response<ResponseInfo<String>> response) {
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        String token = Objects.requireNonNull(response.body()).getData();
                        SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "token", token);
                        ZLoadingDialog dialog = new ZLoadingDialog(UMapplication.getContext());
                        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                                .setLoadingColor(UMapplication.getContext().getResources().getColor(R.color.blue))//颜色
                                .setHintText("loading...")
                                .setCanceledOnTouchOutside(false)
                                .show();
                        ZLoadingDialog dialog2 = new ZLoadingDialog(context);
                        dialog2.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                                .setLoadingColor(UMapplication.getContext().getResources().getColor(R.color.blue))//颜色
                                .setHintText("upLoading...")
                                .setCanceledOnTouchOutside(false)
                                .show();
                        postUserInfo(context, startAct, token, dialog);
                        String imagePath = SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "imagePath");
                        imagePath = CircleDrawable.compressImage(imagePath);
                        postUserAvatar(imagePath, dialog2);
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<String>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");

            }
        });
    }

    //用户第三方登录接口，传入uid，判断用户信息是否完整以及登录
    public static void postAuthLogin(final Context context, final UMengLoginRequest uMengLoginRequest, final Activity startAct) {
        PostQmkl request = retrofit.create(PostQmkl.class);

        Call<ResponseInfo<String>> call = request.getUserAuthLogin(uMengLoginRequest);
        call.enqueue(new Callback<ResponseInfo<String>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<String>> call, @NonNull Response<ResponseInfo<String>> response) {
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "token", Objects.requireNonNull(response.body()).getData());
                        SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "platform", uMengLoginRequest.getPlatform());
                        //使用com.zyao89:zloading:1.1.2引用別人的加载动画
                        ZLoadingDialog dialog = new ZLoadingDialog(context);
                        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                                .setLoadingColor(UMapplication.getContext().getResources().getColor(R.color.blue))//颜色
                                .setHintText("Login...")
                                .setCanceledOnTouchOutside(false)
                                .show();
                        Log.d("token值", Objects.requireNonNull(response.body()).getData());
                        RetrofitUtils.postUserInfo(UMapplication.getContext(), startAct, Objects.requireNonNull(response.body()).getData(), dialog);
//                    nextActivity(UMapplication.getContext(),startAct,MainActivity.class);
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                        nextActivity(startAct, AuthPerUserInfo.class);
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<String>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    //用户第三方登录接口，完善用户信息
    public static void postAuthPerInfo(final Context context, AuthPerInfoRequest authPerInfoRequest, final Activity startAct) {
        PostQmkl request = retrofit.create(PostQmkl.class);

        Call<ResponseInfo<String>> call = request.getUserAuthUpdateInfo(authPerInfoRequest);
        call.enqueue(new Callback<ResponseInfo<String>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<String>> call, @NonNull Response<ResponseInfo<String>> response) {
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "token", Objects.requireNonNull(response.body()).getData());
                        SharedPreferencesUtils.setStoredMessage(UMapplication.getContext(), "hasLogin", "true");
                        //使用com.zyao89:zloading:1.1.2引用別人的加载动画
                        ZLoadingDialog dialog = new ZLoadingDialog(context);
                        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                                .setLoadingColor(UMapplication.getContext().getResources().getColor(R.color.blue))//颜色
                                .setHintText("Login...")
                                .setCanceledOnTouchOutside(false)
                                .show();
                        Log.d("token值", Objects.requireNonNull(response.body()).getData());
                        RetrofitUtils.postUserInfo(UMapplication.getContext(), startAct, Objects.requireNonNull(response.body()).getData(), dialog);
//                    nextActivity(UMapplication.getContext(),startAct);
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<String>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");

            }
        });
    }


    //用户上传文件
    public static void PostUpLoadFiles(String filePath, String spath, String note, String anonymous, final ZLoadingDialog dialog, final Activity startAct) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        final File file = new File(filePath);
        String userId = SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "id");

        RequestBody userIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), userId);


        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part avatarRequest = MultipartBody.Part.createFormData("file", file.getName(), fileRequestBody);

        RequestBody spathRequest = RequestBody.create(MediaType.parse("multipart/form-data"), spath);
        RequestBody noteRequest;
        if (note != null) {
            noteRequest = RequestBody.create(MediaType.parse("multipart/form-data"), note);
        } else {
            noteRequest = null;
        }

        RequestBody anonymousRequest = RequestBody.create(MediaType.parse("multipart/form-data"), anonymous);

        Call<ResponseInfo<String>> call = request.finalexamAppUpfile(userIdRequest, avatarRequest, noteRequest, spathRequest, anonymousRequest);
        call.enqueue(new Callback<ResponseInfo<String>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<String>> call, @NonNull final Response<ResponseInfo<String>> response) {
                if (response.body() == null) {
                    Toast.makeText(UMapplication.getContext(), UPLOAD_FILE_BIG, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UMapplication.getContext(), UPLOAD_FILE_SUCCESS, Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(startAct).setTitle("提示").setMessage("上传文件成功，留在此页还是返回主页面？")

                            .setPositiveButton("返回主页面", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startAct.finish();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("留在此页", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<String>> call, @NonNull Throwable t) {
                Log.d(TAG + "上传", "失败");
                Toast.makeText(UMapplication.getContext(), UPLOAD_FILE_FAILURE, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    //查询所有帖子
    public static void postAllPost(final Context context, PostRequest postRequest, final ArrayList<Mixinfo> data) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo<PostInfo[]>> call = request.getPostList(postRequest);
        call.enqueue(new Callback<ResponseInfo<PostInfo[]>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<PostInfo[]>> call, @NonNull Response<ResponseInfo<PostInfo[]>> response) {
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        MixShowActivity.mixlist.removeFooterView(PullToZoomListView.mFooterView);
                        if(response.body().getData().length==0){
                            Toast.makeText(UMapplication.getContext(),"到底啦，我也是有底线的~",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            for(PostInfo postinfo:response.body().getData()){
                                Mixinfo mixinfo= new Mixinfo(postinfo);
                                data.add(mixinfo);
                            }
                            MixShowActivity.adapterData.notifyDataSetChanged();
                        }
                    } else {
                        MixShowActivity.mixlist.removeFooterView(PullToZoomListView.mFooterView);
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MixShowActivity.mixlist.removeFooterView(PullToZoomListView.mFooterView);
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<PostInfo[]>> call, @NonNull Throwable t) {
                MixShowActivity.mixlist.removeFooterView(PullToZoomListView.mFooterView);
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }
    //查询所有帖子
    public static void postAllPost(final Context context, PostRequest postRequest, final ArrayList<Mixinfo> data, final ZLoadingDialog dialog) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo<PostInfo[]>> call = request.getPostList(postRequest);
        call.enqueue(new Callback<ResponseInfo<PostInfo[]>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<PostInfo[]>> call, @NonNull Response<ResponseInfo<PostInfo[]>> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        //请求成功后取消底部加载动画
                        MixShowActivity.mixlist.removeFooterView(PullToZoomListView.mFooterView);
                        if(response.body().getData().length==0){
                            Toast.makeText(UMapplication.getContext(),"到底啦，我也是有底线的~",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //将请求回的数据加入到data数据集中
                            for(PostInfo postinfo:response.body().getData()){
                                Mixinfo mixinfo= new Mixinfo(postinfo);
                                data.add(mixinfo);
                            }
                            //更新adapterData
                            MixShowActivity.adapterData.notifyDataSetChanged();

                        }
                    } else {
                        MixShowActivity.mixlist.removeFooterView(PullToZoomListView.mFooterView);
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MixShowActivity.mixlist.removeFooterView(PullToZoomListView.mFooterView);
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<PostInfo[]>> call, @NonNull Throwable t) {
                dialog.dismiss();
                MixShowActivity.mixlist.removeFooterView(PullToZoomListView.mFooterView);
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    /**
     * @param context 上下文
     * @param postIsLikeRequest
     * @param like 是否点赞的图标
     * @param position 帖子在列表中位置
     * @param sourceClass 来源类
     */
    //判断是否点赞
    public static void postIsLike(Context context, PostIsLikeRequest postIsLikeRequest, final ImageView like, final int position,final Class sourceClass) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo<Boolean>> call = request.getPostLikeIsLike(postIsLikeRequest);
        call.enqueue(new Callback<ResponseInfo<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<Boolean>> call, @NonNull final Response<ResponseInfo<Boolean>> response) {
                if(response.body()!=null){
                    if(response.body().getData()){
                        like.post(new Runnable() {
                            @Override
                            public void run() {
                                like.setImageResource(R.drawable.like2);
                            }
                        });
                        if(sourceClass == MixListAdapter.class) MixShowActivity.data.get(position).is_like=true;
                        else if(sourceClass == CollectionListAdapter.class) MyCollectionActivity.data.get(position).is_like=true;
                        else if(sourceClass == DynamicListAdapter.class) MyDynamicActivity.data.get(position).is_like=true;
                    }
                    else {
                        if(sourceClass == MixListAdapter.class) MixShowActivity.data.get(position).is_like=false;
                        else if(sourceClass == CollectionListAdapter.class) MyCollectionActivity.data.get(position).is_like=false;
                        else if(sourceClass == DynamicListAdapter.class) MyDynamicActivity.data.get(position).is_like=false;
                    }
                }
                else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<Boolean>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    /**
     * @param context 上下文
     * @param postIsLikeRequest
     * @param like 详情页是否点赞左边的图标
     * @param like2 详情页是否点赞右边的图标

     */
    //从我的评论进入详情页中判断是否点赞
    public static void postIsLike(Context context, PostIsLikeRequest postIsLikeRequest, final ImageView like, final ImageView like2) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo<Boolean>> call = request.getPostLikeIsLike(postIsLikeRequest);
        call.enqueue(new Callback<ResponseInfo<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<Boolean>> call, @NonNull final Response<ResponseInfo<Boolean>> response) {
                if(response.body()!=null){
                    if(response.body().getData()){
                        like.post(new Runnable() {
                            @Override
                            public void run() {
                                like.setImageResource(R.drawable.like2);
                            }
                        });
                        like2.post(new Runnable() {
                            @Override
                            public void run() {
                                like2.setImageResource(R.drawable.like2);
                            }
                        });
                        DetailsFromCommentActivity.mixinfo.is_like=true;
                    }
                    else {
                        DetailsFromCommentActivity.mixinfo.is_like=false;
                    }
                }
                else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<Boolean>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    //帖子点赞
    /**
     * @param context 上下文
     * @param postIsLikeRequest
     * @param like 评论列表的点赞图标以及详情页的点赞图标
     * @param like2 详情页右下角点赞图标，空值则为外部评论列表请求
     * @param like_count 点赞数
     * @param islike 本人是否点赞
     */
    public static void postLike(Context context, PostIsLikeRequest postIsLikeRequest, final ImageView like, final ImageView like2, final TextView like_count, final boolean islike) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo> call = request.getPostLikeAdd(postIsLikeRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
                if(response.body()!=null){
                    if(response.body().getCode()==SUCCESS_CODE){
                        like.post(new Runnable() {
                            @Override
                            public void run() {
                                if(islike){
                                    like.setImageResource(R.drawable.like1);
                                }
                                else{
                                    like.setImageResource(R.drawable.like2);
                                }
                            }
                        });
                        if(like2!=null){
                            like2.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(islike){
                                        like2.setImageResource(R.drawable.like1);
                                    }
                                    else{
                                        like2.setImageResource(R.drawable.like2);
                                    }
                                }
                            });
                        }

                        like_count.post(new Runnable() {
                            @Override
                            public void run() {
                                if(islike){
                                    like_count.setText(Integer.parseInt(like_count.getText().toString())-1==0?"0":(Integer.parseInt(like_count.getText().toString())-1)+"");
                                }
                                else{
                                    like_count.setText(like_count.getText().toString().equals("")?"1":(String.valueOf(Integer.parseInt(like_count.getText().toString())+1)));
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(UMapplication.getContext(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }


//    //获取所有评论列表
//    /**
//     * @param context 上下文
//     * @param getCommentListRequest 请求参数
//     * @param data 数据源
//     * @param dataPosition 点击帖子在列表中的位置
//     */
//    public static void postGetCommentList(final Context context, GetCommentListRequest getCommentListRequest, final ArrayList data, final int dataPosition) {
//        final Mixinfo newInfo=(Mixinfo)data.get(dataPosition);
//        if(newInfo.commentListData.size()!=newInfo.postInfo.getCommentNum()){
//            PostGetCommentList request = retrofit.create(PostGetCommentList.class);
//            Call<ResponseInfo<CommentListData>> call = request.getCall(getCommentListRequest);
//            call.enqueue(new Callback<ResponseInfo<CommentListData>>() {
//                @Override
//                public void onResponse(@NonNull Call<ResponseInfo<CommentListData>> call, @NonNull final Response<ResponseInfo<CommentListData>> response) {
//                    if(response.body()!=null){
//                        newInfo.commentListData.clear();
//                        if(Objects.requireNonNull(response.body()).getData()!=null){
//                            for(int i=0;i<Objects.requireNonNull(response.body()).getData().length;i++){
//                                newInfo.commentListData.add((response.body().getData())[i]);
//                            }
//                            data.set(dataPosition,newInfo);
//                            DetailsActivity.commentListAdapter.notifyDataSetChanged();
//                        }
//                    }
//                    else {
//                        Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
//                    }
//                }
//                @Override
//                public void onFailure(@NonNull Call<ResponseInfo<CommentListData>> call, @NonNull Throwable t) {
//                    Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "请求失败");
//                }
//            });
//        }
//    }


    //获取对应帖子的评论列表
    /**
     * @param context 上下文
     * @param getCommentListRequest 请求参数
     * @param sourceClass
     * @param position
     */
    public static void postGetCommentList(final Context context, GetCommentListRequest getCommentListRequest, final Class sourceClass, final int position) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo<CommentListData>> call = request.getCommentList(getCommentListRequest);
        call.enqueue(new Callback<ResponseInfo<CommentListData>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<CommentListData>> call, @NonNull final Response<ResponseInfo<CommentListData>> response) {
                if(response.body()!=null){
                    if(Objects.requireNonNull(response.body()).getData()!=null){
                        Mixinfo mixinfo= DataManagerUtils.getData(sourceClass).get(position);
                        mixinfo.commentListData=Objects.requireNonNull(response.body()).getData();
                        DataManagerUtils.setMixinfo(sourceClass,position,mixinfo);
                        DetailsActivity.commentListAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseInfo<CommentListData>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });

    }
//
//    //我的评论进入详情页获取所有评论列表
//    /**
//     * @param context 上下文
//     * @param getCommentListRequest 请求参数
//     */
//    public static void postGetCommentList(final Context context, GetCommentListRequest getCommentListRequest) {
//        if(DetailsFromCommentActivity.mixinfo.commentListData.size()!=DetailsFromCommentActivity.mixinfo.postInfo.getCommentNum()){
//            PostGetCommentList request = retrofit.create(PostGetCommentList.class);
//            Call<ResponseInfo<CommentListData[]>> call = request.getCall(getCommentListRequest);
//            call.enqueue(new Callback<ResponseInfo<CommentListData[]>>() {
//                @Override
//                public void onResponse(@NonNull Call<ResponseInfo<CommentListData[]>> call, @NonNull final Response<ResponseInfo<CommentListData[]>> response) {
//                    if(response.body()!=null){
//                        DetailsFromCommentActivity.mixinfo.commentListData.clear();
//                        if(Objects.requireNonNull(response.body()).getData()!=null){
//                            for(int i=0;i<Objects.requireNonNull(response.body()).getData().length;i++){
//                                DetailsFromCommentActivity.mixinfo.commentListData.add((response.body().getData())[i]);
//                            }
//                            DetailsFromCommentActivity.commentListAdapter.notifyDataSetChanged();
//                        }
//                    }
//                    else {
//                        Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
//                    }
//                }
//                @Override
//                public void onFailure(@NonNull Call<ResponseInfo<CommentListData[]>> call, @NonNull Throwable t) {
//                    Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "请求失败");
//                }
//            });
//        }
//    }
//
//    //我的评论进入详情页添加评论
//    /**
//     * @param context 上下文
//     * @param commentAddRequest
//     * @param commentCount 评论数
//     */
//    public static void postAddComment(final Context context, CommentAddRequest commentAddRequest, final TextView commentCount) {
//        PostCommentAdd request = retrofit.create(PostCommentAdd.class);
//        Call<ResponseInfo> call = request.getCall(commentAddRequest);
//        call.enqueue(new Callback<ResponseInfo>() {
//            @Override
//            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
//                if(response.body()!=null){
//                    if(Objects.requireNonNull(response.body()).getCode()==ConstantUtils.SUCCESS_CODE){
//                        String token=SharedPreferencesUtils.getStoredMessage(context,"token");
//
//                        DetailsFromCommentActivity.mixinfo.postInfo.setCommentNum(DetailsFromCommentActivity.mixinfo.postInfo.getCommentNum()+1);
//                        commentCount.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                commentCount.setText(DetailsFromCommentActivity.mixinfo.postInfo.getCommentNum()+"");
//                            }
//                        });
//
//                        GetCommentListRequest getCommentListRequest=new GetCommentListRequest(token,String.valueOf(DetailsFromCommentActivity.mixinfo.postInfo.getId()),"1",String.valueOf(DetailsFromCommentActivity.mixinfo.postInfo.getCommentNum()));
//                        RetrofitUtils.postGetCommentList(context,getCommentListRequest);
//                        Toast.makeText(context,"评论成功！",Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else {
//                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
//                }
//            }
//            @Override
//            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
//                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "请求失败");
//            }
//        });
//    }


    //添加评论
    /**
     * @param context 上下文
     * @param commentAddRequest
     * @param index 帖子在listview中的位置
     * @param mixinfo 帖子信息
     * @param commentCount 评论数
     */
    public static void postAddComment(final Context context, CommentAddRequest commentAddRequest, final int index, final Mixinfo mixinfo, final TextView commentCount,final Class sourceClass) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo> call = request.getCommentAdd(commentAddRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
                if(response.body()!=null){
                    if(Objects.requireNonNull(response.body()).getCode()==ConstantUtils.SUCCESS_CODE){
                        String token=SharedPreferencesUtils.getStoredMessage(context,"token");

                        Mixinfo mixinfo1=DataManagerUtils.getMixinfo(sourceClass,index);
                        mixinfo1.postInfo.setCommentNum(mixinfo1.postInfo.getCommentNum()+1);
                        DataManagerUtils.setMixinfo(sourceClass,index,mixinfo1);
                        DataManagerUtils.notifyDataSetChanged(sourceClass);

                        GetCommentListRequest getCommentListRequest=new GetCommentListRequest(token,String.valueOf(mixinfo.postInfo.getId()),"1",String.valueOf(mixinfo1.postInfo.getCommentNum()));
                        RetrofitUtils.postGetCommentList(context,getCommentListRequest,sourceClass,index);

                        final Mixinfo finalMixinfo = mixinfo1;
                        commentCount.post(new Runnable() {
                            @Override
                            public void run() {
                                commentCount.setText(finalMixinfo.postInfo.getCommentNum()+"");
                            }
                        });
                        Toast.makeText(context,"评论成功！",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }
    //用户发表新帖子
    /**
     * @param context 上下文
     * @param postAddRequest
     * @param startAct 启动活动
     */
    public static void postAddPost(final Context context, PostAddRequest postAddRequest, final Activity startAct) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo> call = request.getPostAdd(postAddRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
                if(response.body()!=null){
                    nextActivity(startAct,MixShowActivity.class);
                    Toast.makeText(UMapplication.getContext(), "发表成功！", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    /**
     * @param context 上下文
     * @param postIsLikeRequest
     * @param position 帖子在列表中的位置
     * @param sourceClass 来源类
     */
    //判断是否收藏
    public static void postIsCollect(Context context, PostIsLikeRequest postIsLikeRequest, final int position, final Class sourceClass,final ImageView collection) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo<Boolean>> call = request.getCollectIsCollect(postIsLikeRequest);
        call.enqueue(new Callback<ResponseInfo<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<Boolean>> call, @NonNull final Response<ResponseInfo<Boolean>> response) {
                if(response.body()!=null){
                    if(response.body().getData()){
                        DataManagerUtils.getData(sourceClass).get(position).is_collect=true;
                        collection.post(new Runnable() {
                            @Override
                            public void run() {
                                collection.setImageResource(R.drawable.collection);
                            }
                        });
                    }
                    else {
                        DataManagerUtils.getData(sourceClass).get(position).is_collect=false;
                        collection.post(new Runnable() {
                            @Override
                            public void run() {
                                collection.setImageResource(R.drawable.uncollection);
                            }
                        });
//                        if(sourceClass== MixListAdapter.class) {
//                            MixShowActivity.data.get(position).is_collect=false;
//                        }
//                        else if(sourceClass== CollectionListAdapter.class){
//                            MyCollectionActivity.data.get(position).is_collect=false;
//                        }
//                        else if (sourceClass == DynamicListAdapter.class){
//                            MyDynamicActivity.data.get(position).is_collect=false;
//                        }
                    }
                }
                else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<Boolean>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }
    /**
     * @param context 上下文
     * @param postIsLikeRequest
     */
    //我的评论进入详情页中判断是否收藏
    public static void postIsCollect(Context context, PostIsLikeRequest postIsLikeRequest) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo<Boolean>> call = request.getCollectIsCollect(postIsLikeRequest);
        call.enqueue(new Callback<ResponseInfo<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<Boolean>> call, @NonNull final Response<ResponseInfo<Boolean>> response) {
                if(response.body()!=null){
                    if(response.body().getData()){
                        DetailsFromCommentActivity.mixinfo.is_collect=true;
                    }
                    else {
                        DetailsFromCommentActivity.mixinfo.is_collect=false;
                    }
                }
                else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<Boolean>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    /**
     * @param activity 启动活动，当时DetailsActivity时不为空
     * @param postIsLikeRequest
     * @param mixinfo 主要使用mixinfo.is_collect来判断使用者是收藏或取消收藏
     * @param position 帖子在列表中的位置
     * @param sourceClass 来源类
     */
    //用户收藏或取消收藏
    public static void postCollectPost(final Activity activity, PostIsLikeRequest postIsLikeRequest, final Mixinfo mixinfo, final int position, final Class sourceClass, final ImageView collection) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo> call = request.getCollectAoc(postIsLikeRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
                if(response.body()!=null){
                    if(mixinfo.is_collect){
                        //趣聊主页列表和详情页点击取消收藏
                        if(sourceClass == MixListAdapter.class) {
                            MixShowActivity.data.get(position).is_collect=false;
                            MixShowActivity.adapterData.notifyDataSetChanged();
                        }
                        //我的收藏页面列表点击取消收藏
                        else if(activity==null && sourceClass == CollectionListAdapter.class) {
                            MyCollectionActivity.data.remove(position);
                            MyCollectionActivity.adapterData.notifyDataSetChanged();
                        }

                        //我的收藏页面的详情页点击取消收藏
                        else if(activity!=null && sourceClass == CollectionListAdapter.class){
                            MyCollectionActivity.data.remove(position);
                            MyCollectionActivity.adapterData.notifyDataSetChanged();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activity.finish();
                                }
                            });
                        }
                        //我的动态页面和详情页点击取消收藏
                        else if(sourceClass == DynamicListAdapter.class){
                            MyDynamicActivity.data.get(position).is_collect=false;
                            MyDynamicActivity.adapterData.notifyDataSetChanged();
                        }
                        collection.post(new Runnable() {
                            @Override
                            public void run() {
                                collection.setImageResource(R.drawable.uncollection);
                            }
                        });
                        Toast.makeText(UMapplication.getContext(), "已取消收藏！", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //趣聊主页列表和详情页点击收藏
                        if(sourceClass == MixListAdapter.class) {
                            MixShowActivity.data.get(position).is_collect=true;
                            MixShowActivity.adapterData.notifyDataSetChanged();
                        }
                        else if(sourceClass == DynamicListAdapter.class){
                            MyDynamicActivity.data.get(position).is_collect=true;
                            MyDynamicActivity.adapterData.notifyDataSetChanged();
                        }
                        collection.post(new Runnable() {
                            @Override
                            public void run() {
                                collection.setImageResource(R.drawable.collection);
                            }
                        });
                        Toast.makeText(UMapplication.getContext(), "收藏成功，您可以在 我的收藏 中找到该贴！", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    /**

     * @param postIsLikeRequest
     * @param is_collect 用户是否已收藏
     */
    //我的评论进入详情页中用户收藏或取消收藏
    public static void postCollectPost(PostIsLikeRequest postIsLikeRequest, final boolean is_collect) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo> call = request.getCollectAoc(postIsLikeRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
                if(response.body()!=null){
                    DetailsFromCommentActivity.mixinfo.is_collect=!is_collect;
                    if(is_collect){
                        Toast.makeText(UMapplication.getContext(), "已取消收藏！", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(UMapplication.getContext(), "收藏成功，您可以在 我的收藏 中找到该贴！", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    /**
     * @param context 上下文
     * @param postRequest
     * @param data 收藏帖子数据源
     * @param dialog 等待图标
     */
    //查询我收藏的帖子
    public static void postGetCollection(final Context context, PostRequest postRequest, final ArrayList<Mixinfo> data, final ZLoadingDialog dialog) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo<CollectionListData[]>> call = request.getCollectList(postRequest);
        call.enqueue(new Callback<ResponseInfo<CollectionListData[]>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<CollectionListData[]>> call, @NonNull Response<ResponseInfo<CollectionListData[]>> response) {
                if(dialog!=null) dialog.dismiss();
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        //请求成功后取消底部加载动画
                        MyCollectionActivity.collectionList.removeFooterView(CollectionListView.mFooterView);
                        if(response.body().getData().length==0){
                            Toast.makeText(UMapplication.getContext(),"到底啦，我也是有底线的~",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //将请求回的数据加入到data数据集中
                            for(CollectionListData collectionListData:response.body().getData()){
                                data.add(new Mixinfo(new CollectionInfo(collectionListData)));
                            }
                            MyCollectionActivity.adapterData.notifyDataSetChanged();
                        }
                    } else {
                        MyCollectionActivity.collectionList.removeFooterView(CollectionListView.mFooterView);
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MyCollectionActivity.collectionList.removeFooterView(CollectionListView.mFooterView);
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<CollectionListData[]>> call, @NonNull Throwable t) {
                if(dialog!=null) dialog.dismiss();
                MyCollectionActivity.collectionList.removeFooterView(CollectionListView.mFooterView);
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }
    /**
     * @param context 上下文
     * @param postRequest
     * @param data 我的动态数据源
     * @param dialog 等待图标
     */
    //查询我发出的帖子
    public static void postGetMyDynamic(final Context context, PostRequest postRequest, final ArrayList<Mixinfo> data, final ZLoadingDialog dialog) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo<PostInfo[]>> call = request.getPostUserList(postRequest);
        call.enqueue(new Callback<ResponseInfo<PostInfo[]>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<PostInfo[]>> call, @NonNull Response<ResponseInfo<PostInfo[]>> response) {
                if(dialog!=null) dialog.dismiss();
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        //请求成功后取消底部加载动画
                        MyDynamicActivity.dynamicList.removeFooterView(DynamicListView.mFooterView);
                        if(response.body().getData().length==0){
                            Toast.makeText(UMapplication.getContext(),"到底啦，我也是有底线的~",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //将请求回的数据加入到data数据集中
                            for(PostInfo postInfo:response.body().getData()){
                                data.add(new Mixinfo(postInfo));
                            }
                            MyDynamicActivity.adapterData.notifyDataSetChanged();

                        }
                    } else {
                        MyDynamicActivity.dynamicList.removeFooterView(DynamicListView.mFooterView);
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MyDynamicActivity.dynamicList.removeFooterView(DynamicListView.mFooterView);
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<PostInfo[]>> call, @NonNull Throwable t) {
                if(dialog!=null) dialog.dismiss();
                MyDynamicActivity.dynamicList.removeFooterView(DynamicListView.mFooterView);
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    /**
     * @param context 上下文
     * @param postRequest
     * @param data 我的评论数据源
     * @param dialog 等待图标
     */
    //查询我的评论
    public static void postGetMyComment(final Context context, PostRequest postRequest, final ArrayList<MyCommentListData> data, final ZLoadingDialog dialog) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo<MyCommentListData[]>> call = request.getCommentUserList(postRequest);
        call.enqueue(new Callback<ResponseInfo<MyCommentListData[]>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<MyCommentListData[]>> call, @NonNull Response<ResponseInfo<MyCommentListData[]>> response) {
                if(dialog!=null) dialog.dismiss();
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        //请求成功后取消底部加载动画
                        MyCommentActivity.commentList.removeFooterView( CommentListView.mFooterView);
                        if(response.body().getData().length==0){
                            Toast.makeText(UMapplication.getContext(),"到底啦，我也是有底线的~",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //将请求回的数据加入到data数据集中
                            data.addAll(Arrays.asList(response.body().getData()));
                            MyCommentActivity.adapterData.notifyDataSetChanged();
                        }
                    } else {
                        MyCommentActivity.commentList.removeFooterView(CommentListView.mFooterView);
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MyCommentActivity.commentList.removeFooterView(CommentListView.mFooterView);
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<MyCommentListData[]>> call, @NonNull Throwable t) {
                if(dialog!=null) dialog.dismiss();
                MyCommentActivity.commentList.removeFooterView(CommentListView.mFooterView);
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }


    //查询某一个贴子
    public static void postQueryAPost(final Context context, String postId, String token, final Activity startAct) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo<PostInfo>> call = request.getPostGetPost(postId,new Token(token));
        call.enqueue(new Callback<ResponseInfo<PostInfo>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<PostInfo>> call, @NonNull final Response<ResponseInfo<PostInfo>> response) {
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        if(response.body().getData()!=null){
                            startAct.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(startAct, DetailsFromCommentActivity.class);
                                    intent.putExtra("mixinfo",new Mixinfo(response.body().getData()));
                                    UMapplication.getContext().startActivity(intent);
                                }
                            });
                        }
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<PostInfo>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    //删除帖子
    public static void postDelPost(final Context context, PostIsLikeRequest postIsLikeRequest, final Activity startAct, final Class endClass) {
        PostQmkl request = retrofit.create(PostQmkl.class);
        Call<ResponseInfo> call = request.getPostDel(postIsLikeRequest);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        nextActivity(startAct,endClass);
                        Toast.makeText(UMapplication.getContext(), "删除成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    //获取服务器图片
    public static void postHomePage(final Context context , final ImageView headerView) {
        PostPicture request = retrofit.create(PostPicture.class);
        Call<ResponseInfo<AdData[]>> call = request.getHomePage();
        call.enqueue(new Callback<ResponseInfo<AdData[]>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<AdData[]>> call, @NonNull final Response<ResponseInfo<AdData[]>> response) {
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        ImageLoaders.setsendimg(response.body().getData()[0].getUrl(),headerView);
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<AdData[]>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }

    //获取服务器图片
    public static void postBannerPic(final Context context , final Banner banner) {
        PostPicture request = retrofit.create(PostPicture.class);
        Call<ResponseInfo<AdData[]>> call = request.getBannerPic();
        call.enqueue(new Callback<ResponseInfo<AdData[]>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<AdData[]>> call, @NonNull final Response<ResponseInfo<AdData[]>> response) {
                if (response.body() != null) {
                    int responseCode = Objects.requireNonNull(response.body()).getCode();
                    if (responseCode == SUCCESS_CODE) {
                        ArrayList<String> path_list=new ArrayList<>();
                        for(AdData adData:response.body().getData()){
                            path_list.add(adData.getUrl());
                            Log.d("各个", response.body().getData()[0].getId()+"");
                        }
                        banner.setImages(path_list).start();
                    } else {
                        Toast.makeText(UMapplication.getContext(), Objects.requireNonNull(response.body()).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UMapplication.getContext(), CONNECT_WITH_ME, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo<AdData[]>> call, @NonNull Throwable t) {
                Toast.makeText(UMapplication.getContext(), SERVER_REQUEST_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "请求失败");
            }
        });
    }
    //获取远程信息失败或者广告版本为最新时, 检查本地广告图片是否存在
    private static boolean checkLocalADImage() {

        File adImageFile = new File(SDCardUtils.getADImage(newAdName));
        if (adImageFile.exists()) Log.d(TAG, "本地广告图像存在");
        else Log.d(TAG, "本地广告图像不存在");
        return adImageFile.exists();
    }

    //获取远程信息失败或者广告版本为最新时, 检查本地头像是否存在
    private static boolean checkLocalAvatarImage() {
        Log.d(TAG, "检测本地头像是否存在");
        File avatarImageFile = new File(SDCardUtils.getAvatarImage(SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "avatar")));
        return avatarImageFile.exists();
    }

    //根据token是否有效决定下一活动
    private static void nextActivity(final Activity startAct) {
        if (SharedPreferencesUtils.getStoredMessage(UMapplication.getContext(), "hasLogin").equals("false")) {
            startAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(startAct, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    UMapplication.getContext().startActivity(intent);
                    startAct.finish();
                }
            });
        } else {
            startAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(startAct, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    UMapplication.getContext().startActivity(intent);
                    startAct.finish();
                }
            });
        }
    }



    //根据传入参数类决定下一启动活动
    private static void nextActivity(final Activity startAct, final Class endAct) {
        startAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(startAct, endAct);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UMapplication.getContext().startActivity(intent);
                startAct.finish();
            }
        });
    }

    //提取字符串中的所有数字作为广告名
    private static String getNumbers(String adName) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(adName);
        return m.replaceAll("").trim();
    }
}