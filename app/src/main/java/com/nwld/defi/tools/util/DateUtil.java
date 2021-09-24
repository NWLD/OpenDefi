package com.nwld.defi.tools.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getDateTime(long timestamp) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(new Date(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
