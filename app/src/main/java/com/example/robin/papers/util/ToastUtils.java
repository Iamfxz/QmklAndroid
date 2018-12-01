package com.example.robin.papers.util;

import android.content.Context;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 凌子文 on 15/7/22.
 * Content Toast相关工具
 *      用于弹出各种提示的综合管理
 */
public class ToastUtils {

    private ToastUtils()
    {
        /** cannot be instantiated**/
        throw new UnsupportedOperationException("不能实例化");
    }

    public static boolean isShow = true;
    /**
     * 短时间显示Toast
     *
     * @param context 上下文，可当成所在页面
     * @param message 显示的信息
     */
    public static void showShort(Context context, CharSequence message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context 上下文，可当成所在页面
     * @param message 显示的信息
     */
    public static void showShort(Context context, int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    /**
     * 长时间显示Toast
     *
     * @param context 上下文，可当成所在页面
     * @param message 显示的信息
     */
    public static void showLong(Context context, CharSequence message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context 上下文，可当成所在页面
     * @param message 显示的信息
     */
    public static void showLong(Context context, int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }
}
