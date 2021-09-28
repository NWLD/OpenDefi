package com.nwld.defi.tools.entity;

import java.math.BigInteger;

public class Balance {
    public String address;
    public Chain chain;
    public String erc20Address;
    public BigInteger balance;
    public  boolean stopRefresh;

    @Override
    public String toString() {
        return "Balance{" +
                "address='" + address + '\'' +
                ", chain=" + chain +
                ", erc20Address='" + erc20Address + '\'' +
                ", balance=" + balance +
                '}';
    }
}
