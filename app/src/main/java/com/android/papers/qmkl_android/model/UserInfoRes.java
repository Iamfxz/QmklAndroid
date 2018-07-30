package com.android.papers.qmkl_android.model;


import com.google.gson.annotations.SerializedName;

/**
 * json数据转换java对象
 * 用户信息查询的返回结果
 */
public class UserInfoRes {

    private int code;//返回结果代码，200表示成功

    private Data data;//用户数据

    public class Data{

        private String academy,avatar,college,enterYear,gender;
        private int id;
        private String nickname,phone,username;

        public String getAcademy() {
            return academy;
        }

        public void setAcademy(String academy) {
            this.academy = academy;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getCollege() {
            return college;
        }

        public void setCollege(String college) {
            this.college = college;
        }

        public String getEnteYear() {
            return enterYear;
        }

        public void setEnteYear(String enteYear) {
            this.enterYear = enteYear;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
