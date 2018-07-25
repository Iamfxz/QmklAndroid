package com.android.papers.qmkl_android.model;

/**
 * 作者：方向臻 on 2018/7/25/025 15:41
 * 邮箱：273332683@qq.com
 *      文件详细数据的返回数据
 */
public class FileUrlRes {
    private String code;
    private DATA data;
    private String msg;

    public class DATA {
        private String fileName;
        private String url;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DATA getData() {
        return data;
    }

    public void setData(DATA data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
