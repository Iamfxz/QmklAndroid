package com.example.robin.papers.util;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

/**
 * Created by 凌子文 on 15/7/22.
 * Content 操作SD卡工具
 */
public class SDCardUtils {

    private SDCardUtils()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("不能实例化");
    }

    /**
     * 判断SDCard是否可用
     *
     * @return 是否可用
     */
    public static boolean isSDCardEnable()
    {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取SD卡路径
     *
     * @return SD卡绝对路径路径+分隔符
     */
    public static String getSDCardPath()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 检查路径是否存在
     *
     * @param path SD卡路径
     */
    private static void checkPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 获取App文件目录
     *
     * @return {SDCardPath}/PapersApp/
     */
    public static String getAppPath() {
        String appPath = getSDCardPath() + "FinalExam" + File.separator;
        checkPath(appPath);
        return appPath;
    }

    /**
     * 获取下载目录
     *
     * @return {AppPath}/download/
     */
    public static String getDownloadPath() {
        String downloadPath = getAppPath() + "download" + File.separator;
        checkPath(downloadPath);
        return downloadPath;
    }

    /**
     * 获取缓存目录
     *
     * @return {AppPath}/cache/
     */
    public static String getCachePath() {
        String cachePath = getAppPath() + "cache" + File.separator;
        checkPath(cachePath);
        return cachePath;
    }

    /**
     * 获取数据库文件目录
     *
     * @return {AppPath}/database/
     */
    public static String getDBPath() {
        String dbPath = getAppPath() + "database" + File.separator;
        checkPath(dbPath);
        return dbPath;
    }

    /**
     * 获取广告图片地址
     *
     * @return {AppPath}/ad/
     */
    public static String getADImagePath() {
        String adImagePath = getAppPath() + "ad" + File.separator;
        checkPath(adImagePath);
        return adImagePath;
    }

    /**
     * 获取本地广告图片路径
     *
     * @param picName 广告名字
     * @return 广告图片路径
     */
    public static String getADImage(String picName) {
        if(picName!=null && !picName.equals("")){
            String strings[]=picName.split("\\.");
            int length=strings.length;
            if(strings[length-1].equals("jpg") || strings[length-1].equals("png")) {
                return getADImagePath() + picName;
            }
            return getADImagePath() + picName + ".png";
        }
        else {
            return getCachePath() + picName;
        }
    }

    /**
     * 获取本地头像图片路径
     *
     * @param avatarName 头像名字
     * @return 头像图片路径
     */
    public static String getAvatarImage(String avatarName) {
        if(avatarName!=null && !avatarName.equals("")){
            String strings[]=avatarName.split("\\.");
            int length=strings.length;
            if(strings[length-1].equals("jpg") || strings[length-1].equals("png")){
                return getCachePath() + avatarName;
            }
            return getCachePath() + avatarName + ".png";
        }
        else {
            return getCachePath() + avatarName;
        }
    }


    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return SD卡的剩余容量
     */
    public static long getSDCardAllSize()
    {
        if (isSDCardEnable())
        {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = stat.getAvailableBlocksLong() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocksLong();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath 指定路径
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath)
    {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath()))
        {
            filePath = getSDCardPath();
        } else
        {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocksLong() - 4;
        return stat.getBlockSizeLong() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     *
     * @return 系统存储路径
     */
    public static String getRootDirectoryPath()
    {
        return Environment.getRootDirectory().getAbsolutePath();
    }

}