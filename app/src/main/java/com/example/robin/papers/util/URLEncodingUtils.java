package com.example.robin.papers.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 对字符进行编码或解码
 */
public class URLEncodingUtils {

    public static String UTF8Decoder(String s){
        String writeNote="";
        try {
            writeNote = URLDecoder.decode(s, "utf-8");//utf-8解码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return writeNote;
    }

    public static boolean stringIsNull(String s){
        if(s!=null && !s.trim().equals("")){
            return true;
        }
        return false;
    }



}
