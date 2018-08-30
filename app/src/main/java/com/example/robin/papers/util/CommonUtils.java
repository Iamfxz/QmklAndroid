package com.example.robin.papers.util;

/**
 * 作者：方向臻 on 2018/8/2/002 12:53
 * 邮箱：273332683@qq.com
 *      避免多次点击的工具类及文本相似度算法
 */
public class CommonUtils {
    private static long lastClickTime;

    /**
     *      是否在800ms内点击两次
     * @return true -- 点击过快
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        //800ms内只能点击一次
        if ( 0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     *      文本相似度算法
     * @param str1 比较字符串1
     * @param str2 比较字符串2
     * @return similarity 相似度，范围[0.0,1.0]
     */
    public static float levenshtein(String str1, String str2) {
        // 计算两个字符串的长度。
        int len1 = str1.length();
        int len2 = str2.length();
        // 建立上面说的数组，比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        // 赋初值，步骤B。
        for (int a = 0; a <= len1; a++) {
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++) {
            dif[0][a] = a;
        }
        // 计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {

                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 取三个值中最小的
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,
                        dif[i - 1][j] + 1);
            }
        }

        // 计算相似度并返回
        return 1 - (float) dif[len1][len2]
                / Math.max(str1.length(), str2.length());
    }

    /**
     *      三者取最小值
     * @param a 参数1
     * @param b 参数2
     * @param c 参数3
     * @return 最小值
     */
    private static int min(int a, int b, int c){
        int min;
        min=a;
        min=(b<min)?b:min;
        min=(c<min)?c:min;
        return min;
    }
}
