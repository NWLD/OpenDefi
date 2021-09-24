package com.nwld.defi.tools.repository;

import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.web3.Web3Util;

import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SwapPairRepository {
    public Chain chain;
    public String pairAddress;

    public SwapPairRepository(Chain chain, String address) {
        this.chain = chain;
        this.pairAddress = address;
    }

    public String token0() throws Exception {
        Function function = new Function(
                "token0",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Address>() {
                }));
        String responseValue = Web3Util.getInstance().callSmartContractFunction(chain, function, pairAddress);
        List<Type> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        return response.get(0).toString();
    }

    public String token1() throws Exception {
        Function function = new Function(
                "token1",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Address>() {
                }));
        String responseValue = Web3Util.getInstance().callSmartContractFunction(chain, function, pairAddress);
        List<Type> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        return response.get(0).toString();
    }
}
