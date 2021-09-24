package com.nwld.defi.tools.async;

import android.os.Handler;
import android.os.Looper;

public class MainHandler extends Handler {
    private static MainHandler handler;

    private MainHandler(Looper looper) {
        super(looper);
    }

    private static class MainHandlerHolder {
        final static MainHandler instance = new MainHandler(Looper.getMainLooper());
    }

    public static MainHandler getHandler() {
        return MainHandlerHolder.instance;
    }
}
