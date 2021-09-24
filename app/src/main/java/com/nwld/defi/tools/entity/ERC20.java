package com.nwld.defi.tools.entity;

public class ERC20 {
    public String address;
    public String symbol;
    public int decimals;
    public Chain chain;

    @Override
    public String toString() {
        return "ERC20{" +
                "address='" + address + '\'' +
                ", symbol='" + symbol + '\'' +
                ", decimals='" + decimals + '\'' +
                '}';
    }
}
