package com.android.papers.qmkl_android.model;

import com.google.gson.annotations.SerializedName;

public class ResponseInfo<T> {

    String code;
    T data;
    String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
