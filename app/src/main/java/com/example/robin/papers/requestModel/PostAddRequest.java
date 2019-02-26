package com.example.robin.papers.requestModel;

public class PostAddRequest {
    String token,content;

    public PostAddRequest(String token, String content) {
        this.token = token;
        this.content = content;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
