package com.nwld.defi.tools.widget;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.util.KeyBoardUtils;
import com.nwld.defi.tools.util.LogUtil;


/**
 * Created by denghaofa
 * on 2019-05-15 17:25
 */
public abstract class BaseDialog extends Dialog {
    //在构造方法里提前加载了样式
    public BaseActivity baseActivity;//上下文

    public BaseDialog(BaseActivity context) {
        this(context, R.style.base_dialog);
    }

    //是否透明的
    private boolean isTranslucentDialog = false;

    public BaseDialog(BaseActivity context, int style) {
        super(context, style);//加载dialog的样式
        this.baseActivity = context;
        isTranslucentDialog = style == R.style.base_dialog_translucent;
    }

    private Window dialogWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //提前设置Dialog的一些样式
        dialogWindow = getWindow();
        int gravity = getGravity();
        dialogWindow.setGravity(gravity);//设置dialog显示居中
        if (Gravity.BOTTOM == gravity) {
            dialogWindow.setWindowAnimations(R.style.base_dialog_bottom_animation);//设置动画效果
        } else if (Gravity.CENTER == gravity) {
            dialogWindow.setWindowAnimations(R.style.base_dialog_center_animation);//设置动画效果
        }

        setContentView(getLayoutId());

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        if (getWidthRatio() != 0) {
            lp.width = (int) (baseActivity.widthPixels * getWidthRatio());// 设置dialog宽度为屏幕
        }
        dialogWindow.setAttributes(lp);
        setCancelable(true);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失

        initView();
    }

    public abstract int getGravity();

    public abstract void initView();

    public abstract int getLayoutId();

    public abstract double getWidthRatio();

    public final <V> V V(int id) {
        View view = findViewById(id);
        if (null == view) {
            return null;
        }
        return (V) view;
    }

    public void bindText(int textId, String string) {
        TextView textView = V(textId);
        textView.setText(string);
    }

    public void bindClick(int viewId, final View.OnClickListener onClickListener) {
        View view = V(viewId);
        view.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (null != onClickListener) {
                    onClickListener.onClick(v);
                }
            }
        });
    }

    public BaseActivity getBaseActivity() {
        return baseActivity;
    }

    boolean hadStart = false;

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e(this.getClass().getName(), "onStart");
        if (isTranslucentDialog && hadStart) {
            return;
        }
        hadStart = true;
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 0.5f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    float v = (float) animation.getAnimatedValue();
                    dialogWindow.setDimAmount(v);
                    //修改dialog window 动画
                    if (v == 0.5f) {
                        int gravity = getGravity();
                        if (Gravity.BOTTOM == gravity) {
                            dialogWindow.setWindowAnimations(R.style.base_dialog_bottom_animation_showing);//设置动画效果
                        } else if (Gravity.CENTER == gravity) {
                            dialogWindow.setWindowAnimations(R.style.base_dialog_center_animation_showing);//设置动画效果
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    valueAnimator.cancel();
                }
            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    boolean hadStop = false;

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e(this.getClass().getName(), "onStop");
        if (isTranslucentDialog && hadStop) {
            return;
        }
        hadStop = true;
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.5f, 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    float v = (float) animation.getAnimatedValue();
                    dialogWindow.setDimAmount(v);
                } catch (Exception e) {
                    e.printStackTrace();
                    valueAnimator.cancel();
                }
            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    public void setTextMedium(int textId) {
        TextView titleText = V(textId);
    }

    @Override
    public void dismiss() {
        KeyBoardUtils.hideInputForce(this);
        super.dismiss();
    }
}
