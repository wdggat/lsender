package com.liu.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String showTime(long unixtime) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		return sdf.format(new Date(unixtime * 1000));
    }
    
    public static String showDate(long unixtime) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		return sdf.format(new Date(unixtime * 1000));
    }
}
