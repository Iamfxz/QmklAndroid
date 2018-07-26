package com.android.papers.qmkl_android.model;

/**
 * 作者：方向臻 on 2018/7/26/026 14:53
 * 邮箱：273332683@qq.com
 */
public class FileDetailRes {
    private String code;
    private FileDetail data;

    public class FileDetail {
        Long createAt;
        String md5;
        String name;
        String nick;
        int size;
        Long updateAt;

        public Long getCreateAt() {
            return createAt;
        }

        public void setCreateAt(Long createAt) {
            this.createAt = createAt;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public Long getUpdateAt() {
            return updateAt;
        }

        public void setUpdateAt(Long updateAt) {
            this.updateAt = updateAt;
        }
    }
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public FileDetail getData() {
        return data;
    }

    public void setData(FileDetail data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
