package com.android.papers.qmkl_android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.Source;

/**
 * 作者：方向臻 on 2018/7/24/024 11:08
 * 邮箱：273332683@qq.com
 *      向“文件列表”端口请求文件列表后返回的数据
 *      使用Parcelable方法传递数据
 */
public class FileRes implements Parcelable{
    private String code;
    private HashMap<String,String> data;//每一行是一个文件的名字，及文件大小
    private String msg;


    public FileRes(Parcel source) {
        this.code = source.readString();
        data = new HashMap<>();
        source.readHashMap(HashMap.class.getClassLoader());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);//写出操作结果码
        dest.writeMap(data);//写入该列表下所有文件名及对应文件大小
        dest.writeString(msg);//操作结果解释
    }

    public static final Parcelable.Creator<FileRes> CREATOR = new Parcelable.Creator<FileRes>(){

        @Override
        public FileRes createFromParcel(Parcel source) {
            return new FileRes(source);
        }

        @Override
        public FileRes[] newArray(int size) {
            return new FileRes[0];
        }
    };
}
