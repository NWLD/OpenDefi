package com.nwld.defi.tools.util;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class StringUtil {
    /**
     * 判断两个字符串内容是否一样，空串都表示不一样
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean e(String str1, String str2) {
        if (null == str1 || null == str2 || "".equals(str1.trim()) || "".equals(str2.trim())) {
            return false;
        }
        return str1.trim().equals(str2.trim());
    }

    public static boolean eOrNull(String str1, String str2) {
        if (null == str1 && null == str2) {
            return true;
        }
        return str1.trim().equals(str2.trim());
    }

    public static boolean ignoreE(String str1, String str2) {
        if (null == str1 || null == str2 || "".equals(str1.trim()) || "".equals(str2.trim())) {
            return false;
        }
        return str1.toLowerCase().trim().equals(str2.toLowerCase().trim());
    }

    /**
     * 判断字符串是否为空
     *
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        return null == string
                || "".equals(string.trim())
                || "null".equals(string.toLowerCase())
                || "nil".equals(string.toLowerCase());
    }

    /**
     * 将json字符串转换为json对象，
     * 也可用来判断字符串是否json格式
     *
     * @param json
     * @return 返回null表示字符串不是json格式
     */
    public static JSONObject parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static JSONArray parseJsonArray(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            return jsonArray;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String trimZero(String s) {
        if (null != s && s.indexOf(".") > 0) {
            // 去掉多余的0
            s = s.replaceAll("0+?$", "");
            // 如最后一位是.则去掉
            s = s.replaceAll("[.]$", "");
        }
        return s;
    }

    public static String numDotDown(double d, int dotNum) {
        BigDecimal bg = new BigDecimal(d);
        String s = bg.setScale(dotNum, BigDecimal.ROUND_DOWN).toString();
        return s;
    }

    public static int str2Int(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long str2Long(String str) {
        try {
            return Long.parseLong(str.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static double str2Double(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void toClipboard(Context context, String text) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

    /**
     * 将json对象转为js字符串
     *
     * @param data json对象格式的字符串
     * @return
     */
    public static String replace4Js(String data) {
        if (StringUtil.isEmpty(data)) {
            return data;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            //利用JSONObject将data转为转义字符串，传递给js使用
            //{"json":"data"}
            String json = jsonObject.put("json", data).toString();
//            Log.e("JSON-O", json);
            //只保留转义后的data字符串，丢弃{"json":}，保留"data"
            data = json.substring(8, json.length() - 1);
//            Log.e("JSON", data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 计算精度整数值
     *
     * @param amount
     * @param decimal
     * @return
     */
    public static String getValue(String amount, String decimal) {
        if (StringUtil.isEmpty(amount)) {
            return "0";
        }
        if (StringUtil.isEmpty(decimal)) {
            return amount;
        }
        int decimalInt = StringUtil.str2Int(decimal);
        String num = CalcUtils.multiply(amount, String.valueOf(Math.pow(10, decimalInt)));
        BigDecimal bg = new BigDecimal(num);
        String s = bg.setScale(decimalInt, BigDecimal.ROUND_DOWN).toPlainString();
        return StringUtil.trimZero(s);
    }

    public static String[] split(String str, int length) {
        int len = str.length();
        String[] arr = new String[(len + length - 1) / length];
        for (int i = len; i > 0; i -= length) {
            int n = i - length;
            if (n < 0) {
                n = 0;
            }
            arr[(n + length - 1) / length] = str.substring(n, i);
        }
        return arr;
    }

    public static String join(String[] strs, String c) {
        if (null == strs) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int len = strs.length;
        if (0 == len) {
            return "";
        }
        for (int index = 0; index < len; index++) {
            stringBuilder.append(c);
            stringBuilder.append(strs[index]);
        }
        return stringBuilder.toString().substring(1);
    }

    public static String splitJoin(String str, int length, String c) {
        return join(split(str, length), c);
    }

    public static String firstUpperCase(String str) {
        if (StringUtil.isEmpty(str)) {
            return str;
        }
        String first = str.substring(0, 1);
        return first.toUpperCase() + str.substring(1);
    }

    public static String getShowBalance(String value) {
        String intPart = value;
        String dotPart = "";
        if (value.contains(".")) {
            int index = value.indexOf(".");
            intPart = value.substring(0, index);
            dotPart = value.substring(index);
        }
        intPart = StringUtil.splitJoin(intPart, 3, ",");
        return intPart + dotPart;
    }
}
