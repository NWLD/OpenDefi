package com.nwld.defi.tools.entity;

public class Swap {
    public Chain chain;
    public String name;
    public String tokenSymbol;
    public String swapFactoryAddress;
    public String swapRouterAddress;
    public boolean findAll = false;//首次监控新交易对，查找该swap的所有交易对，适合于比较新的swap，数据少
    public boolean findNew = true;//监控新交易对，bsc pancake新交易对太多了，不玩土狗，不想监控了，监控一些新链的新交易对就行。

    @Override
    public String toString() {
        return "Swap{" +
                "chain=" + chain +
                ", name='" + name + '\'' +
                ", tokenSymbol='" + tokenSymbol + '\'' +
                ", swapFactoryAddress='" + swapFactoryAddress + '\'' +
                ", swapRouterAddress='" + swapRouterAddress + '\'' +
                '}';
    }
}
