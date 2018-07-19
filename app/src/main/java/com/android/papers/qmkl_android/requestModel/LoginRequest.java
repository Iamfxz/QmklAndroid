package com.android.papers.qmkl_android.requestModel;

public class LoginRequest {
    String username;
    String password;

    public LoginRequest(String username, String password){
        this.username = username;
        this.password = password;
    }
}