package com.nwld.defi.tools.ui.more;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.constant.ChainConstant;
import com.nwld.defi.tools.entity.MyTransaction;
import com.nwld.defi.tools.manager.BalanceManager;
import com.nwld.defi.tools.manager.KeyManager;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.ui.login.LoginDialog;
import com.nwld.defi.tools.ui.swap.SearchPairDialog;
import com.nwld.defi.tools.ui.transaction.TransactionConfirmDialog;
import com.nwld.defi.tools.util.LogUtil;
import com.nwld.defi.tools.widget.BaseDialog;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        initBuyNft();
        initValk();
    }

    private void initWithdrawWfce() {
        View withdraw = findViewById(R.id.withdraw_wfce);
        withdraw.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
//                MyTransaction transaction = new MyTransaction();
//                transaction.contract = "0x3BB4Db87DD2e67deF8a9c8673e802C10ff8b107F";
//                transaction.chain = ChainConstant.chain("BNB");
//                transaction.credentials = KeyManager.getInstance().getCredentials();
//                //只能提取解锁部分
//                Uint256 amount = new Uint256(BigInteger.valueOf(5130000000L));
//                String amountEn = TypeEncoder.encode(amount);
//                transaction.encodedFunction = "0xaf9100d1" + amountEn;
//                transaction.from = transaction.credentials.getAddress();
//                TransactionConfirmDialog.show(baseActivity, transaction, null);

//                MyTransaction transaction = new MyTransaction();
//                transaction.contract = "0xc1fae1924303cc7a816919b7a3935cda8bf8ef3d";
//                transaction.chain = ChainConstant.chain("MATIC");
//                transaction.credentials = KeyManager.getInstance().getCredentials();
//                Function fun = new Function(
//                        "buyAndFree22457070633",
//                        Arrays.asList(new Utf8String("邓浩发")),
//                        Collections.emptyList());
//                String encodedFunction = FunctionEncoder.encode(fun);
//                LogUtil.e("encodedFunction",encodedFunction);
//
//                transaction.encodedFunction = "0x00000000"+encodedFunction.substring(10,encodedFunction.length());
//                transaction.from = transaction.credentials.getAddress();
//                LogUtil.e("encoded",transaction.encodedFunction);
//                TransactionConfirmDialog.show(baseActivity, transaction, null);

//                Sign.signedPrefixedMessageToKey("",KeyManager.getInstance().getCredentials().getEcKeyPair());
            }
        });
    }

    private String sha3(String str){
        final byte[] input = str.getBytes();
        final byte[] hash = Hash.sha3(input);
        return Numeric.toHexString(hash);
    }


    private void initValk() {
        View valkView = findViewById(R.id.valk);
        valkView.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {

            }
        });
    }

    private void initBuyNft() {
        View withdraw = findViewById(R.id.buy_nft);
        withdraw.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                MyTransaction transaction = new MyTransaction();
                transaction.contract = "0x232d3ec5feee0e5cac69caf004926bee398e6f0c";
                transaction.chain = ChainConstant.chain("BNB");
                transaction.credentials = KeyManager.getInstance().getCredentials();
                transaction.encodedFunction = "0xe2f639b6" + "0000000000000000000000000000000000000000000000000000000000000001";
                transaction.from = transaction.credentials.getAddress();
                transaction.quickGas = 120;
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
