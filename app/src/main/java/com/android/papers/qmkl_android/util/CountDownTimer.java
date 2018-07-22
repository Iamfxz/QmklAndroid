package com.android.papers.qmkl_android.util;

import android.os.Handler;
import android.widget.Button;

/**
 * 自定义倒计时类，实现Runnable接口
 */
public class CountDownTimer implements Runnable{


    private Handler mHandler = new Handler();
    private Button skip;
    private int time=3;

    public CountDownTimer(int time, Button skip){
        this.time=time;
        this.skip=skip;
    }
    @Override
    public void run() {
        //倒计时开始，循环
        while (time > 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    skip.setText("跳过(" + time + "秒)");
                }
            });
            try {
                Thread.sleep(1000); //强制线程休眠1秒，就是设置倒计时的间隔时间为1秒。
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time--;
        }
    }
}
