package com.android.papers.qmkl_android.model;

import com.google.gson.annotations.SerializedName;

public class ResponseInfo<T> {
    //json数据里的code等同于java对象里的Result
    @SerializedName("code")
    private int resultCode;
    @SerializedName("data")
    private T token;//登录标记，用于自动登录

    private String msg;//"操作成功"


    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public T getToken() {
        return token;
    }

    public void setToken(T token) {
        this.token = token;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
