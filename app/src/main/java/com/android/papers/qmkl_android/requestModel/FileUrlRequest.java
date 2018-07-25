package com.android.papers.qmkl_android.requestModel;

/**
 * 作者：方向臻 on 2018/7/25/025 15:41
 * 邮箱：273332683@qq.com
 */
public class FileUrlRequest {
    String token;
    String path;
    String collegeName;

    public FileUrlRequest(String token, String path, String collegeName) {
        this.token = token;
        this.path = path;
        this.collegeName = collegeName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String colleageName) {
        this.collegeName = colleageName;
    }
}
