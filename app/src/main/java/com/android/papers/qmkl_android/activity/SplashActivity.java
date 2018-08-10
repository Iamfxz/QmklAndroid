package com.android.papers.qmkl_android.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.util.ActManager;
import com.android.papers.qmkl_android.util.PermissionUtils;
import com.android.papers.qmkl_android.util.RetrofitUtils;


import com.android.papers.qmkl_android.util.SharedPreferencesUtils;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class SplashActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private static final String TAG = "SplashActivity";
    private static final String[] requestPermissions = {
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActManager.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //获取SD卡读写权限
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE);

        RetrofitUtils.postLogin(this,SharedPreferencesUtils.getStoredMessage(getApplication(),"token"));

        //若已获取SD卡读写权限
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            RetrofitUtils.postAd(this,SplashActivity.this);
        }



    }


    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }



    //对用户做出的权限选择做监听
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //请求读写SD卡权限
        if (requestCode == 8) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    Log.d(TAG, "成功");
                    RetrofitUtils.postAd(this,SplashActivity.this);

                } else {
                    Log.d(TAG, "失败");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            nextActivity(getApplicationContext(), SplashActivity.this);
                        }
                    }).start();
                }
            }
        }
    }

    private void nextActivity(Class clazz) {
        final Intent intent = new Intent(SplashActivity.this, clazz);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(intent);

                    }
                });
            }
        }).start();
    }

    //根据hasLogin判断之前是否登录过决定下一启动活动
    private static void nextActivity(final Context context, final Activity startAct){

        if(SharedPreferencesUtils.getStoredMessage(context,"hasLogin").equals("false")){
            startAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startAct.finish();
                    Intent intent=new Intent(startAct,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });
        }
        else {
            startAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startAct.finish();
                    Intent intent=new Intent(startAct,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });
        }
    }
}