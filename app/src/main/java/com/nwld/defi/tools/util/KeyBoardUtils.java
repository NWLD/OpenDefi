package com.nwld.defi.tools.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by denghaofa
 * on 2019-05-17 14:41
 */
public class KeyBoardUtils {

    /*** 打开软键盘**@parammEditText*@parammContext*/
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /*** 关闭软键盘**@parammEditText*@parammContext*/
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /*** des:隐藏软键盘,这种方式参数为activity**@paramactivity*/
    public static void hideInputForce(Activity activity) {
        if (activity == null || activity.getCurrentFocus() == null) return;
        ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static void hideInputForce(Dialog dialog) {
        if (dialog == null || dialog.getCurrentFocus() == null) return;
        ((InputMethodManager) dialog.getContext().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /*** 打开键盘**/
    public static void showInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }
}
