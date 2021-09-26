package com.nwld.defi.tools.widget;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.util.StringUtil;

/**
 * Created by denghaofa
 * on 2019-05-15 13:02
 */
public class ConfirmCancelDialog extends BaseDialog {
    private String title;
    private String des;
    private String confirmText;
    private String cancelText;

    public ConfirmCancelDialog(BaseActivity context, String title) {
        super(context);//加载dialog的样式
        this.title = title;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setConfirmText(String confirmText) {
        this.confirmText = confirmText;
    }

    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public int getLayoutId() {
        return R.layout.base_dialog_confirm_cancel;
    }

    @Override
    public double getWidthRatio() {
        return (double) 270 / 375;
    }

    private TextView confirmButton;
    private TextView cancelButton;
    private TextView titleText;
    private TextView desText;

    public void initView() {
        titleText = V(R.id.base_dialog_confirm_cancel_title);
        desText = V(R.id.base_dialog_confirm_cancel_des);
        confirmButton = V(R.id.base_dialog_confirm_cancel_confirm);
        cancelButton = V(R.id.base_dialog_confirm_cancel_cancel);
        titleText.setText(title);
        confirmButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (null != onConfirmListener && onConfirmListener.onConfirm()) {
                    return;
                }
                dismiss();
            }
        });
        cancelButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (null != onCancelListener && onCancelListener.onCancel()) {
                    return;
                }
                dismiss();
            }
        });
    }


    public void show() {
        super.show();
        if (StringUtil.isEmpty(des)) {
            desText.setVisibility(View.GONE);
        } else {
            desText.setText(des);
            desText.setVisibility(View.VISIBLE);
        }
        if (!StringUtil.isEmpty(confirmText)) {
            confirmButton.setText(confirmText);
        }
        if (!StringUtil.isEmpty(cancelText)) {
            cancelButton.setText(cancelText);
        }
    }

    OnCancelListener onCancelListener;
    OnConfirmListener onConfirmListener;

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public interface OnConfirmListener {
        boolean onConfirm();
    }

    public interface OnCancelListener {
        boolean onCancel();
    }
}
