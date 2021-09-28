package com.nwld.defi.tools.model;

import com.nwld.defi.tools.async.BaseExecutor;
import com.nwld.defi.tools.async.BaseTask;
import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.ERC20;
import com.nwld.defi.tools.entity.MyTransaction;
import com.nwld.defi.tools.manager.ERC20Manager;
import com.nwld.defi.tools.repository.ERC20Repository;
import com.nwld.defi.tools.repository.SwapRouterRepository;

import org.web3j.abi.datatypes.Function;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ERC20Model {
    private Map<String, Boolean> loadingMap = new HashMap<>();
    private final Map<String, ERC20> dataMap = new HashMap<>();
    private Chain chain;

    public ERC20Model(Chain chain) {
        this.chain = chain;
    }

    public Map<String, ERC20> getDataMap() {
        Map<String, ERC20> map = new HashMap<>();
        synchronized (dataMap) {
            map.putAll(dataMap);
        }
        return map;
    }

    public void postDataMap(ERC20 erc20) {
        if (null == erc20) {
            return;
        }
        ERC20Manager.getInstance().postErc20Map(erc20);
    }

    public void getERC20Info(String address) {
        Map<String, ERC20> map = getDataMap();
        if (null != map.get(address.toLowerCase())) {
            return;
        }
        synchronized (loadingMap) {
            if (null != loadingMap.get(address.toLowerCase())) {
                return;
            }
            loadingMap.put(address.toLowerCase(), true);
        }
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    ERC20Repository service = new ERC20Repository(chain, address);
                    ERC20 erc20 = new ERC20();
                    erc20.chain = chain;
                    erc20.address = address;
                    erc20.symbol = service.symbol();
                    erc20.decimals = service.decimals();
                    postDataMap(erc20);
                } catch (Exception e) {
                    e.printStackTrace();
                    synchronized (loadingMap) {
                        loadingMap.remove(address.toLowerCase());
                    }
                    repeatGetERC20Info(address);
                }
            }
        });
    }

    public void repeatGetERC20Info(String address) {
        MainHandler.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getERC20Info(address);
            }
        }, 3000);
    }

    public MyTransaction approveTransaction(String spender, BigInteger amount, String from, String erc20Address) {
        Function function = ERC20Repository.approveFun(spender, amount);
        MyTransaction myTransaction = new MyTransaction();
        myTransaction.from = from;
        myTransaction.contract = erc20Address;
        myTransaction.function = function;
        myTransaction.chain = chain;
        return myTransaction;
    }
}
