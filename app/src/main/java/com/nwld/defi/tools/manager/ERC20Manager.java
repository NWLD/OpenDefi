package com.nwld.defi.tools.manager;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.ERC20;
import com.nwld.defi.tools.model.ERC20Model;

import java.util.HashMap;
import java.util.Map;

public class ERC20Manager {
    private final Map<String, ERC20Model> erc20ModelMap = new HashMap<>();
    private final MutableLiveData<Integer> erc20Data = new MutableLiveData<>();
    private final Map<String, ERC20> erc20Map = new HashMap<>();

    private static class ManagerHolder {
        private static final ERC20Manager manager = new ERC20Manager();
    }

    public static ERC20Manager getInstance() {
        return ManagerHolder.manager;
    }

    private ERC20Manager() {

    }

    public ERC20Model erc20Model(Chain chain) {
        ERC20Model erc20Model = erc20ModelMap.get(chain.symbol);
        if (null == erc20Model) {
            erc20Model = new ERC20Model(chain);
            erc20ModelMap.put(chain.symbol, erc20Model);
        }
        return erc20Model;
    }

    public void watchERC20Map(LifecycleOwner lifecycleOwner, Observer<Integer> observer) {
        if (null == lifecycleOwner || null == observer) {
            return;
        }
        erc20Data.observe(lifecycleOwner, observer);
    }

    public void unWatchERC20Map(Observer<Integer> observer) {
        if (null == observer) {
            return;
        }
        erc20Data.removeObserver(observer);
    }

    public Map<String, ERC20> getErc20Map() {
        Map<String, ERC20> map = new HashMap<>();
        synchronized (erc20Map) {
            map.putAll(erc20Map);
        }
        return map;
    }

    public ERC20 erc20(String address, String chain) {
        synchronized (erc20Map) {
            return erc20Map.get(addressKey4Chain(address, chain));
        }
    }

    public void postErc20Map(ERC20 erc20) {
        if (null == erc20) {
            return;
        }
        synchronized (erc20Map) {
            erc20Map.put(addressKey4Chain(erc20.address, erc20.chain.symbol), erc20);
        }
        erc20Data.postValue(1);
    }

    public static String addressKey4Chain(String address, String chain) {
        return address.toLowerCase() + " @ " + chain;
    }
}
