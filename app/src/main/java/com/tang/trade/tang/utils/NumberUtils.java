package com.tang.trade.tang.utils;

/**
 * Created by dagou on 2017/9/21.
 */

public class NumberUtils {

    public static boolean compareFloatAndInt(float f,int i) {
        Float aFloat = Float.valueOf(f);
        int i1 = aFloat.compareTo((float) i);
        if (i1 == 0) {
            return true;
        }

        return false;

    }


    /**
     * 规则2：至少包含大小写字母及数字中的两种
     * 是否包含
     *
     * @param str
     * @return
     */
    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            } else if (Character.isLetter(str.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        String regex = "^[^\\u4e00-\\u9fa5]{0,}$";
        boolean isRight = isDigit && isLetter && str.matches(regex) && str.length()>=8 && str.length()<=18;
        return isRight;
    }
}
