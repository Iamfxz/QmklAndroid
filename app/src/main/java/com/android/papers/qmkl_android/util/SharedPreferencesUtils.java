package com.android.papers.qmkl_android.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 持久化存储数据的SharedPreferences管理类
 */
public class SharedPreferencesUtils {

    static SharedPreferences sp;

    public static String getStoredMessage(Context context,String key){
        sp=context.getSharedPreferences("finalExam",Context.MODE_PRIVATE);
        return sp.getString(key,null);
    }

    public static void setStoredMessage(Context context,String key,String value){
        sp=context.getSharedPreferences("finalExam",Context.MODE_PRIVATE);
        Editor editor=sp.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
