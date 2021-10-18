package com.nwld.defi.tools.entity;

import java.util.ArrayList;
import java.util.List;

public class Chain {
    public String symbol;
    public int decimals;
    public int chainId;
    public String rpcUrl;
    public final List<Swap> swapList = new ArrayList<>();
    //检测代币能否卖出的合约地址
    public String checkSell;
    //区块浏览器
    public String browser;

    public void addSwap(Swap swap) {
        swapList.add(swap);
    }

    @Override
    public String toString() {
        return "Chain{" +
                "symbol='" + symbol + '\'' +
                ", decimals=" + decimals +
                ", chainId=" + chainId +
                ", rpcUrl='" + rpcUrl + '\'' +
                '}';
    }
}
