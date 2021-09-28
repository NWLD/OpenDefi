package com.nwld.defi.tools.widget;

import android.view.Gravity;
import android.widget.TextView;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.util.StringUtil;


public class LoadingDialog extends BaseDialog {
    private String title;

    public LoadingDialog(BaseActivity context) {
        super(context, R.style.base_dialog_translucent);//加载dialog的样式
    }

    public LoadingDialog(BaseActivity context, String title) {
        super(context, R.style.base_dialog_translucent);//加载dialog的样式
        this.title = title;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public int getLayoutId() {
        return R.layout.base_dialog_loading;
    }

    @Override
    public double getWidthRatio() {
        return 0;
    }

    private TextView titleText;

    public void initView() {
        setCancelable(true);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失
        titleText = V(R.id.base_dialog_loading_text);
        if (StringUtil.isEmpty(title)) {
            return;
        }
        titleText.setText(title);
    }

    private volatile int state = 0;

    public void show() {
        state = 0;
        //延迟50毫秒显示loading，如果数据已经加载完毕，不需要再显示
        MainHandler.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (0 == state) {
                    if (baseActivity.isDestroyed()) {
                        return;
                    }
                    try {
                        LoadingDialog.super.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 500);
    }

    public void setTitle(String title) {
        if (StringUtil.isEmpty(title)) {
            return;
        }
        this.title = title;
        if (null == titleText) {
            return;
        }
        titleText.setText(title);
    }

    public void dismiss() {
        state = -1;
        super.dismiss();
    }
}
