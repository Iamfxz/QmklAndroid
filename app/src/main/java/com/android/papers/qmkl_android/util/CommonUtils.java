package com.android.papers.qmkl_android.util;

/**
 * 作者：方向臻 on 2018/8/2/002 12:53
 * 邮箱：273332683@qq.com
 *      避免多次点击的工具类
 */
public class CommonUtils {
    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        //800ms内只能点击一次
        if ( 0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
