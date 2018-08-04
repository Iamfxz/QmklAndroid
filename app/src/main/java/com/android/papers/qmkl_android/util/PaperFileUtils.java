package com.android.papers.qmkl_android.util;

import com.android.papers.qmkl_android.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 16/4/23.
 *
 * 历年卷文件工具
 */
public class PaperFileUtils {
    private static Map<String, Integer> types = new HashMap<String, Integer>(){
        {
            //word
            put("wps", R.drawable.document_type_word);
            put("doc", R.drawable.document_type_word);
            put("docx", R.drawable.document_type_word);

            //ppt
            put("dps", R.drawable.document_type_ppt);
            put("ppt", R.drawable.document_type_ppt);
            put("pptx", R.drawable.document_type_ppt);

            //表格
            put("xls", R.drawable.document_type_xls);
            put("xls", R.drawable.document_type_xls);
            put("xlt", R.drawable.document_type_xls);
            put("et", R.drawable.document_type_xls);

            //文本
            put("txt", R.drawable.document_type_txt);
            put("rtf", R.drawable.document_type_txt);

            //压缩包
            put("zip", R.drawable.document_type_zip);
            put("rar", R.drawable.document_type_zip);
            put("7z", R.drawable.document_type_zip);

            //pdf
            put("pdf", R.drawable.document_type_pdf);

            //image
            put("png", R.drawable.document_type_img);
            put("jpg", R.drawable.document_type_img);

            //unknow
            put("unknow", R.drawable.document_type_unknow);

            //folder
            put("folder",R.drawable.document_type_folder);
        }
    };

    /**
     * 将字节转换为单位为KB,MB,GB的#.##的双精度十进制数
     *
     * @param size 文件大小 单位 kb
     * @return 转换后的字符串
     */
    public static String sizeWithDouble(double size) {

        DecimalFormat format = new DecimalFormat("#.##");

        String result = "";

        if (size < 1024) {
            result = format.format(size) + "KB";
        } else if (size < 1024 * 1024) {
            result = format.format(size / 1024.0) + "MB";
        } else if (size < 1024 * 1024 * 1024) {
            result = format.format(size / 1024.0 / 1024.0) + "GB";
        }
        return result;
    }

    /**
     *      通过服务器返回的字符串判断文件类型
     *      folder--文件夹
     *      word--某种文档格式
     * @param fileName 服务器返回的字符串
     * @return 文件类型
     */
    static public String typeWithFileName(String fileName){
        String result;
        if(fileName.contains("."))
            result = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
        else
            result = "folder";

        return result.trim();
    }

    /**
     *      获取倒数第二个斜杠的索引
     * @param str 需要处理的字符串，一般为路径，例如/cad/
     * @return 倒数第二个斜杠的索引，上述例子返回为0
     */
    public static int last2IndexOf(String str){
        int num = 0, num2 = 0;
        int i;
        for(i = 0; i < str.length(); i++){
            if(str.charAt(i) == '/')
                num++;
        }
        for(i = 0 ;i < str.length() ; i++){
            if(str.charAt(i) == '/'){
                num2++;
            }
            if(num2 == num-1) break;
        }
        if(num == 0)
            return num;
        return i;
    }

    /**
     *      利用路径名字得出文件的名字
     * @param path 相对路径
     * @return 文件的名字
     */
    static public String nameWithPath(String path){
        return path.substring(path.lastIndexOf("/")+1,path.length());
    }

    /**
     *      获取课程名字
     * @param path 相对路径
     * @return 课程名字
     */
    static public String courseWithPath(String path){
        int num = 0;
        int index2 = path.indexOf("/");
        for (int i = 0; i < path.length();i++){
            if(path.charAt(i) == '/'){
                num ++;
            }

            if(num == 2){
                index2 = i;
                break;
            }else if (path.length() == 1){
                //特殊情况处理，一般后台数据没出错不会执行这里
                return "/";
            }
        }
        return path.substring(path.indexOf("/")+1,index2);
    }
    /**
     * 文件后缀获得相应资源文件id
     *
     * @param type 文件拓展名
     * @return 文件图标
     */
    public static int parseImageResource(String type) {
        if (types.containsKey(type.toLowerCase())) {
            return types.get(type.toLowerCase());
        } else {
            return types.get("unknow");//原意unknown
        }
    }

    /**
     * 获取当前时间，yyyy-MM-dd 默认中国表达方式
     *
     * @return 当前时间的字符串
     */
    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return simpleDateFormat.format(new Date());
    }

    public static String ParseTimestamp(Long timestamp){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return simpleDateFormat.format(timestamp);
    }
}
