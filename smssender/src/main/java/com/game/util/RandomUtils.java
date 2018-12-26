package com.game.util;

import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType;

import java.security.SecureRandom;

/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2018/11/25 15:37
 */
public class RandomUtils {

    public enum CharacterType {
        /**
         * 数字
         */
        DIGIT,
        /**
         * 字母
         */
        LETTER,
        /**
         * 数字和字母
         */
        DIGIT_LETTER
    }

    public static final String getRandomString(int size, CharacterType type) {
        if (size <= 0) {
            throw new IllegalArgumentException("size<=0 error");
        }
        String str;
        switch (type) {
            case DIGIT:
                str = "0123456789";
                break;
            case LETTER:
                str = "qwertyuipasdfghjkzxcvbnm";
                break;
            default:
                str = "0123456789qwertyuipasdfghjkzxcvbnm";
                break;
        }

        StringBuilder strBuilder = new StringBuilder();
        int strLenth = str.length();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < size; i++) {
            strBuilder.append(str.charAt(random.nextInt(strLenth)));
        }
        return strBuilder.toString();
    }
}
