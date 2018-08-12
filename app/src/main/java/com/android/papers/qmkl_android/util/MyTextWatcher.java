package com.android.papers.qmkl_android.util;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import com.android.papers.qmkl_android.R;

import java.util.Objects;

public class MyTextWatcher implements TextWatcher {

    private Context context;
    private Button changeBtn;
    private TextInputLayout[] textInputLayouts;

    /**
     * TextWatcher监听器
     * @param context 上下文
     * @param changeBtn 要修改的按钮
     * @param textInputLayouts 输入布局，当所有均有输入时按钮变为可用
     */
    public MyTextWatcher(Context context, Button changeBtn,TextInputLayout...textInputLayouts) {
        this.context=context;
        this.changeBtn=changeBtn;
        this.textInputLayouts=textInputLayouts;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
    @Override
    public void afterTextChanged(Editable s) {
        boolean isEnable=true;
        for(int i=0;i<textInputLayouts.length;i++){
            if(textInputLayouts[i].getEditText().getText().toString().length()==0){
                changeBtn.setBackgroundColor(context.getResources().getColor(R.color.btn_unable));
                changeBtn.setEnabled(false);
                isEnable=false;
            }
        }
        if(isEnable){
            changeBtn.setBackgroundColor(context.getResources().getColor(R.color.blue));
            changeBtn.setEnabled(true);
        }
    }
}