package com.android.papers.qmkl_android.util;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.activity.AdsActivity;
import com.android.papers.qmkl_android.activity.LoginActivity;
import com.android.papers.qmkl_android.activity.MainActivity;
import com.android.papers.qmkl_android.activity.UserInfoActivity;
import com.android.papers.qmkl_android.impl.PostAds;
import com.android.papers.qmkl_android.impl.PostAllAcademies;
import com.android.papers.qmkl_android.impl.PostAllColleges;
import com.android.papers.qmkl_android.impl.PostExitLogin;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.impl.PostUpdateUserInfo;
import com.android.papers.qmkl_android.impl.PostUserAvatar;
import com.android.papers.qmkl_android.impl.PostUserInfo;
import com.android.papers.qmkl_android.model.AcademiesOrCollegesRes;
import com.android.papers.qmkl_android.model.AdData;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.model.UserInfoRes;
import com.android.papers.qmkl_android.requestModel.ExitLoginRequest;
import com.android.papers.qmkl_android.requestModel.LoginRequest;
import com.android.papers.qmkl_android.requestModel.QueryAcademiesRequest;
import com.android.papers.qmkl_android.requestModel.TokenRequest;
import com.android.papers.qmkl_android.requestModel.UpdateUserRequest;
import com.zyao89.view.zloading.ZLoadingDialog;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
    private static final int NICKNAME=1;
    private static final int GENDER = 2;
    private static final int ENTERYEAR=3;
    private static final int COLLEGE = 4;
    private static final int ACADEMY=5;

    private static final String TAG = ".RetrofitUtils";
    private static String oldAdName,newAdName,adPath,avatarPath;
    //获取广告
    public static void postAd(final Context context, final Activity startAct){
        PostAds request = retrofit.create(PostAds.class);
        request.getCall().enqueue(new Callback<ResponseInfo<AdData>>() {
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
                        Objects.requireNonNull(response.body()).getData().getFallback());
                SharedPreferencesUtils.setStoredMessage(context,"adtitle",
                        response.body().getData().getTitle());
                Log.d(TAG, response.body().getData().getTitle());

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
                                e.printStackTrace();

                                //缓存失败，进入登录界面或者主界面
                                Toast.makeText(startAct,"缓存广告失败,请反馈给开发者",Toast.LENGTH_SHORT).show();
                                try {
                                    Thread.sleep(2000);
                                    } catch (InterruptedException e2) {
                                        e.printStackTrace();
                                    }
                                    nextActivity(context,startAct);
                             }
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

        //创建 网络请求接口 的实例
        PostLogin request = retrofit.create(PostLogin.class);
        Call<ResponseInfo> call = request.getCall(r);
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
            PostLogin request = retrofit.create(PostLogin.class);
            Call<ResponseInfo> call = request.getTokenCall(new TokenRequest(token));
            call.enqueue(new Callback<ResponseInfo>() {
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

    //通过token值返回当前登录用户的信息，并显示用户头像等信息
    public static void postUserInfo(final Context context, String token, final CircleImageView headImg, final TextView userName, final TextView userCollege ,final ZLoadingDialog dialog){

        if(token!=null){
            PostUserInfo request = retrofit.create(PostUserInfo.class);
            Call<UserInfoRes> call = request.getCall(new TokenRequest(token));
            call.enqueue(new Callback<UserInfoRes>() {
                @Override
                public void onResponse(@NonNull Call<UserInfoRes> call, @NonNull final Response<UserInfoRes> response) {
                    //本地头像不存在或头像已上传更新，重新缓存头像信息并显示
                    if (!checkLocalAvatarImage() || SharedPreferencesUtils.getStoredMessage(context,"avatar")==null
                            || (SharedPreferencesUtils.getStoredMessage(context,"avatar")!=null
                            && !SharedPreferencesUtils.getStoredMessage(context,"avatar").equals(response.body().getData().getAcademy()))) {
                        SharedPreferencesUtils.setStoredMessage(context,"nickname",response.body().getData().getNickname());
                        SharedPreferencesUtils.setStoredMessage(context,"academy",response.body().getData().getAcademy());
                        SharedPreferencesUtils.setStoredMessage(context,"avatar",response.body().getData().getAvatar());
                        SharedPreferencesUtils.setStoredMessage(context,"college",response.body().getData().getCollege());
                        SharedPreferencesUtils.setStoredMessage(context,"enterYear",response.body().getData().getEnteYear());
                        SharedPreferencesUtils.setStoredMessage(context,"gender",response.body().getData().getGender());
                        SharedPreferencesUtils.setStoredMessage(context,"phone",response.body().getData().getPhone());
                        SharedPreferencesUtils.setStoredMessage(context,"username",response.body().getData().getUsername());
                        userName.setText(response.body().getData().getNickname());
                        userCollege.setText(response.body().getData().getCollege());
                        avatarPath=context.getString(R.string.user_info_url)+response.body().getData().getAvatar();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    DownLoader.downloadFile(new File(SDCardUtils.getAvatarImage(response.body().getData().getAvatar())),
                                            avatarPath);
                                    final Drawable drawable=Drawable.createFromPath(SDCardUtils.getAvatarImage(response.body().getData().getAvatar()));

                                    headImg.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            headImg.setImageDrawable(drawable);
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    //本地照片存在且未更新头像
                    else {
                        final Drawable drawable=Drawable.createFromPath(SDCardUtils.getAvatarImage(response.body().getData().getAvatar()));

                        headImg.post(new Runnable() {
                            @Override
                            public void run() {
                                headImg.setImageDrawable(drawable);
                            }
                        });
                    }
                    dialog.dismiss();

                }
                //请求失败时回调
                @Override
                public void onFailure(@NonNull Call<UserInfoRes> call, @NonNull Throwable t) {
                    Log.d(TAG, "请求失败");
                    Toast.makeText(context,"服务器请求失败",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
        else{
            Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    //传入token值和用户信息并更新
    public static void postUpdateUser(final int flag,final Context context, final UpdateUserRequest userInfo, final AlertDialog alertDialog, final TextView textView,final ZLoadingDialog dialog){

        PostUpdateUserInfo request = retrofit.create(PostUpdateUserInfo.class);
        Call<ResponseInfo> call = request.getCall(userInfo);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull final Response<ResponseInfo> response) {
                Log.d(TAG, response.body().getMsg());
                int responseCode=Integer.parseInt(response.body().getCode());
                if(responseCode==successCode){
                    switch (flag){
                        //修改昵称
                        case NICKNAME:
                            textView.setText(userInfo.getUser().getNickname());
                            SharedPreferencesUtils.setStoredMessage(context,"nickname",userInfo.getUser().getNickname());
                            alertDialog.dismiss();
                            break;
                        //修改性别
                        case GENDER:
                            textView.setText(userInfo.getUser().getGender());
                            SharedPreferencesUtils.setStoredMessage(context,"gender",userInfo.getUser().getGender());
                            break;
                        //修改入学年份
                        case ENTERYEAR:
                            textView.setText(userInfo.getUser().getEnterYear());
                            SharedPreferencesUtils.setStoredMessage(context,"enterYear",userInfo.getUser().getEnterYear());
                            break;
                        //修改所在大学
                        case COLLEGE:
                            break;
                        //修改所在学院
                        case ACADEMY:
                            break;
                    }
                }
                else if (response.body().getMsg().equals("昵称已经被占用")){
                    Toast.makeText(context,"昵称已经被占用",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context,"修改失败",Toast.LENGTH_SHORT).show();
                }
                if(dialog!=null)dialog.dismiss();
            }
            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Log.d(TAG, "请求失败");
                Toast.makeText(context,"服务器请求失败",Toast.LENGTH_SHORT).show();
                if(dialog!=null)dialog.dismiss();
            }
        });
    }

    //传入用户token值该学校所有专业
    public static void postAllAcademies(final Context context, QueryAcademiesRequest academiesRequest, final AlertDialog.Builder builder, final TextView college, final TextView academy,final ZLoadingDialog dialog,boolean isUpCollege){
        //监听返回键
        if(isUpCollege){
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface DialogInterface, int keyCode, KeyEvent event) {
                    if(keyCode==KeyEvent.KEYCODE_BACK){
                        college.setText(SharedPreferencesUtils.getStoredMessage(context,"lastCollege"));
                        SharedPreferencesUtils.setStoredMessage(context,"college",SharedPreferencesUtils.getStoredMessage(context,"lastCollege"));
                        UpdateUserRequest userRequest=UserInfoActivity.getUserRequest(context,college.getText().toString(),COLLEGE);
                        RetrofitUtils.postUpdateUser(COLLEGE,context,userRequest,null,academy,dialog);
                    }
                    return false;
                }
            });
        }
        else {
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface DialogInterface, int keyCode, KeyEvent event) {
                    if(keyCode==KeyEvent.KEYCODE_BACK){
                        dialog.dismiss();
                    }
                    return false;
                }
            });
        }
        if(UserInfoActivity.academies==null){
            PostAllAcademies request = retrofit.create(PostAllAcademies.class);
            Call<AcademiesOrCollegesRes> call = request.getCall(academiesRequest);
            call.enqueue(new Callback<AcademiesOrCollegesRes>() {
                @Override
                public void onResponse(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull final Response<AcademiesOrCollegesRes> response) {
                    int responseCode=Integer.parseInt(response.body().getCode());
                    if(responseCode==successCode){
                        UserInfoActivity.academies=response.body().getData();
                        // 设置参数
                        builder.setTitle("选择学院")
                                .setItems(UserInfoActivity.academies, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        academy.setText(UserInfoActivity.academies[which]);
                                        SharedPreferencesUtils.setStoredMessage(context,"academy",UserInfoActivity.academies[which]);
                                        UpdateUserRequest userRequest=UserInfoActivity.getUserRequest(context,academy.getText().toString(),ACADEMY);
                                        RetrofitUtils.postUpdateUser(ACADEMY,context,userRequest,null,academy,dialog);
                                    }
                                });

                        AlertDialog alertDialog=builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                    else{
                        Toast.makeText(context,"获取学院信息失败",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull Throwable t) {
                    Log.d(TAG, "请求失败");
                    Toast.makeText(context,"服务器请求失败",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
        else {
            // 设置参数
            builder.setTitle("选择学院")
                    .setItems(UserInfoActivity.academies, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            academy.setText(UserInfoActivity.academies[which]);
                            SharedPreferencesUtils.setStoredMessage(context,"academy",UserInfoActivity.academies[which]);
                            UpdateUserRequest userRequest=UserInfoActivity.getUserRequest(context,academy.getText().toString(),ACADEMY);
                            RetrofitUtils.postUpdateUser(ACADEMY,context,userRequest,null,academy,dialog);
                        }
                    });
            AlertDialog alertDialog=builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

    }

    //传入用户token值获取所有学校
    public static void postAllColleges(final Context context, final TokenRequest tokenRequest, final AlertDialog.Builder builder, final TextView college, final TextView academy, final ZLoadingDialog dialog){
        //监听返回键
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface DialogInterface, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_BACK){
                    dialog.dismiss();
                }
                return false;
            }
        });

        if(UserInfoActivity.collgees==null){
            PostAllColleges request = retrofit.create(PostAllColleges.class);
            Call<AcademiesOrCollegesRes> call = request.getCall(tokenRequest);
            call.enqueue(new Callback<AcademiesOrCollegesRes>() {
                @Override
                public void onResponse(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull final Response<AcademiesOrCollegesRes> response) {
                    int responseCode=Integer.parseInt(response.body().getCode());
                    if(responseCode==successCode){
                        UserInfoActivity.collgees=response.body().getData();
                        // 设置参数
                        builder.setTitle("选择学校")
                                .setItems(UserInfoActivity.collgees, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        college.setText(UserInfoActivity.collgees[which]);
                                        SharedPreferencesUtils.setStoredMessage(context,"lastCollege",
                                                SharedPreferencesUtils.getStoredMessage(context,"college"));
                                        SharedPreferencesUtils.setStoredMessage(context,"college",UserInfoActivity.collgees[which]);
                                        UpdateUserRequest userRequest=UserInfoActivity.getUserRequest(context,college.getText().toString(),COLLEGE);
                                        RetrofitUtils.postUpdateUser(COLLEGE,context,userRequest,null,college,dialog);
                                        UserInfoActivity.academies=null;
                                        QueryAcademiesRequest academiesRequest=new QueryAcademiesRequest(
                                                SharedPreferencesUtils.getStoredMessage(context,"college"),
                                                SharedPreferencesUtils.getStoredMessage(context,"token")
                                        );
                                        postAllAcademies(context,academiesRequest,builder,college,academy,dialog,true);
                                    }
                                });
                        AlertDialog alertDialog=builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                    else{
                        Toast.makeText(context,"获取学校信息失败",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<AcademiesOrCollegesRes> call, @NonNull Throwable t) {
                    Log.d(TAG, "请求失败");
                    Toast.makeText(context,"服务器请求失败",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
        else {
            builder.setTitle("选择学校")
                    .setItems(UserInfoActivity.collgees, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            college.setText(UserInfoActivity.collgees[which]);
                            SharedPreferencesUtils.setStoredMessage(context,"lastCollege",
                                    SharedPreferencesUtils.getStoredMessage(context,"college"));
                            SharedPreferencesUtils.setStoredMessage(context,"college",UserInfoActivity.collgees[which]);
                            UpdateUserRequest userRequest=UserInfoActivity.getUserRequest(context,college.getText().toString(),COLLEGE);
                            RetrofitUtils.postUpdateUser(COLLEGE,context,userRequest,null,college,dialog);
                            UserInfoActivity.academies=null;
                            QueryAcademiesRequest academiesRequest=new QueryAcademiesRequest(
                                    SharedPreferencesUtils.getStoredMessage(context,"college"),
                                    SharedPreferencesUtils.getStoredMessage(context,"token")
                            );
                            postAllAcademies(context,academiesRequest,builder,college,academy,dialog,true);
                        }
                    });
            AlertDialog alertDialog=builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

    }



    //传入用户名退出登录
    //此函数一直进入onFailure，具体原因未知
    public static void postExitLogin(final Context context, String username, final Activity startAct, final ZLoadingDialog dialog){

            PostExitLogin request = retrofit.create(PostExitLogin.class);
            ExitLoginRequest exitLoginRequest=new ExitLoginRequest(username);
            Call<String> call = request.getCall(exitLoginRequest);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    SharedPreferencesUtils.setStoredMessage(context,"hasLogin","false");
                    SharedPreferencesUtils.setStoredMessage(context,"token",null);
                    Toast.makeText(context,"退出登录",Toast.LENGTH_SHORT).show();

                    nextActivity(context,startAct,LoginActivity.class);
                    dialog.dismiss();
                }
                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d(TAG, "请求失败");
                    Toast.makeText(context,"退出登录",Toast.LENGTH_SHORT).show();
                    SharedPreferencesUtils.setStoredMessage(context,"hasLogin","false");
                    SharedPreferencesUtils.setStoredMessage(context,"token",null);
                    nextActivity(context,startAct,LoginActivity.class);
                    dialog.dismiss();
                }
            });
    }

    //传入用户token和图片，上传用户头像
    public static void postUserAvatar(final Context context,String imagePath){
        PostUserAvatar request = retrofit.create(PostUserAvatar.class);
        File avatar=new File(imagePath);
        String token=SharedPreferencesUtils.getStoredMessage(context,"token");

//设置Content-Type:application/octet-stream
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), avatar);
//设置Content-Disposition:form-data; name="photo"; filename="xuezhiqian.png"
        MultipartBody.Part avatarRequest = MultipartBody.Part.createFormData("avatar", avatar.getName(), photoRequestBody);

        RequestBody tokenRequest = RequestBody.create(MediaType.parse("multipart/form-data"), token);
        Call<ResponseInfo<String>> call = request.getCall(avatarRequest,tokenRequest);
        call.enqueue(new Callback<ResponseInfo<String>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo<String>> call, @NonNull final Response<ResponseInfo<String>> response) {
                SharedPreferencesUtils.setStoredMessage(context,"avatar",response.body().getData());
                avatarPath=context.getString(R.string.user_info_url)+response.body().getData();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DownLoader.downloadFile(new File(SDCardUtils.getAvatarImage(response.body().getData())),
                                    avatarPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            @Override
            public void onFailure(@NonNull Call<ResponseInfo<String>> call, @NonNull Throwable t) {
                Log.d(TAG, "请求失败");
                Toast.makeText(context,"上传头像失败",Toast.LENGTH_SHORT).show();
            }
        });
    }


    //获取远程信息失败或者广告版本为最新时, 检查本地广告图片是否存在
    private static boolean checkLocalADImage() {
        Log.d(TAG, "检测本地广告图像是否存在");
        File adImageFile = new File(SDCardUtils.getADImage(newAdName));
        return adImageFile.exists();
    }

    //获取远程信息失败或者广告版本为最新时, 检查本地广告图片是否存在
    private static boolean checkLocalAvatarImage() {
        Log.d(TAG, "检测本地广告图像是否存在");
        File avatarImageFile = new File(SDCardUtils.getCachePath());
        return avatarImageFile.exists();
    }

    //根据token是否有效决定下一活动
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

    //根据传入参数类决定下一启动活动
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
