package com.example.robin.papers.util;

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
    public static final String BaseUrl = "http://120.77.32.233/";//后端版本
    public static final String mCheckUrl = "http://120.77.32.233/qmkl1.0.0/app/update/";//检查版本更新信息的地址
    public static final String postUrl = "http://120.77.32.233/qmkl1.0.0/user/download/avatar/id/";//获取用户头像的基础url

    //在线预览基础url
    public static final String onlineViewUrl = "https://view.officeapps.live.com/op/view.aspx?src=http://www.finalexam.cn/qmkl1.0.0/dir/online/file/";
    public static final String myonlineViewUrl = "http://www.finalexam.cn/qmkl1.0.0/dir/online/file/";

    public static final String policyUrl = "http://120.77.32.233/qmkl1.0.0/dir/protocol/policy";
    public static final String agreementUrl = "http://120.77.32.233/qmkl1.0.0/dir/protocol/agreement";

    //官方获取的APPID，QQ第三方登录使用
    public static final String APP_ID = "1104895232";

    //服务器返回正确与错误代码
    public static final int SUCCESS_CODE = 200;//请求成功
    public static final int NORMAL_ERROR_CODE = 202;//常规错误，可以直接抛出msg给用户
    public static final int TOKEN_INVALID_CODE = 301;//登陆失效
    public static final int ERROR_CODE = 404;//请求错误
    public static final long DEFAULT_TIMEOUT = 10000;//默认网络请求超时时间10s

    //发送短信接口声明修改密码或者新用户注册
    public static final String FORGET_PSW_MSG = "修改密码";
    public static final String REGISTER_MSG = "注册";

    //提示信息
    public static final String CACHE_AD_ERROR = "缓存广告失败";
    public static final String SERVER_REQUEST_FAILURE = "网络连接失败";
    public static final String UPLOAD_IMG_FAILURE = "上传头像失败";
    public static final String UPLOAD_FILE_BIG = "上传文件过大，请重新选择文件上传";
    public static final String UPLOAD_FILE_FAILURE = "上传文件失败";
    public static final String UPLOAD_FILE_SUCCESS = "上传文件成功，感谢您的分享~";

    public static final String CHECK_ACCOUNT_AND_PSW = "请检查账号密码是否准确";
    public static final String LOGIN_FIRST = "请先登录";
    public static final String CHOOSE_ACADEMY = "选择学院";
    public static final String CHOOSE_COLLEGE = "选择学校";
    public static final String VER_CODE_SEND = "验证码已发送~请查收~";
    public static final String LOGIN_INVALID = "您的登陆信息居然失效了，需要重新登陆~谢谢~";
    public static final String SERVER_FILE_ERROR = "您网络可能出了点问题啦，请重新启动一下~谢谢~";
    public static final String UNKNOWN_ERROR = "很抱歉出现未知异常，可以反馈给我们吗~谢谢~";
    public static final String CONNECT_WITH_ME = "服务器已经崩了，请联系13375983207陈同学";

    public static final String SAVE_PICTURE = "保存至手机";
    public static final String SAVE_PICTURE_FAILED ="保存失败";
    public static final String SAVE_PICTURE_SUCCESS = "图片成功保存至%s目录";


    //微软在线预览URL前缀
    /*
    http://us7-view-wopi.wopi.live.net:808/oh/wopi/files/@/wFileId?wFileId
    一次编码
    http%3A%2F%2Fus7-view-wopi%2Ewopi%2Elive%2Enet%3A808%2Foh%2Fwopi%2Ffiles%2F%40%2FwFileId%3FwFileId
     */
    //doc或docx
    public static final String DOC_OFFICE_URL="https://us7-word-view.officeapps.live.com/wv/wordviewerframe.aspx?ui=zh-CN&rs=zh-CN&WOPISrc=http%3A%2F%2Fus7-view-wopi%2Ewopi%2Elive%2Enet%3A808%2Foh%2Fwopi%2Ffiles%2F%40%2FwFileId%3FwFileId%3D";
    //ppt或pptx
    public static final String PPT_OFFICE_URL="https://us7-powerpoint.officeapps.live.com/p/PowerPointFrame.aspx?PowerPointView=ReadingView&ui=zh%2DCN&rs=zh%2DCN&WOPISrc=http%3A%2F%2Fus7-view-wopi%2Ewopi%2Elive%2Enet%3A808%2Foh%2Fwopi%2Ffiles%2F%40%2FwFileId%3FwFileId%3D";
    //xls或xlsx
    public static final String XLS_OFFICE_URL="https://us7-excel.officeapps.live.com/x/_layouts/xlviewerinternal.aspx?ui=zh%2DCN&rs=zh%2DCN&WOPISrc=http%3A%2F%2Fus7%2Dview%2Dwopi%2Ewopi%2Elive%2Enet%3A808%2Foh%2Fwopi%2Ffiles%2F%40%2FwFileId%3FwFileId%3D";
    //后缀
    public static final String SUFFIX_OFFICE_URL="&access_token_ttl=0";
}