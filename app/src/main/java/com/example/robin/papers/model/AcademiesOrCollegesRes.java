package com.example.robin.papers.model;

//存储所有学校信息或者学院信息
public class AcademiesOrCollegesRes {
    private String code;//返回结果代码，200表示成功

    private String[] data;

    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
