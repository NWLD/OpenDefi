package com.nwld.defi.tools.manager;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.nwld.defi.tools.entity.Balance;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.model.BalanceModel;

import java.util.HashMap;
import java.util.Map;

public class BalanceManager {
    private final Map<String, BalanceModel> balanceModelMap = new HashMap<>();
    private final MutableLiveData<Balance> balanceData = new MutableLiveData<>();
    private final Map<String, Balance> balanceMap = new HashMap<>();

    private static class ManagerHolder {
        private static final BalanceManager manager = new BalanceManager();
    }

    public static BalanceManager getInstance() {
        return ManagerHolder.manager;
    }

    private BalanceManager() {

    }

    public void watchBalanceData(LifecycleOwner lifecycleOwner, Observer<Balance> observer) {
        if (null == lifecycleOwner || null == observer) {
            return;
        }
        balanceData.observe(lifecycleOwner, observer);
    }

    public void unWatchBalanceData(Observer<Balance> observer) {
        if (null == observer) {
            return;
        }
        balanceData.removeObserver(observer);
    }

    private Map<String, Balance> getBalanceMap() {
        Map<String, Balance> map = new HashMap<>();
        synchronized (balanceMap) {
            map.putAll(balanceMap);
        }
        return map;
    }

    private BalanceModel balanceModel(Chain chain) {
        synchronized (balanceModelMap) {
            BalanceModel model = balanceModelMap.get(chain.symbol);
            if (null == model) {
                model = new BalanceModel(chain);
                balanceModelMap.put(chain.symbol, model);
            }
            return model;
        }
    }

    public void refreshBalance(Balance balance) {
        BalanceModel model = balanceModel(balance.chain);
        model.refreshBalance(balance);
    }

    public Balance balance(String key) {
        synchronized (balanceMap) {
            return balanceMap.get(key);
        }
    }

    public void postBalance(Balance balance) {
        if (null == balance) {
            return;
        }
        String key = balanceKey(balance);
        synchronized (balanceMap) {
            balanceMap.put(key, balance);
        }
        balanceData.postValue(balance);
    }

    public static String balanceKey(Balance balance) {
        if (null == balance) {
            return "";
        }
        return balance.address.toLowerCase() + " @ " + balance.erc20Address.toLowerCase() + " @ " + balance.chain.symbol;
    }

    public void clear() {
        synchronized (balanceMap) {
            balanceMap.clear();
        }
    }
}
