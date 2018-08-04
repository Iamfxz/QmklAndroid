package com.android.papers.qmkl_android.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 作者：方向臻 on 2018/7/24/024 11:08
 * 邮箱：273332683@qq.com
 *      向“文件列表”端口请求文件列表后返回的数据
 *      使用Parcelable方法传递数据
 */
public class FileRes implements Parcelable{
    private String code;
    private LinkedHashMap<String,String> data;//每一行是一个文件的名字，及文件大小
    private String msg;


    //默认使用ascii码排序，后期可再更改成将中文转为拼音在按字母排序
    public void sort(){
        Set<Map.Entry<String,String>> entries =  data.entrySet();
        List<Map.Entry<String,String>> list = new LinkedList<>(entries);
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return o1.getKey().compareTo(o2.getKey());
            }
        });
    }

    public FileRes(Parcel source) {
        this.code = source.readString();
        data = new LinkedHashMap<>();
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

    public void setData(LinkedHashMap<String, String> data) {
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
