package com.example.robin.papers.activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robin.papers.R;
import com.example.robin.papers.requestModel.LoginRequest;
import com.example.robin.papers.requestModel.UMengLoginRequest;
import com.example.robin.papers.util.ActivityManager;
import com.example.robin.papers.util.MyTextWatcher;
import com.example.robin.papers.util.RetrofitUtils;
import com.example.robin.papers.util.SHAArithmetic;
import com.example.robin.papers.util.SharedPreferencesUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.robin.papers.util.ConstantUtils.*;

public class LoginActivity extends BaseActivity {
    private static final int errorCode=404;
    private static final int successCode = 200;
    private static final String TAG = "LoginActivity";
    private static Boolean isExit = false; //是否退出
    public static Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;

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
        ActivityManager.addActivity(this);
//        setBarColor(R.color.white); //沉浸式状态栏设置颜色
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        forgetPsw.getPaint().setAntiAlias(true);//抗锯齿
        register.getPaint().setAntiAlias(true);//抗锯齿

        //闪退的通过try可以解决，但是如果是必要执行的不要使用try
        try{
            if(SharedPreferencesUtils.getStoredMessage(this,"phone")!=null
                    && SharedPreferencesUtils.getStoredMessage(this,"phone").equals("")){
                //设置默认账号
                userPhoneNum.getEditText().setText(SharedPreferencesUtils.getStoredMessage(this,"phone"));
                //初始焦点位于输入密码位置
                userPsw.getEditText().requestFocus();
            }
        }catch (Exception e){
            e.printStackTrace();

        }
        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());


        //初始时登录按钮不可用
        loginBtn.setEnabled(false);
        //帐号密码都不为空时,登录按钮变色
        Objects.requireNonNull(userPhoneNum.getEditText()).addTextChangedListener(new MyTextWatcher(this,loginBtn,userPhoneNum,userPsw));
        Objects.requireNonNull(userPsw.getEditText()).addTextChangedListener(new MyTextWatcher(this,loginBtn,userPhoneNum,userPsw));


    }



    @OnClick({R.id.back, R.id.user_phone_num, R.id.user_psw, R.id.login_btn, R.id.user_info,
            R.id.forget_psw,R.id.register,R.id.qq_quick_login})
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
                                ActivityManager.AppExit(getApplicationContext());
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
            case R.id.qq_quick_login:
//                友盟
//                if(isQQClientAvailable(LoginActivity.this)){
//                    UMShareAPI.get(LoginActivity.this).getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ,new MyUMAuthListener(LoginActivity.this,LoginActivity.this,"qq",true));
//                }
//                else{
//                    Toast.makeText(LoginActivity.this,"未安装qq客户端",Toast.LENGTH_SHORT).show();
//                }
//                break;
                /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
                 官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
                 第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
                mIUiListener = new BaseUiListener();

                String openID = SharedPreferencesUtils.getStoredMessage(LoginActivity.this,"openid");
                String accessToken = SharedPreferencesUtils.getStoredMessage(LoginActivity.this,"access_token");
                String expires = SharedPreferencesUtils.getStoredMessage(LoginActivity.this,"expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken,expires);
                //all表示获取所有权限
                mTencent.login(LoginActivity.this,"all", mIUiListener);
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
            ActivityManager.AppExit(getApplicationContext());
        }
    }
//    友盟
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        //QQ第三方登录调用
//        UMShareAPI.get(LoginActivity.this).onActivityResult(requestCode,resultCode,data);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode,resultCode,data,mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 自定义监听器实现IUiListener接口后，需要实现的3个方法
     * onComplete完成 onError错误 onCancel取消
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                final String openID = obj.getString("openid");
                SharedPreferencesUtils.setStoredMessage(LoginActivity.this,"uid",openID);
                //openid作为头像名称
                SharedPreferencesUtils.setStoredMessage(LoginActivity.this,"avatar",openID);
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                SharedPreferencesUtils.setStoredMessage(LoginActivity.this,"accessToken",accessToken);
                SharedPreferencesUtils.setStoredMessage(LoginActivity.this,"expires",expires);
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken,expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(),qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        JSONObject res = (JSONObject) response;
                        Log.e(TAG,"登录成功"+response.toString());
                        try {
                            SharedPreferencesUtils.setStoredMessage(LoginActivity.this,"nickname",res.getString("nickname"));
                            SharedPreferencesUtils.setStoredMessage(LoginActivity.this,"gender",res.getString("gender"));
                            SharedPreferencesUtils.setStoredMessage(LoginActivity.this,"avatarUrl",res.getString("figureurl_2"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        UMengLoginRequest uMengLoginRequest=new UMengLoginRequest(openID,"qq");
                        RetrofitUtils.postAuthLogin(LoginActivity.this,uMengLoginRequest,LoginActivity.this);
                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.e(TAG,"登录失败"+uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG,"登录取消");

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();

        }

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
