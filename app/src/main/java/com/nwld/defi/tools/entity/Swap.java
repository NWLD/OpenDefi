package com.nwld.defi.tools.entity;

public class Swap {
    public Chain chain;
    public String name;
    public String tokenSymbol;
    public String swapFactoryAddress;
    public String swapRouterAddress;
    public boolean findAll = false;

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
