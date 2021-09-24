package com.nwld.defi.tools.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nwld.defi.tools.util.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public abstract class BaseActivity extends AppCompatActivity {

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
    }
}