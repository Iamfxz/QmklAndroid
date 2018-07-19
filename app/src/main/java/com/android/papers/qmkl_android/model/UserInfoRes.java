package com.android.papers.qmkl_android.model;


import com.google.gson.annotations.SerializedName;

/**
 * json数据转换java对象
 * 用户信息查询的返回结果
 */
public class UserInfoRes {
    //json数据里的code等同于java对象里的Result
    @SerializedName("code")
    public int result;//返回结果代码，200表示成功

    public Info data;//用户数据

    public class Info{
        public String academy;

        public String college;

        public String enteYear;

        public String gender;

        public int id;

        public String nickname;

        public String phone;

        public String username;
    }

    @SerializedName("msg")
    public String resultMsg;
}
