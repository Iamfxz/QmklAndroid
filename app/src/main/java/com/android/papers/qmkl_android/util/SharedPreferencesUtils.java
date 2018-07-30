package com.android.papers.qmkl_android.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 持久化存储数据的SharedPreferences管理类
 */
public class SharedPreferencesUtils {

    /**
     *  fallback:点击广告返回的链接地址
     *  adtitle:广告标题
     *  AdName:广告名称
     *  token:用户登录返回的token值
     *  hasLogin:返回用户五天之内是否已登录
     *  nickname:用户昵称
     *  academy:所在学院
     *  avatar:头像名称
     *  college:所在大学
     *  enterYear:入学年份
     *  gender:用户性别
     *  phone:电话号码
     *  username:用户名
     *  lastCollege:在更改学校的时候为选择学校之后但未选择专业时恢复原学校准备
     */


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
