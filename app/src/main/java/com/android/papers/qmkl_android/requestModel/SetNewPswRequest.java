package com.android.papers.qmkl_android.requestModel;

public class SetNewPswRequest {

    private String token,vercode,phone,password;

    public SetNewPswRequest(String token, String vercode, String phone, String password) {
        this.token = token;
        this.vercode = vercode;
        this.phone = phone;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
