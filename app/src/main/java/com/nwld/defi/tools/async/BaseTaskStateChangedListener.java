package com.nwld.defi.tools.async;

public interface BaseTaskStateChangedListener {
    void onSuccess(Object data);

    void onFailed(int code, String msg);
}
