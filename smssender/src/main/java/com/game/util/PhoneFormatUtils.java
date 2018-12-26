package com.game.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Description:
 * @Author: Jason
 * @CreateDate: 2018/11/24 21:15
 */
public class PhoneFormatUtils {

    private static Map<String, String> phoneAreaPrefix = new HashMap<>();
    static {
        phoneAreaPrefix.put("65", "8");
        phoneAreaPrefix.put("60", "9_10");
        phoneAreaPrefix.put("886", "9");
        phoneAreaPrefix.put("852", "8");
        phoneAreaPrefix.put("61", "9");
        phoneAreaPrefix.put("86", "11");
        phoneAreaPrefix.put("64", "9");
        phoneAreaPrefix.put("1", "10");

//        phoneAreaPrefix.put("66", "TH");
//        phoneAreaPrefix.put("84", "VN");
//        phoneAreaPrefix.put("63", "PH");
//        phoneAreaPrefix.put("62", "ID");
//        phoneAreaPrefix.put("853", "MK");
//        phoneAreaPrefix.put("82", "KL");
//        phoneAreaPrefix.put("81", "JP");
//        phoneAreaPrefix.put("1", "US");



    }

    public static boolean isSupport(String phone){
        if(StringUtils.isBlank(phone) || !phone.contains("-")){
            return false;
        }
        String[] areaPhone = phone.split("-");

        if(StringUtils.isAnyBlank(areaPhone[0], areaPhone[1]) || !StringUtils.isNumeric(areaPhone[0]) || !StringUtils.isNumeric(areaPhone[1])){
            return false;
        }

        String phoneLengthRule = phoneAreaPrefix.get(areaPhone[0]);
        String[] rules = StringUtils.split(phoneLengthRule, "_");
        for(int i=0;i<rules.length;i++){
            if(Integer.valueOf(rules[i]) == areaPhone[1].length()){
                return true;
            }
        }
        return false;
    }
}
