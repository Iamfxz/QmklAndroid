package com.android.papers.qmkl_android.requestModel;

public class RegisterRequest {

    private String token,vercode,phone;

    public RegisterRequest(String token, String vercode, String phone) {
        this.token = token;
        this.vercode = vercode;
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVercode() {
        return vercode;
    }

    public void setVercode(String vercode) {
        this.vercode = vercode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

