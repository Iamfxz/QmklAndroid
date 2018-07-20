package com.android.papers.qmkl_android.activity;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.papers.qmkl_android.R;
import com.android.papers.qmkl_android.impl.PostLogin;
import com.android.papers.qmkl_android.model.ResponseInfo;
import com.android.papers.qmkl_android.requestModel.LoginRequest;
import com.android.papers.qmkl_android.util.SHAarithmetic;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends BaseActivity {
    private static final int errorCode=404;
    private static final int successCode = 200;
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
                    Toast.makeText(getApplicationContext(),"手机号长度不正确",Toast.LENGTH_SHORT).show();
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
        String SHApassword = SHAarithmetic.encode(password);//密码加密
        LoginRequest req = new LoginRequest(username,SHApassword);//账号密码封装
        postLogin(this, req);//发送登录请求验证

        //使用com.zyao89:zloading:1.1.2引用別人的加载动画
        ZLoadingDialog dialog = new ZLoadingDialog(this);
        dialog.setLoadingBuilder(Z_TYPE.STAR_LOADING)//设置类型
                .setLoadingColor(getResources().getColor(R.color.blue))//颜色
                .setHintText("Login...")
                .show();
    }

    //登录调用API发送登录数据给服务器
    public void postLogin(Context context, LoginRequest r){

        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))// 设置 网络请求 Url,0.0.4版本
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

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
                    Toast.makeText(getApplicationContext(),"请检查账号密码是否准确",Toast.LENGTH_SHORT).show();
                }else if (resultCode == successCode){
                    String token = Objects.requireNonNull(response.body()).getData().toString();
                    //TODO
                    //接下来进入登录界面
                }else{
                Toast.makeText(getApplicationContext(),"发生未知错误,请反馈给开发者",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(),"服务器请求失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
