package com.android.papers.qmkl_android.activity;
import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.requestModel.LoginRequest;
import com.android.papers.qmkl_android.util.ActManager;
import com.android.papers.qmkl_android.util.MyTextWatcher;
import com.android.papers.qmkl_android.util.RetrofitUtils;
import com.android.papers.qmkl_android.util.SHAArithmetic;
import com.android.papers.qmkl_android.util.SharedPreferencesUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    private static final int errorCode=404;
    private static final int successCode = 200;
    private static final String TAG = "LoginActivity";

    private static Boolean isExit = false; //是否退出

    @BindView(R.id.back)
    ImageView back;//返回
    @BindView(R.id.user_phone_num)
    TextInputLayout userPhoneNum;
    @BindView(R.id.user_psw)
    TextInputLayout userPsw;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.user_info)
    LinearLayout userInfo;
    @BindView(R.id.forget_psw)
    TextView forgetPsw;
    @BindView(R.id.register)
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActManager.addActivity(this);
//        setBarColor(R.color.white); //沉浸式状态栏设置颜色
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //"忘记密码" 加下划线
        forgetPsw.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        forgetPsw.getPaint().setAntiAlias(true);//抗锯齿

        //"注册新用户" 加下划线
        register.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        register.getPaint().setAntiAlias(true);//抗锯齿

        //闪退的通过try可以解决，但是如果是必要执行的不要使用try
        try{
            if(SharedPreferencesUtils.getStoredMessage(this,"phone").equals("")
                    ||SharedPreferencesUtils.getStoredMessage(this,"phone")==null){
                //设置默认账号
                userPhoneNum.getEditText().setText(SharedPreferencesUtils.getStoredMessage(this,"phone"));
                //初始焦点位于输入密码位置
                userPsw.getEditText().requestFocus();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //初始时登录按钮不可用
        loginBtn.setEnabled(false);
        //帐号密码都不为空时,登录按钮变色
        Objects.requireNonNull(userPhoneNum.getEditText()).addTextChangedListener(new MyTextWatcher(this,loginBtn,userPhoneNum,userPsw));
        Objects.requireNonNull(userPsw.getEditText()).addTextChangedListener(new MyTextWatcher(this,loginBtn,userPhoneNum,userPsw));

    }



    @OnClick({R.id.back, R.id.user_phone_num, R.id.user_psw, R.id.login_btn, R.id.user_info, R.id.forget_psw,R.id.register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                // 创建构建器
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // 设置参数
                builder.setTitle("确定要退出？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {// 积极

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ActManager.AppExit(getApplicationContext());
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {// 消极

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                    }
                });
                builder.create().show();
                break;
            case R.id.user_phone_num:
                break;
            case R.id.user_psw:
                userPsw.setErrorEnabled(false);
                break;
            case R.id.login_btn:
                if (Objects.requireNonNull(userPhoneNum.getEditText()).getText().toString().length() != 11) {
                    Toast.makeText(getApplicationContext(),"手机号长度不正确",Toast.LENGTH_SHORT).show();
                } else if ((Objects.requireNonNull(userPsw.getEditText()).getText().toString().length() < 6) &&
                        (userPsw.getEditText().getText().toString().length()>16)) {
                    Toast.makeText(getApplicationContext(),"密码要 6至16 位",Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "点击登录");
                    doLogin(Objects.requireNonNull(userPhoneNum.getEditText()).getText().toString(),
                            Objects.requireNonNull(userPsw.getEditText()).getText().toString());
                }
                break;
            case R.id.user_info:
                break;
            case R.id.forget_psw:
                startActivity(new Intent(LoginActivity.this,ForgetPswActivity.class));  //忘记密码 进入短信验证并找回
                break;
            case R.id.register:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));  //用户注册 进入短信验证
                break;
        }
    }


    private void doLogin(String username,String password) {
        String SHApassword = SHAArithmetic.encode(password);//密码加密
        LoginRequest req = new LoginRequest(username,SHApassword);//账号密码封装

        //使用com.zyao89:zloading:1.1.2引用別人的加载动画
        ZLoadingDialog dialog = new ZLoadingDialog(this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                .setHintText("Login...")
                .setCanceledOnTouchOutside(false)
                .show();

        RetrofitUtils.postLogin(LoginActivity.this,this, req,dialog);//发送登录请求验证
    }

    @Override
    public void onBackPressed() {
        Timer tExit = null;
        if (isExit == false) {
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
            ActManager.AppExit(getApplicationContext());
        }
    }
}
