package com.android.papers.qmkl_android.requestModel;

public class TokenLoginRequest {
    String token;

    public TokenLoginRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
