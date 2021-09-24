package com.nwld.defi.tools.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LogUtil {
    public static boolean showLog = true;

    public static void e(String tag, Object value) {
        if (!showLog) {
            return;
        }
        if (StringUtil.isEmpty(tag)) {
            tag = "LogUtil";
        }
        if (null == value) {
            value = "log is null !";
        }
        String TAG = "DefiTool#"+tag;
        String log = value.toString();
        Log.e(TAG, log + "\n\n");
        printJson(TAG, log, "");
    }

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.e(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.e(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }

    public static void printJson(String tag, String msg, String headString) {
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        printLine(tag, true);
        message = headString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            Log.e(tag, "║ " + line);
        }
        printLine(tag, false);
    }

    public static void d(Object tag, Object log) {
        if (!showLog) {
            return;
        }
        Log.d("abc@" + tag.toString(), log.toString());
    }
}
