package com.android.papers.qmkl_android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.papers.qmkl_android.util.PaperFileUtils;

/**
 * Alter by fxz on 2018/7/26 19：14
 *      文件详细信息
 */
public class PaperFile implements Parcelable {


    private String name;//文件名
    private String url;//url
    private String type;//类型
    private String size;//大小
    private String course;//所属课程
    private boolean download;//是否已经下载
    private String path;//文件路径
    private String createAt;//建立时间
    private String updateAt;//更新时间
    private String md5;//文件的md5
    private String nick;//上传文件的用户昵称
    private int dislikeNum;//踩的人数
    private int likeNum;//赞的人数
    private int uid;//用户id
    private int id;//文件id

    public PaperFile(){

    }

    public PaperFile(String path, String size, FileDetailRes fileDetailRes) {
        this.path = path;
        this.name = PaperFileUtils.nameWithPath(path);
        this.type = PaperFileUtils.typeWithFileName(this.name);
        this.size = size;
        this.course = PaperFileUtils.courseWithPath(path);
        this.download = false;
        this.createAt = fileDetailRes.getData().createAt;
        this.updateAt = fileDetailRes.getData().updateAt;
        this.md5 = fileDetailRes.getData().md5;//文件的md5
        this.nick = fileDetailRes.getData().md5;//上传文件的用户昵称
        this.dislikeNum = fileDetailRes.getData().dislikeNum;//踩的人数
        this.likeNum = fileDetailRes.getData().likeNum;//赞的人数
        this.uid = fileDetailRes.getData().uid;//用户id
        this.id = fileDetailRes.getData().id;//文件id
    }

    public static Creator<PaperFile> getCREATOR() {
        return CREATOR;
    }

    public void setPath(String path) {
        this.path = path;
        this.name = PaperFileUtils.nameWithPath(path);
        this.type = PaperFileUtils.typeWithFileName(this.name);
        this.course = PaperFileUtils.courseWithPath(path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.size);
        dest.writeString(this.course);

        dest.writeByte(download ? (byte) 1 : (byte) 0);
        dest.writeString(this.url);

        dest.writeInt(this.dislikeNum);
        dest.writeInt(this.likeNum);

        dest.writeString(this.md5);
        dest.writeInt(this.id);

        dest.writeString(this.createAt);
        dest.writeString(this.updateAt);
    }

    private PaperFile(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.size = in.readString();
        this.course = in.readString();

        this.download = in.readByte() != 0;
        this.url = in.readString();

        this.dislikeNum = in.readInt();
        this.likeNum = in.readInt();

        this.md5 = in.readString();
        this.id = in.readInt();

        this.createAt = in.readString();
        this.updateAt = in.readString();
    }

    public static final Creator<PaperFile> CREATOR = new Creator<PaperFile>() {
        @Override
        public PaperFile createFromParcel(Parcel source) {
            return new PaperFile(source);
        }

        @Override
        public PaperFile[] newArray(int size) {
            return new PaperFile[size];
        }
    };

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    public String getPath() {
        return path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getDislikeNum() {
        return dislikeNum;
    }

    public void setDislikeNum(String DislikeNum){ this.dislikeNum = Integer.parseInt(DislikeNum); }

    public void setDislikeNum(int dislikeNum) {
        this.dislikeNum = dislikeNum;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(String likeNum){ this.likeNum = Integer.parseInt(likeNum); }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public void setId(int id) {
        this.id = id;
    }

}
