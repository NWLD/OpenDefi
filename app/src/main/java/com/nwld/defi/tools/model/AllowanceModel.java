package com.nwld.defi.tools.model;

import com.nwld.defi.tools.async.BaseExecutor;
import com.nwld.defi.tools.async.BaseTask;
import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.repository.ERC20Repository;

import java.math.BigInteger;

public class AllowanceModel {
    public interface OnAllowance {
        void onAllowance(BigInteger amount);
    }

    public Chain chain;
    public String erc20Address;

    public AllowanceModel(Chain chain, String erc20Address) {
        this.chain = chain;
        this.erc20Address = erc20Address;
    }

    public void allowance(String owner, String spender, OnAllowance onAllowance) {
        ERC20Repository repository = new ERC20Repository(this.chain, this.erc20Address);
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    BigInteger amount = repository.allowance(owner, spender);
                    onAllowance(onAllowance, amount);
                } catch (Exception e) {
                    e.printStackTrace();
                    onAllowance(onAllowance, BigInteger.ZERO);
                }
            }
        });
    }

    private void onAllowance(OnAllowance onAllowance, BigInteger amount) {
        MainHandler.getHandler().post(new Runnable() {
            @Override
            public void run() {
                onAllowance.onAllowance(amount);
            }
        });
    }
}
