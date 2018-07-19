package com.android.papers.qmkl_android.model;

import com.google.gson.annotations.SerializedName;

public class ResponseInfo<T> {
<<<<<<< HEAD

    String code;
    T data;
    String msg;
=======
    //json数据里的code等同于java对象里的Result
    @SerializedName("code")
    int resultCode;
    @SerializedName("data")
    T token;//登录标记，用于自动登录

    String msg;//"操作成功"
>>>>>>> eebb50d47daf62cb45fe88bbfd810bd36447116e


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
