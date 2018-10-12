package com.example.robin.papers.requestModel;

public class PostRequest {

    String token,page,num;

    public PostRequest(String token, String page, String num) {
        this.token = token;
        this.page = page;
        this.num = num;
    }

    public PostRequest(String token, String page) {
        this.token = token;
        this.page = page;
        this.num="10";
    }

    public PostRequest(String token) {
        this.token = token;
        this.page = "1";
        this.num = "10";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}


