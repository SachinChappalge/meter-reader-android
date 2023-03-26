package com.extentia.imagetotext.utils;

import java.text.DecimalFormat;

public class Utils {


    public static String convertToFormat(double value){
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(value);
    }

}
