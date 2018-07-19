package com.android.papers.qmkl_android.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.requestModel.LoginRequest;
import com.android.papers.qmkl_android.util.RetrofitUtils;
import com.android.papers.qmkl_android.util.SHAarithmetic;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBarColor(R.color.white); //沉浸式状态栏设置颜色
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //"忘记密码" 加下划线
        forgetPsw.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        forgetPsw.getPaint().setAntiAlias(true);//抗锯齿

        //帐号密码都不为空时,登录按钮变色
        Objects.requireNonNull(userPhoneNum.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((userPhoneNum.getEditText().getText().toString().length()>0) &&
                        (Objects.requireNonNull(userPsw.getEditText()).getText().toString().length()>0))
                {
                    loginBtn.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else {
                    loginBtn.setBackgroundColor(getResources().getColor(R.color.btn_unable));
                }
            }
        });

        Objects.requireNonNull(userPsw.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((userPhoneNum.getEditText().getText().toString().length()>0) &&
                        (userPsw.getEditText().getText().toString().length()>0)){
                    loginBtn.setBackgroundColor(getResources().getColor(R.color.blue));
                }else{
                    loginBtn.setBackgroundColor(getResources().getColor(R.color.btn_unable));
                }
            }
        });


    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @OnClick({R.id.back, R.id.user_phone_num, R.id.user_psw, R.id.login_btn, R.id.user_info, R.id.forget_psw})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.user_phone_num:
                break;
            case R.id.user_psw:
                userPsw.setErrorEnabled(false);
                break;
            case R.id.login_btn:
                if (Objects.requireNonNull(userPhoneNum.getEditText()).getText().toString().length() != 11) {
                    Toast.makeText(getApplicationContext(),"手机号不正确",Toast.LENGTH_SHORT).show();
                } else if ((Objects.requireNonNull(userPsw.getEditText()).getText().toString().length() < 6) &&
                        (userPsw.getEditText().getText().toString().length()>16)) {
                    Toast.makeText(getApplicationContext(),"密码要 6至16 位",Toast.LENGTH_SHORT).show();
                } else {
                    doLogin(Objects.requireNonNull(userPhoneNum.getEditText()).getText().toString(),
                            Objects.requireNonNull(userPsw.getEditText()).getText().toString());
                }
                break;
            case R.id.user_info:
                break;
            case R.id.forget_psw:
//                startActivity(new Intent(LoginActivity.this,WebViewActivity.class));  //忘记密码 进入短信验证并找回
                break;
        }
    }

    private void doLogin(String username,String password) {
        String SHApassword = SHAarithmetic.encode(password);
        LoginRequest req = new LoginRequest(username,SHApassword);
        RetrofitUtils ru = new RetrofitUtils();

        ru.postLogin(this, req);

        //使用com.zyao89:zloading:1.1.2引用別人的加载动画
        ZLoadingDialog dialog = new ZLoadingDialog(this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                .setHintText("Login...")
                .show();
    }
}
