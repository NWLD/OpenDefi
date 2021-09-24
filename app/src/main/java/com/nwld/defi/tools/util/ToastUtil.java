package com.nwld.defi.tools.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static void showToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
