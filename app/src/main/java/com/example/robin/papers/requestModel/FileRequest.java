package com.example.robin.papers.requestModel;

/**
 * 作者：方向臻 on 2018/7/24/024 11:10
 * 邮箱：273332683@qq.com
 */
public class FileRequest {

    String path;
    String token;
    String collegeName;

    public FileRequest(String path, String collegeName, String token) {
        this.token = token;
        this.path = path;
        this.collegeName = collegeName;
    }
}
