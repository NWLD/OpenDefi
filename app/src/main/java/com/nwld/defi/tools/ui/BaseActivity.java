package com.nwld.defi.tools.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.nwld.defi.tools.util.KeyBoardUtils;
import com.nwld.defi.tools.util.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class BaseActivity extends AppCompatActivity {
    public int widthPixels;
    public int heightPixels;

    //Android 8.0 Only fullscreen opaque activities can request orientation
    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            LogUtil.e("Android O", "avoid calling setRequestedOrientation when Oreo.");
            return;
        }
        try {
            super.setRequestedOrientation(requestedOrientation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            boolean result = fixOrientation();
        }
        super.onCreate(savedInstanceState);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        widthPixels = outMetrics.widthPixels;
        heightPixels = outMetrics.heightPixels;
    }

    @Override
    protected void onDestroy() {
        KeyBoardUtils.hideInputForce(this);
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (isTouchView(filterViews(), ev)) {
                return super.dispatchTouchEvent(ev);
            }
            KeyBoardUtils.hideInputForce(this);
            View v = getCurrentFocus();
            if (null != v) {
                v.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    //是否触摸在指定view上面,对某个控件过滤
    public boolean isTouchView(View[] views, MotionEvent ev) {
        if (views == null || views.length == 0) return false;
        int[] location = new int[2];
        for (View view : views) {
            if (null == view) {
                continue;
            }
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            if (ev.getX() > x && ev.getX() < (x + view.getWidth()) && ev.getY() > y && ev.getY() < (y + view.getHeight())) {
                return true;
            }
        }
        return false;
    }

    /*** 传入要过滤的View* 过滤之后点击将不会有隐藏软键盘的操作**@returnid 数组*/
    public View[] filterViews() {
        return null;
    }
}