package com.nwld.defi.tools.ui.transaction;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.async.BaseExecutor;
import com.nwld.defi.tools.async.BaseTask;
import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.entity.Gas;
import com.nwld.defi.tools.entity.MyTransaction;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.util.LogUtil;
import com.nwld.defi.tools.util.ToastUtil;
import com.nwld.defi.tools.web3.Web3Util;
import com.nwld.defi.tools.widget.BaseDialog;

import java.math.BigInteger;

public class TransactionConfirmDialog extends BaseDialog {

    public interface OnTransactionHash {
        void onHash(String hash);
    }

    MyTransaction transaction;
    OnTransactionHash onTransactionHash;
    boolean autoConfirm;

    public TransactionConfirmDialog(BaseActivity context, MyTransaction transaction) {
        super(context);
        this.transaction = transaction;
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public static void show(BaseActivity activity, MyTransaction transaction, OnTransactionHash onTransactionHash) {
        show(activity, transaction, onTransactionHash, false);
    }

    public static void show(BaseActivity activity, MyTransaction transaction, OnTransactionHash onTransactionHash, boolean autoConfirm) {
        activity.showLoading();
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    Gas gas = Web3Util.getInstance().ethEstimateGas(transaction);
                    MainHandler.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (activity.isDestroyed()) {
                                return;
                            }
                            TransactionConfirmDialog dialog = new TransactionConfirmDialog(activity, transaction);
                            dialog.onTransactionHash = onTransactionHash;
                            dialog.autoConfirm = autoConfirm;
                            activity.hideLoading();
                            dialog.show();
                        }
                    });
                } catch (Exception e) {
                    MainHandler.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (activity.isDestroyed()) {
                                return;
                            }
                            ToastUtil.showToast(activity, e.getMessage(), true);
                            activity.hideLoading();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_transaction_confirm;
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
        initTransactionView();
    }

    private TextView confirmButton;
    private TextView cancelButton;
    private TextView showValueText;
    private TextView receiptAddressText;
    private TextView gasText;
    private TextView gasDetailText;

    private TextView x2gasButton;
    private TextView x3gasButton;
    private TextView x5gasButton;

    public void initTransactionView() {
        confirmButton = findViewById(R.id.confirm_pay);
        cancelButton = findViewById(R.id.cancel);
        showValueText = findViewById(R.id.show_value);
        receiptAddressText = findViewById(R.id.receipt_address);
        gasText = findViewById(R.id.gas);
        gasDetailText = findViewById(R.id.gas_detail);

        receiptAddressText.setText(transaction.contract);
        gasText.setText(transaction.gasLabel());
        gasDetailText.setText(transaction.gasDetailLabel());
        showValueText.setText(transaction.showValue);

        cancelButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                hideThisDialog();
            }
        });

        if (transaction.isApproval) {
            confirmButton.setText(R.string.confirm_approval);
        }
        confirmButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                confirmAction();
            }
        });
        //只显示，不发起交易
        if (transaction.justShow) {
            confirmButton.setEnabled(false);
        }
        if (autoConfirm) {
            confirmAction();
        }

        x2gasButton = findViewById(R.id.x2gas);
        x3gasButton = findViewById(R.id.x3gas);
        x5gasButton = findViewById(R.id.x5gas);
        x2gasButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                multiGas(2);
            }
        });
        x3gasButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                multiGas(3);
            }
        });
        x5gasButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                multiGas(5);
            }
        });
    }

    private void multiGas(int multi) {
        transaction.gas.gasPrice = transaction.gas.gasPrice.multiply(BigInteger.valueOf(multi));
        gasText.setText(transaction.gasLabel());
        gasDetailText.setText(transaction.gasDetailLabel());
    }

    private void confirmAction() {
        baseActivity.showLoading();
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    String hash = Web3Util.getInstance().execute(transaction);
                    LogUtil.e("hash", hash);
                    MainHandler.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            baseActivity.hideLoading();
                            if (null != onTransactionHash) {
                                onTransactionHash.onHash(hash);
                            }
                            hideThisDialog();
                        }
                    });
                } catch (Exception e) {
                    MainHandler.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            baseActivity.hideLoading();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }

}
