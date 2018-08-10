package com.android.papers.qmkl_android.util;

public class ConstantUtils {

    //个人信息代码
    public static final int NICKNAME = 1;
    public static final int GENDER = 2;
    public static final int ENTERYEAR = 3;
    public static final int COLLEGE = 4;
    public static final int ACADEMY = 5;

    //服务端基础url
    public static final String BaseUrl = "http://120.77.32.233/qmkl1.0.0/";//后端版本

    //服务器返回正确与错误代码
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 202;

    //发送短信接口声明修改密码或者新用户注册
    public static final String FORGET_PSW_MSG = "修改密码";
    public static final String REGISTER_MSG = "注册";

    //提示信息
    public static final String CACHE_AD_ERROR="缓存广告失败";
    public static final String SERVER_REQUEST_FAILURE="服务器请求失败";
    public static final String UPLOAD_IMG_FAILURE="上传头像失败";
    public static final String CHECK_ACCOUNT_AND_PSW="请检查账号密码是否准确";
    public static final String LOGIN_FIRST="请先登录";
    public static final String CHOOSE_ACADEMY="选择学院";
    public static final String CHOOSE_COLLEGE="选择学校";
    public static final String VER_CODE_SEND="验证码已发送";


}
