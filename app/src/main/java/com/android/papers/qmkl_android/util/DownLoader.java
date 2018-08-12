package com.android.papers.qmkl_android.util;

import android.app.ProgressDialog;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Robin on 2015/9/29.
 */
public class DownLoader {

    public interface DownloadTaskCallback {
        public void onProgress(int hasWrite, int totalExpected);
        public void onSuccess(String successName);
        public void onFailure(Exception e);
        public void onInterruption();
    }


    public static File downloadUpdate(String url, File file, ProgressDialog pd)  throws Exception {
        URL downloadURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) downloadURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(3000);
        InputStream is = connection.getInputStream();
        FileOutputStream os = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        int maxLen = connection.getContentLength();
        pd.setMax(maxLen);
        int progress = 0;
        while((len = is.read(buffer)) != -1)  {
            os.write(buffer, 0, len);
            progress += len;
            pd.setProgress(progress);
        }
        os.flush();
        os.close();
        is.close();
        return file;
    }

    /**
     * 下载文件的线程
     *
     * @param url 下载地址url
     * @param destination 下载目的地
     * @param callback 回调
     * @return 线程
     */
    public static Thread downloadPaperFile(final String url, final String destination, final DownloadTaskCallback callback) {
        return new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream is = null;
                FileOutputStream os = null;
                HttpURLConnection connection = null;
                File file = null;
                String fileName = null;
                String mPath = null;
                try {
                    URL downloadURL = new URL(url);
                    connection = (HttpURLConnection) downloadURL.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(3000);
                    connection.setDoInput(true);
                    connection.connect();
                    is = connection.getInputStream();

                    file = new File(destination);
                    fileName = file.getName();
                    Log.d("ActivityTag", "file name :" + fileName);
                    mPath = destination.replace(fileName, "");
                    Log.d("ActivityTag", "file path :" + mPath);
                    int suffix = 0;
                    while (file.exists()) {
                        String name = addSuffix(fileName, ++ suffix);
                        Log.d("ActivityTag", "文件名重复, 尝试使用:" + name);
                        file = new File(mPath + name);
                    }

                    os = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    int totalLen = connection.getContentLength();
                    int progress = 0;

                    while((len = is.read(buffer)) != -1 && ! Thread.currentThread().isInterrupted())  {
                        os.write(buffer, 0, len);
                        progress += len;

                        if (callback instanceof DownloadTaskCallback) {
                            callback.onProgress(progress, totalLen);
                        }
                    }

                    os.flush();

                    if (Thread.currentThread().isInterrupted()) {

                        //若停止下载,则删除存在的下载文件

                        if (file != null && file.exists()) {
                            file.delete();
                        }

                        if (callback instanceof DownloadTaskCallback) {
                            callback.onInterruption();
                        }
                    }

                    if (callback instanceof DownloadTaskCallback && ! Thread.currentThread().isInterrupted()) {
                        callback.onSuccess(file.getName());
                    }

                }catch (Exception e) {

                    if (callback instanceof DownloadTaskCallback && ! Thread.currentThread().isInterrupted()) {
                        callback.onFailure(e);
                    }

                    if (file != null && file.exists()) {
                        file.delete();
                    }

                } finally {

                    try {

                        if (os != null) {
                            os.close();
                        }

                        if (is != null) {
                            is.close();
                        }

                        if (connection != null) {
                            connection.disconnect();
                        }

                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    /**
     * 为下载文件适当添加后缀以确定下载文件的储存名唯一
     *
     * @param name 文件名
     * @param suffix 后缀：第几个同名文件
     * @return 文件名+（后缀名）
     */
    public static String addSuffix(String name, int suffix) {
        StringBuffer buffer = new StringBuffer(name);
        int index = buffer.lastIndexOf(".");
        return buffer.replace(index, index + 1, "(" + suffix + ").").toString();
    }

    /**
     *
     * @param file 需要的下载文件
     * @param path 下载链接
     * @return 下载完的文件引用
     * @throws Exception 调用者处理
     */
    public static File downloadFile(File file, String path)  {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            InputStream is = connection.getInputStream();
            FileOutputStream fout = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = is.read(buffer)) != -1)  {
                fout.write(buffer, 0, len);
            }
            fout.flush();
            fout.close();
            is.close();
        }catch (java.net.SocketTimeoutException e){
            Log.d("downloader", "超时 ");
        }catch (IOException e) {
            Log.d("downloader", "异常 ");
        }

        return file;
    }

    /* 从网络Url中下载文件
     *
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(5 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);
        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        System.out.println("info:" + url + " download success");
    }
    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
    public static void main(String[] args) {
        try {
            downLoadFromUrl("https://qmkl.oss-cn-qingdao.aliyuncs.com/objects/000/0586d07f86fee3d92a66d443b089e?OSSAccessKeyId=O7fR5ZayjEVadzYh&Expires=1531562343&Signature=SSLLKn1kFSg%2Bg1ZaVbactsAjYWc%3D",
                    "课件2.pdf", "D:\\qmkl下载");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
