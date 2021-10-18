package com.nwld.defi.tools.entity;

import com.nwld.defi.tools.util.CalcUtils;
import com.nwld.defi.tools.util.StringUtil;

import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;

import java.math.BigInteger;

public class MyTransaction {
    public String contract;
    public Function function;
    public Chain chain;
    public String from;
    public Gas gas;
    public Credentials credentials;
    public String encodedFunction;
    public String hash;
    //交易确认页显示
    public String showValue;
    public boolean isApproval;
    public int quickGas = 1;
    //只显示，不发起交易
    public boolean justShow = false;

    public String gasLabel() {
        BigInteger value = gas.gasLimit.multiply(gas.gasPrice);
        return StringUtil.trimZero(CalcUtils.decimals(value, 18, 8)) + " " + chain.symbol;
    }

    public String gasDetailLabel() {
        return "Gas(" +
                gas.gasLimit.toString() +
                ") * " +
                "Gas Price(" +
                StringUtil.trimZero(CalcUtils.decimals(gas.gasPrice, 9, 4)) +
                " Gwei)";
    }
}
