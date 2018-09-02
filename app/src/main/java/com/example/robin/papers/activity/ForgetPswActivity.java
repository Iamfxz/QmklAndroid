package com.example.robin.papers.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robin.papers.R;
import com.example.robin.papers.util.MyTextWatcher;
import com.example.robin.papers.util.RetrofitUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//找回密码界面一
public class ForgetPswActivity extends BaseActivity {

    private static final String TAG = "ForgetPswActivity";
    private static final String FORGET_PSW_MSG="修改密码";

    @BindView(R.id.user_phone)
    TextInputLayout userPhone;
    @BindView(R.id.verification_code)
    TextInputLayout verificationCode;
    @BindView(R.id.new_psw)
    TextInputLayout newPsw;
    @BindView(R.id.confirm_new_psw)
    TextInputLayout confirmPsw;
    @BindView(R.id.submit)
    Button submitBtn;
    @BindView(R.id.send_code)
    TextView sendCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpsw);
        ButterKnife.bind(this);

        submitBtn.setEnabled(false);

        //帐号、验证码、新密码、确认新密码都不为空时,登录按钮变色
        Objects.requireNonNull(userPhone.getEditText()).addTextChangedListener(new MyTextWatcher(this,submitBtn,userPhone,verificationCode,newPsw,confirmPsw));
        Objects.requireNonNull(verificationCode.getEditText()).addTextChangedListener(new MyTextWatcher(this,submitBtn,userPhone,verificationCode,newPsw,confirmPsw));
        Objects.requireNonNull(newPsw.getEditText()).addTextChangedListener(new MyTextWatcher(this,submitBtn,userPhone,verificationCode,newPsw,confirmPsw));
        Objects.requireNonNull(confirmPsw.getEditText()).addTextChangedListener(new MyTextWatcher(this,submitBtn,userPhone,verificationCode,newPsw,confirmPsw));

    }


    @OnClick({R.id.submit,R.id.send_code,R.id.back})
    public void clickNext(View view){
        switch (view.getId()){
            case R.id.submit:
                if(Objects.requireNonNull(verificationCode.getEditText()).getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"验证码不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(!isRightPsw(Objects.requireNonNull(newPsw.getEditText()).getText().toString())){
                    Toast.makeText(getApplicationContext(),"密码必须由8-16位字符和数字组合构成",Toast.LENGTH_SHORT).show();
                }
                else if(!newPsw.getEditText().getText().toString().equals(Objects.requireNonNull(confirmPsw.getEditText()).getText().toString())){
                    Toast.makeText(getApplicationContext(),"两次密码输入不一致",Toast.LENGTH_SHORT).show();
                }
                else {
                    RetrofitUtils.postSetNewPsw(ForgetPswActivity.this,
                            Objects.requireNonNull(userPhone.getEditText()).getText().toString(),verificationCode.getEditText().getText().toString(),
                            newPsw.getEditText().getText().toString());
                }

                break;
            case R.id.send_code:
                if(Objects.requireNonNull(userPhone.getEditText()).getText().toString().length()==11) {
                    RetrofitUtils.postGetCode(userPhone.getEditText().getText().toString(), sendCodeBtn,FORGET_PSW_MSG);

                }
                else{
                    Toast.makeText(getApplicationContext(),"手机号长度不正确",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }


    //判断密码是否合法
    private boolean isRightPsw(String psw){
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isChar = false;//定义一个boolean值，用来表示是否包含字母
        //必须是8-16位密码
        if(psw.length()<8 || psw.length()>16){
            return false;
        }
        //同时包含字符和数字
        for(int i=0;i<psw.length();i++){
            if(Character.isDigit(psw.charAt(i))){
                isDigit=true;
            }
            else {
                isChar=true;
            }
        }
        return (isChar && isDigit);
    }
}
