package com.example.robin.papers.util;


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
     *  avatarUrl:第三方登录时获取的头像存储路径
     *  college:所在大学
     *  enterYear:入学年份
     *  gender:用户性别
     *  id:用户ID
     *  phone:电话号码
     *  username:用户名
     *  lastCollege:在更改学校的时候为选择学校之后但未选择专业时恢复原学校准备
     *  setPswToken:用户找回密码时使用，作为下次修改用户密码的凭证仅一次有效，修改成功后token就失效，五分钟后也失效
     *  registerToken:用户注册时使用，作为下次添加用户的凭证仅一次有效 添加成功后token就失效
     *  imagePath:上传头像的本地路径
     *  uid:qq第三方登录ID，即用户上传openid
     *  accessToken:qq第三方登录获取，用于判断登录是否失效
     *  expires:qq第三方登录获取，用于判断登录是否失效
     *  platform:存储用户登录平台
     *  hasRequestSDPermission:是否是第一次申请SD读写权限
     *  {college}{filename}:将用户个人收藏的文件，保存在本地，数据结构为键值对，例如<"福州大学安全管理","1">，为null则为没有收藏
     */


    static SharedPreferences sp;

    //获取SharedPreferences存储内容
    public static String getStoredMessage(Context context,String key){
        sp=context.getSharedPreferences("finalExam",Context.MODE_PRIVATE);
        return sp.getString(key,null);
    }

    //存储信息
    public static void setStoredMessage(Context context,String key,String value){
        sp=context.getSharedPreferences("finalExam",Context.MODE_PRIVATE);
        Editor editor=sp.edit();
        editor.putString(key,value);
        editor.commit();
        //editor是同步的，有返回成功或者失败，这里使用同步比较稳妥
    }
}
