package com.nwld.defi.tools.ui.more;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.manager.BalanceManager;
import com.nwld.defi.tools.manager.KeyManager;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.ui.login.LoginDialog;
import com.nwld.defi.tools.widget.BaseDialog;

import org.web3j.crypto.Credentials;

public class MoreDialog extends BaseDialog {

    public MoreDialog(BaseActivity context) {
        super(context);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_more;
    }

    @Override
    public double getWidthRatio() {
        return 1;
    }

    private void setContentHeight() {
        View content = V(R.id.dialog_container);
        LinearLayout.LayoutParams contentLayoutParams = (LinearLayout.LayoutParams) content.getLayoutParams();
        contentLayoutParams.height = baseActivity.heightPixels * 80 / 100;
        content.setLayoutParams(contentLayoutParams);
    }


    public void initView() {
        setContentHeight();
        View dismissView = V(R.id.dialog_dismiss);
        dismissView.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                hideThisDialog();
            }
        });

        View closeView = V(R.id.dialog_back_layout);
        closeView.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                hideThisDialog();
            }
        });
        initLoginView();
    }

    public void hideThisDialog() {
        dismiss();
    }

    private TextView loginButton;

    public void initLoginView() {
        loginButton = V(R.id.login);
        Credentials credentials = KeyManager.getInstance().getCredentials();
        if (null == credentials) {
            loginButton.setBackgroundResource(R.drawable.base_button);
            loginButton.setText(R.string.login);
        } else {
            loginButton.setBackgroundResource(R.drawable.base_warning_button);
            loginButton.setText(R.string.exit_login);
        }
        loginButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                hideThisDialog();
                if (null == credentials) {
                    LoginDialog loginDialog = new LoginDialog(baseActivity);
                    loginDialog.show();
                } else {
                    try {
                        BalanceManager.getInstance().clear();
                        KeyManager.getInstance().savePrivateKey(" ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
