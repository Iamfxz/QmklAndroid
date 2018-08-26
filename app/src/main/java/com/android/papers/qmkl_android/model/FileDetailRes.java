package com.android.papers.qmkl_android.model;

import java.util.Date;

/**
 * 作者：方向臻 on 2018/7/26/026 14:53
 * 邮箱：273332683@qq.com
 */
public class FileDetailRes {
    private String code;
    private FileDetail data;

    public class FileDetail {
        String createAt;//创建时间
        String md5;//文件的md5
        String name;//文件名字
        String nick;//上传文件的用户昵称
        String size;//文件大小
        String updateAt;//更新时间
        int dislikeNum;//踩的人数
        int likeNum;//赞的人数
        int uid;//用户id
        int id;//文件id


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
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

        public String getUpdateAt() {
            return updateAt;
        }

        public void setUpdateAt(String updateAt) {
            this.updateAt = updateAt;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getDislikeNum() {
            return dislikeNum;
        }

        public void setDislikeNum(int dislikeNum) {
            this.dislikeNum = dislikeNum;
        }

        public int getLikeNum() {
            return likeNum;
        }

        public void setLikeNum(int likeNum) {
            this.likeNum = likeNum;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
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
