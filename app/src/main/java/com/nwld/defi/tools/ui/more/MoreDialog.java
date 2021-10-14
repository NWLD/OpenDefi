package com.nwld.defi.tools.ui.more;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.constant.ChainConstant;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.MyTransaction;
import com.nwld.defi.tools.manager.BalanceManager;
import com.nwld.defi.tools.manager.KeyManager;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.ui.login.LoginDialog;
import com.nwld.defi.tools.ui.swap.SearchPairDialog;
import com.nwld.defi.tools.ui.transaction.TransactionConfirmDialog;
import com.nwld.defi.tools.widget.BaseDialog;
import com.nwld.defi.tools.widget.ConfirmCancelDialog;

import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;

import java.math.BigInteger;

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
        initWithdrawWfce();
        initSearchPairView();
    }

    private void initWithdrawWfce() {
        View withdraw = findViewById(R.id.withdraw_wfce);
        withdraw.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                MyTransaction transaction = new MyTransaction();
                transaction.contract = "0x3BB4Db87DD2e67deF8a9c8673e802C10ff8b107F";
                transaction.chain = ChainConstant.chain("BNB");
                transaction.credentials = KeyManager.getInstance().getCredentials();
                //等全部解锁完后就可以了
                Uint256 amount = new Uint256(BigInteger.valueOf(4600000000L));
                String amountEn = TypeEncoder.encode(amount);
                transaction.encodedFunction = "0xaf9100d1" + amountEn;
                transaction.from = transaction.credentials.getAddress();
                TransactionConfirmDialog.show(baseActivity, transaction, null);
            }
        });
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

    private TextView searchPairButton;

    public void initSearchPairView() {
        searchPairButton = V(R.id.search_pair);
        searchPairButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                hideThisDialog();
                SearchPairDialog dialog = new SearchPairDialog(baseActivity);
                dialog.show();
            }
        });
    }

}
