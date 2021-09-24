package com.nwld.defi.tools.entity;

import org.json.JSONObject;

import java.math.BigInteger;

public class SwapPair {
    public String address;
    public String token0;
    public String token1;
    public BigInteger token0Balance;
    public BigInteger token1Balance;

    public long timeStamp;//新增的才有该字段
    public BigInteger token0InitBalance;//创建池子时的余额，有可能有误差
    public BigInteger token1InitBalance;
    public boolean hasNotify;//是否已经通知，新增的才通知，通知一次

    public BigInteger token0BalanceDiff;//余额与前一次的差距
    public BigInteger token1BalanceDiff;

    public Chain chain;//哪条链
    public Swap swap;//哪个交易所

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("chain", chain.symbol);
            jsonObject.put("swap", swap.name);
            jsonObject.put("address", address);
            jsonObject.put("token0", token0);
            jsonObject.put("token1", token1);
            jsonObject.put("timeStamp", timeStamp);
            jsonObject.put("token0InitBalance", token0InitBalance.toString());
            jsonObject.put("token1InitBalance", token1InitBalance.toString());
            jsonObject.put("token0Balance", token0Balance.toString());
            jsonObject.put("token1Balance", token1Balance.toString());
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String toString() {
        return "SwapPair{" +
                "address='" + address + '\'' +
                ", token0='" + token0 + '\'' +
                ", token1='" + token1 + '\'' +
                ", token0Balance=" + token0Balance +
                ", token1Balance=" + token1Balance +
                ", timeStamp=" + timeStamp +
                ", token0InitBalance=" + token0InitBalance +
                ", token1InitBalance=" + token1InitBalance +
                ", hasNotify=" + hasNotify +
                ", token0BalanceDiff=" + token0BalanceDiff +
                ", token1BalanceDiff=" + token1BalanceDiff +
                ", chain=" + chain +
                ", swap=" + swap +
                '}';
    }
}
