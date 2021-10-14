package com.nwld.defi.tools.ui.login;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.manager.KeyManager;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.util.KeyBoardUtils;
import com.nwld.defi.tools.util.LogUtil;
import com.nwld.defi.tools.util.PriKeyUtil;
import com.nwld.defi.tools.util.StringUtil;
import com.nwld.defi.tools.util.ToastUtil;
import com.nwld.defi.tools.widget.BaseDialog;

import org.web3j.crypto.Credentials;

public class LoginDialog extends BaseDialog {

    public LoginDialog(BaseActivity context) {
        super(context);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_login;
    }

    @Override
    public double getWidthRatio() {
        return 1;
    }

    private void setContentHeight() {
        View content = V(R.id.login_container);
        LinearLayout.LayoutParams contentLayoutParams = (LinearLayout.LayoutParams) content.getLayoutParams();
        contentLayoutParams.height = baseActivity.heightPixels * 80 / 100;
        content.setLayoutParams(contentLayoutParams);
    }


    public void initView() {
        setContentHeight();
        View dismissView = V(R.id.dialog_login_dismiss);
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

    EditText privateKeyEdit;
    private View importButton;
    private View createButton;

    public void initLoginView() {
        importButton = V(R.id.import_private);
        createButton = V(R.id.create);
        privateKeyEdit = V(R.id.private_key_edit);
        privateKeyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setButtonStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        importButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                hideSoftInput();
                String privateKey = privateKeyEdit.getText().toString().trim();
                try {
                    KeyManager.getInstance().savePrivateKey(privateKey);
                    String savePriKey = KeyManager.getInstance().getSavePriKey();
                    if (privateKey.equals(savePriKey)) {
                        Credentials credentials = KeyManager.getInstance().getCredentials();
                        String address = credentials.getAddress();
                        if (!StringUtil.isEmpty(address)) {
                            KeyManager.getInstance().setKeyData(1);
                            hideThisDialog();
                            return;
                        }
                    }
                    KeyManager.getInstance().savePrivateKey(" ");
                    ToastUtil.showToast(baseActivity, baseActivity.getString(R.string.import_error));
                    privateKeyEdit.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                    privateKeyEdit.setText("");
                    try {
                        KeyManager.getInstance().savePrivateKey(" ");
                    } catch (Exception e1) {
                        e.printStackTrace();
                    }
                }
            }
        });
        createButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                hideSoftInput();
                String privateKey = PriKeyUtil.generateNewPrivateKey();
                try {
                    KeyManager.getInstance().savePrivateKey(privateKey);
                    String savePriKey = KeyManager.getInstance().getSavePriKey();
                    if (privateKey.equals(savePriKey)) {
                        Credentials credentials = Credentials.create(privateKey);
                        String address = credentials.getAddress();
                        if (!StringUtil.isEmpty(address)) {
                            KeyManager.getInstance().setKeyData(1);
                            hideThisDialog();
                            return;
                        }
                    }
                    KeyManager.getInstance().savePrivateKey(" ");
                    ToastUtil.showToast(baseActivity, baseActivity.getString(R.string.create_error));
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        KeyManager.getInstance().savePrivateKey(" ");
                    } catch (Exception e1) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setButtonStatus() {
        String privateKey = privateKeyEdit.getText().toString();
        LogUtil.e("privateKey", privateKey.trim().length());
        if (StringUtil.isEmpty(privateKey) || 66 != privateKey.trim().length() || !privateKey.trim().startsWith("0x")) {
            importButton.setEnabled(false);
            return;
        }
        importButton.setEnabled(true);
    }

    private void hideSoftInput() {
        KeyBoardUtils.hideSoftInput(getBaseActivity(), privateKeyEdit);
    }

}
