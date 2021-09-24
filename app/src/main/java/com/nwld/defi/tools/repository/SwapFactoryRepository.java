package com.nwld.defi.tools.repository;

import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.web3.Web3Util;

import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SwapFactoryRepository {
    public Chain chain;
    String factoryAddress;

    public SwapFactoryRepository(Chain chain, String address) {
        this.chain = chain;
        this.factoryAddress = address;
    }

    public BigInteger allPairsLength() throws Exception {
        Function function = new Function(
                "allPairsLength",
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint256>() {
                })
        );
        String responseValue = Web3Util.getInstance().callSmartContractFunction(chain, function, factoryAddress);
        List<Type> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        return (BigInteger) response.get(0).getValue();
    }

    public String pair(int index) throws Exception {
        Function function = new Function(
                "allPairs",
                Arrays.asList(new Uint256(index)),
                Arrays.asList(new TypeReference<Address>() {
                }));
        String responseValue = Web3Util.getInstance().callSmartContractFunction(chain, function, factoryAddress);
        List<Type> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        return response.get(0).toString();
    }
}
