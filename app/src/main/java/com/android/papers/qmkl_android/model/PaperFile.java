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
    private String path;
    private Long createAt;//建立时间
    private Long updateAt;//更新时间

    public PaperFile(){

    }

    public PaperFile(String path, String size, Long updateAt, Long createAt) {
        this.path = path;
        this.name = PaperFileUtils.nameWithPath(path);
        this.type = PaperFileUtils.typeWithFileName(this.name);
        this.size = size;
        this.course = PaperFileUtils.courseWithPath(path);
        this.download = false;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
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

    public void setPath(String path) {
        this.path = path;
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
    }

    protected PaperFile(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.size = in.readString();
        this.course = in.readString();
        this.download = in.readByte() != 0;
        this.url = in.readString();
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
}
