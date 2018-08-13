package com.android.papers.qmkl_android.util;

public class ConstantUtils {

    //个人信息代码
    public static final int NICKNAME = 1;
    public static final int GENDER = 2;
    public static final int ENTER_YEAR = 3;
    public static final int COLLEGE = 4;
    public static final int ACADEMY = 5;

    //保存所有学校信息和选中学校的学院信息
    public static String[] colleges=null;
    public static String[] academies=null;

    //服务端基础url
    public static final String BaseUrl = "http://120.77.32.233/qmkl1.0.0/";//后端版本

    //服务器返回正确与错误代码
    public static final int SUCCESS_CODE = 200;//请求成功
    public static final int NORMAL_ERROR_CODE = 202;//常规错误，可以直接抛出msg给用户
    public static final int TOKEN_INVALID_CODE = 301;//登陆失效
    public static final int ERROR_CODE = 404;//请求错误

    //发送短信接口声明修改密码或者新用户注册
    public static final String FORGET_PSW_MSG = "修改密码";
    public static final String REGISTER_MSG = "注册";

    //提示信息
    public static final String CACHE_AD_ERROR = "缓存广告失败";
    public static final String SERVER_REQUEST_FAILURE = "网络连接失败";
    public static final String UPLOAD_IMG_FAILURE = "上传头像失败";
    public static final String CHECK_ACCOUNT_AND_PSW = "请检查账号密码是否准确";
    public static final String LOGIN_FIRST = "请先登录";
    public static final String CHOOSE_ACADEMY = "选择学院";
    public static final String CHOOSE_COLLEGE = "选择学校";
    public static final String VER_CODE_SEND = "验证码已发送~请查收~";
    public static final String LOGIN_INVALID = "您的登陆信息居然失效了，需要重新登陆~谢谢~";
    public static final String SERVER_FILE_ERROR = "您网络可能出了点问题啦，请重新启动一下~谢谢~";
    public static final String UNKNOWN_ERROR = "很抱歉出现未知异常，可以反馈给我们吗~谢谢~";


}