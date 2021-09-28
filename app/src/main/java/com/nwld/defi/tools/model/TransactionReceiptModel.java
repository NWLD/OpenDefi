package com.nwld.defi.tools.model;

import com.nwld.defi.tools.async.BaseExecutor;
import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.interfaces.OnTransactionStatus;
import com.nwld.defi.tools.util.LogUtil;
import com.nwld.defi.tools.web3.Web3Util;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

public class TransactionReceiptModel {
    private Chain chain;
    private String hash;
    private OnTransactionStatus onTransactionStatus;
    private int tryCount = 20;

    public TransactionReceiptModel(Chain chain, String hash, OnTransactionStatus onTransactionStatus) {
        this.chain = chain;
        this.hash = hash;
        this.onTransactionStatus = onTransactionStatus;
    }

    public void getTransactionStatus() {
        MainHandler.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                transactionReceipt();
            }
        }, 3000);
    }

    private void transactionReceipt() {
        BaseExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionReceipt receipt = Web3Util.getInstance().transactionReceipt(chain, hash);
                    if (null == receipt) {
                        LogUtil.e("receipt", "null");
                        repeatGetTransactionStatus();
                        return;
                    }
                    int status;
                    if (receipt.isStatusOK()) {
                        status = 0;
                    } else {
                        status = 1;
                    }
                    final int resultStatus = status;
                    MainHandler.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            onTransactionStatus.onStatus(resultStatus);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    repeatGetTransactionStatus();
                }
            }
        });
    }

    private void repeatGetTransactionStatus() {
        if (0 == tryCount) {
            MainHandler.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    onTransactionStatus.onStatus(1);
                }
            });
            return;
        }
        getTransactionStatus();
        tryCount--;
    }
}
