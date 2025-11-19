package com.example.paymentApi.shared.utility;

public class TextUtil {
    public static String capitalizeFirstLetter(final String str) {
        if (str == null || str.isEmpty()){
            return str;
        }

        if (str.length() < 2){
            return str.toUpperCase();
        }

        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}
