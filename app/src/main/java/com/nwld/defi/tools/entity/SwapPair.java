package com.nwld.defi.tools.entity;

import com.nwld.defi.tools.constant.ChainConstant;
import com.nwld.defi.tools.util.StringUtil;

import org.json.JSONObject;

import java.math.BigInteger;
import java.util.List;
import java.util.Spliterator;

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

    public boolean inWatchList = false;//是否加入监控列表
    public boolean pauseWatch = false;//暂停刷新数据，进入后台
    public boolean stopWatch = false;//停止刷新数据，删除

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
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static SwapPair fromJson(String json) {
        SwapPair swapPair = new SwapPair();
        try {
            JSONObject jsonObject = new JSONObject(json);
            swapPair.address = jsonObject.getString("address");
            swapPair.token0 = jsonObject.getString("token0");
            swapPair.token1 = jsonObject.getString("token1");
            swapPair.timeStamp = jsonObject.getLong("timeStamp");
            swapPair.token0InitBalance = new BigInteger(jsonObject.getString("token0InitBalance"));
            swapPair.token1InitBalance = new BigInteger(jsonObject.getString("token1InitBalance"));
            swapPair.token0Balance = swapPair.token0InitBalance;
            swapPair.token1Balance = swapPair.token1InitBalance;
            swapPair.chain = ChainConstant.chain(jsonObject.getString("chain"));
            String swap = jsonObject.getString("swap");
            List<Swap> swapList = swapPair.chain.swapList;
            for (int index = 0; index < swapList.size(); index++) {
                if (StringUtil.ignoreE(swap, swapList.get(index).name)) {
                    swapPair.swap = swapList.get(index);
                    break;
                }
            }
            return swapPair;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    public String key() {
        return chain.symbol + "#" + swap.name + "#" + address.toLowerCase();
    }
}
