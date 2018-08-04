package com.android.papers.qmkl_android.activity;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;

import com.android.papers.qmkl_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

//找回密码界面二
public class SetNewPswActivity extends BaseActivity {


    @BindView(R.id.new_psw)
    TextInputLayout newPsw;
    @BindView(R.id.confirm_new_psw)
    TextInputLayout confirmPsw;
    @BindView(R.id.submit)
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpsw);
        ButterKnife.bind(this);


    }


}
