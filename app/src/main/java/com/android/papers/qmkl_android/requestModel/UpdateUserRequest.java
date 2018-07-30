package com.android.papers.qmkl_android.requestModel;

public class UpdateUserRequest {


    public class user{
        private String nickname,gender,enterYear,college,academy;

        public user(String nickname, String gender, String enterYear, String college, String academy) {
            this.nickname = nickname;
            this.gender = gender;
            this.enterYear = enterYear;
            this.college = college;
            this.academy = academy;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
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

    private user user;
    private String token;

    public UpdateUserRequest(String nickname, String gender, String enterYear, String college, String academy, String token) {
        this.user = new UpdateUserRequest.user(nickname,gender,enterYear,college,academy);
        this.token = token;
    }

    public user getUser() {
        return user;
    }

    public void setUser(user user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
