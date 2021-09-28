package com.nwld.defi.tools.model;

import com.nwld.defi.tools.async.BaseExecutor;
import com.nwld.defi.tools.async.BaseTask;
import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.entity.Balance;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.manager.BalanceManager;
import com.nwld.defi.tools.manager.ERC20Manager;
import com.nwld.defi.tools.repository.ERC20Repository;

import java.util.HashMap;
import java.util.Map;

public class BalanceModel {
    private final Map<String, Boolean> loadingMap = new HashMap<>();
    private Chain chain;

    public BalanceModel(Chain chain) {
        this.chain = chain;
    }

    private void postDataMap(Balance balance) {
        if (null == balance) {
            return;
        }
        BalanceManager.getInstance().postBalance(balance);
    }

    public void refreshBalance(Balance balance) {
        synchronized (loadingMap) {
            if (null != loadingMap.get(balance.erc20Address.toLowerCase())) {
                return;
            }
            loadingMap.put(balance.erc20Address.toLowerCase(), true);
        }
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    ERC20Repository service = new ERC20Repository(chain, balance.erc20Address);
                    balance.balance = service.balanceOf(balance.address);
                    postDataMap(balance);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    synchronized (loadingMap) {
                        loadingMap.remove(balance.erc20Address.toLowerCase());
                    }
                    repeatGetBalance(balance);
                }
            }
        });
    }

    private void repeatGetBalance(Balance balance) {
        if (balance.stopRefresh) {
            return;
        }
        MainHandler.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshBalance(balance);
            }
        }, 3000);
    }
}
