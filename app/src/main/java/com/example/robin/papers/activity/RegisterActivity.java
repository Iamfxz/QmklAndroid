package com.example.robin.papers.activity;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.robin.papers.R;
import com.example.robin.papers.util.MyTextWatcher;
import com.example.robin.papers.util.RetrofitUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//用户注册界面一
public class RegisterActivity extends BaseActivity{

    private static final String REGISTER_MSG="注册";

    @BindView(R.id.user_phone)
    TextInputLayout userPhone;
    @BindView(R.id.verification_code)
    TextInputLayout verificationCode;
    @BindView(R.id.send_code)
    Button sendCodeBtn;
    @BindView(R.id.next)
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        nextBtn.setEnabled(false);
        //帐号、验证码都不为空时,下一步按钮变色
        Objects.requireNonNull(userPhone.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userPhone,verificationCode));
        Objects.requireNonNull(verificationCode.getEditText()).addTextChangedListener(new MyTextWatcher(this,nextBtn,userPhone,verificationCode));



    }

    @OnClick({R.id.next,R.id.send_code,R.id.back})
    public void clickNext(View view){
        switch (view.getId()){
            case R.id.next:
                if(Objects.requireNonNull(userPhone.getEditText()).getText().toString().length()!=11){
                    Toast.makeText(getApplicationContext(),"手机号长度不正确",Toast.LENGTH_SHORT).show();
                }
                if(Objects.requireNonNull(verificationCode.getEditText()).getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"验证码不能为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    RetrofitUtils.postRegister(RegisterActivity.this,
                            userPhone.getEditText().getText().toString(),verificationCode.getEditText().getText().toString());
                }
                break;
            case R.id.send_code:
                if(Objects.requireNonNull(userPhone.getEditText()).getText().toString().length()==11) {
                    RetrofitUtils.postGetCode(userPhone.getEditText().getText().toString(), sendCodeBtn,REGISTER_MSG);
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
}
