package com.example.robin.papers.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.widget.Button;

import com.example.robin.papers.R;

/**
 * 自定义倒计时类，实现Runnable接口
 */
public class CountDownTimer implements Runnable{


    private Handler mHandler = new Handler();
    private Button button;
    private int time;
    private int flag;
    private Context context;

    //flag为0时，该倒计时类控制跳过广告界面按钮
    //flag为1时，该倒计时类控制发送短信验证码界面按钮
    public CountDownTimer(int time, Button button,int flag){
        this.time=time;
        this.button=button;
        this.flag=flag;
    }
    public CountDownTimer(int time, Button button, int flag, Context context){
        this.time=time;
        this.button=button;
        this.flag=flag;
        this.context=context;
    }
    @Override
    public void run() {
        if(flag==1){
            button.post(new Runnable() {
                @Override
                public void run() {
                    button.setEnabled(false);
                    button.setBackgroundColor(context.getResources().getColor(R.color.btn_unable));
                }
            });
        }
        //倒计时开始，循环
        while (time > 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(flag==0){
                        button.setText("跳过(" + time + "秒)");
                    }
                    else if(flag==1){
                        button.setText(time + "秒后重新发送");
                    }

                }
            });
            try {
                Thread.sleep(1000); //强制线程休眠1秒，就是设置倒计时的间隔时间为1秒。
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time--;
        }
        if(flag==1){
            button.post(new Runnable() {
                @Override
                public void run() {
                    button.setText("重新发送");
                    button.setEnabled(true);
                    button.setBackgroundColor(context.getResources().getColor(R.color.green1));

                }
            });
        }

    }
}
