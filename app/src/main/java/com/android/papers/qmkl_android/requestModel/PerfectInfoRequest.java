package com.android.papers.qmkl_android.requestModel;

public class PerfectInfoRequest {
    private String phone,username,password,nickname,enterYear,gender,academy,college;

    public PerfectInfoRequest(String phone, String username, String password, String nickname,
                              String enterYear, String gender, String academy, String college) {
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.enterYear = enterYear;
        this.gender = gender;
        this.academy = academy;
        this.college = college;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEnterYear() {
        return enterYear;
    }

    public void setEnterYear(String enterYear) {
        this.enterYear = enterYear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }
}
