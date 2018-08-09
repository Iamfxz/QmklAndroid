package com.android.papers.qmkl_android.requestModel;

public class AuthPerInfoRequest {

    private String platformId,nickname,platform,gender,enterYear,avatar,college,academy;

    public AuthPerInfoRequest(String platformId, String nickname, String platform, String gender,
                              String enterYear, String avatar, String college, String academy) {
        this.platformId = platformId;
        this.nickname = nickname;
        this.platform = platform;
        this.gender = gender;
        this.enterYear = enterYear;
        this.avatar = avatar;
        this.college = college;
        this.academy = academy;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEnterYear() {
        return enterYear;
    }

    public void setEnterYear(String enterYear) {
        this.enterYear = enterYear;
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

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }
}
