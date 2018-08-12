package com.android.papers.qmkl_android.util;

import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 文本输入过滤器
 * 限制不能输入空格以及其他特殊字符
 */
public class EditTextFilter {

    public static void setProhibitEmoji(EditText et, Context context) {
        InputFilter[] filters = { getInputFilterProhibitEmoji(context) ,getInputFilterProhibitSP(context)};
        et.setFilters(filters);
    }

    public static InputFilter getInputFilterProhibitEmoji(final Context context) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                StringBuffer buffer = new StringBuffer();
                for (int i = start; i < end; i++) {
                    char codePoint = source.charAt(i);
                    if (!getIsEmoji(codePoint)) {
                        buffer.append(codePoint);
                    } else {
                        Toast.makeText(context,"您的输入中有非法字符",Toast.LENGTH_SHORT).show();
                        i++;
                        continue;
                    }
                }
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(buffer);
                    TextUtils.copySpansFrom((Spanned) source, start, end, null,
                            sp, 0);
                    return sp;
                } else {
                    return buffer;
                }
            }
        };
        return filter;
    }
    public static InputFilter getInputFilterProhibitSP(final Context context) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                StringBuffer buffer = new StringBuffer();
                for (int i = start; i < end; i++) {
                    char codePoint = source.charAt(i);
                    if (!getIsSp(codePoint)) {
                        buffer.append(codePoint);
                    } else {
                        Toast.makeText(context,"您的输入中有非法字符",Toast.LENGTH_SHORT).show();
                        i++;
                        continue;
                    }
                }
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(buffer);
                    TextUtils.copySpansFrom((Spanned) source, start, end, null,
                            sp, 0);
                    return sp;
                } else {
                    return buffer;
                }
            }
        };
        return filter;
    }

    public static boolean getIsEmoji(char codePoint) {
        if ((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))
            return false;
        return true;
    }
    public static boolean getIsSp(char codePoint){
        if(Character.getType(codePoint)>Character.LETTER_NUMBER){
            return true;
        }
        return false;
    }

    //判断字符串中是否有非法字符
    public static boolean isIllegal(String string){
        for(char codePoint:string.toCharArray()){
            if (!(((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                    || (codePoint == 0xD)
                    || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                    || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                    || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))
                    || !(Character.getType(codePoint)>Character.LETTER_NUMBER))) {
                return false;
            }
        }
        return true;

    }


}
