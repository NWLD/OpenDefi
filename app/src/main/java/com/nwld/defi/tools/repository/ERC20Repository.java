package com.nwld.defi.tools.repository;

import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.web3.Web3Util;

import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ERC20Repository {
    public Chain chain;
    public String erc20Address;

    public ERC20Repository(Chain chain, String address) {
        this.chain = chain;
        this.erc20Address = address;
    }

    //查询代币符号
    public String symbol() throws Exception {
        Function function = new Function(
                "symbol",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Utf8String>() {
                }));
        String responseValue = Web3Util.getInstance().callSmartContractFunction(chain, function, erc20Address);
        List<Type> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        return response.get(0).toString();
    }

    //查询代币精度
    public int decimals() throws Exception {
        Function function = new Function(
                "decimals",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Uint8>() {
                }));
        String responseValue = Web3Util.getInstance().callSmartContractFunction(chain, function, erc20Address);
        List<Type> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        return ((Uint8) response.get(0)).getValue().intValue();
    }

    //查询授权额度
    public BigInteger allowance(String owner, String spender) throws Exception {
        Function function = new Function(
                "allowance",
                Arrays.asList(new Address(owner), new Address(spender)),
                Arrays.asList(new TypeReference<Uint256>() {
                }));
        String responseValue = Web3Util.getInstance().callSmartContractFunction(chain, function, erc20Address);
        List<Type> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        return ((Uint256) response.get(0)).getValue();
    }

    //查询余额
    public BigInteger balanceOf(String address) throws Exception {
        Function function = new Function(
                "balanceOf",
                Arrays.asList(new Address(address)),
                Arrays.asList(new TypeReference<Uint256>() {
                }));
        String responseValue = Web3Util.getInstance().callSmartContractFunction(chain, function, erc20Address);
        List<Type> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        return ((Uint256) response.get(0)).getValue();
    }

    public static Function approveFun(String spender, BigInteger amount){
        return new Function(
                "approve",
                Arrays.asList(new Address(spender), new Uint256(amount)),
                Collections.emptyList());
    }
}
