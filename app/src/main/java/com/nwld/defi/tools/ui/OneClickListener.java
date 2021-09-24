package com.nwld.defi.tools.ui;

import android.view.View;

public abstract class OneClickListener implements View.OnClickListener {
    private int T = 1000;
    private long clickTime = 0;

    @Override
    public void onClick(View v) {
        if (!isOneClick()) {
            onOneClick(v);
            return;
        }
        long nowTime = System.currentTimeMillis();
        if (Math.abs(nowTime - clickTime) > T) {
            clickTime = nowTime;
            onOneClick(v);
        }
    }

    public boolean isOneClick() {
        return true;
    }

    public abstract void onOneClick(View v);
}
