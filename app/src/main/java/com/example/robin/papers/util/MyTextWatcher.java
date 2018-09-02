package com.example.robin.papers.util;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.robin.papers.R;

import java.util.Objects;

public class MyTextWatcher implements TextWatcher {

    private Context context;
    private Button changeBtn;
    private CheckBox agreeBtn;
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

    /**
     * TextWatcher监听器
     * @param context 上下文
     * @param changeBtn 要修改的按钮
     * @param agreeBtn 是否同意用户协议和隐私政策
     * @param textInputLayouts 输入布局，当所有均有输入时按钮变为可用
     */
    public MyTextWatcher(final Context context, final Button changeBtn, final CheckBox agreeBtn, final TextInputLayout...textInputLayouts) {
        this.context=context;
        this.changeBtn=changeBtn;
        this.agreeBtn=agreeBtn;
        this.textInputLayouts=textInputLayouts;
        agreeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    boolean isEnable=true;
                    for(int i=0;i<textInputLayouts.length;i++){
                        if(textInputLayouts[i].getEditText().getText().toString().length()==0){
                            changeBtn.setBackgroundColor(context.getResources().getColor(R.color.btn_unable));
                            changeBtn.setEnabled(false);
                            isEnable=false;
                        }
                    }
                    if(agreeBtn!=null && !isChecked){
                        isEnable=false;
                    }
                    if(isEnable){
                        changeBtn.setBackgroundColor(context.getResources().getColor(R.color.blue));
                        changeBtn.setEnabled(true);
                    }
                }
                else {
                    changeBtn.setBackgroundColor(context.getResources().getColor(R.color.btn_unable));
                    changeBtn.setEnabled(false);
                }
            }
        });
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
        if(agreeBtn!=null && !agreeBtn.isChecked()){
            isEnable=false;
        }
        if(isEnable){
            changeBtn.setBackgroundColor(context.getResources().getColor(R.color.blue));
            changeBtn.setEnabled(true);
        }
    }
}