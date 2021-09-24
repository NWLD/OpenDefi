package com.nwld.defi.tools.repository;

import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.Swap;
import com.nwld.defi.tools.web3.Web3Util;

import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SwapRouterRepository {
    public Chain chain;
    public Swap swap;
    static final Credentials ALICE =
            Credentials.create(
                    "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63" // 32 byte
                    // hex
                    // value
                    // 64 byte hex value
            );

    public SwapRouterRepository(Chain chain, Swap swap) {
        this.chain = chain;
        this.swap = swap;
    }

    public BigInteger getAmountsOut(BigInteger in, List<String> path) throws Exception {
        List<Address> addressList = new ArrayList<>();
        for (int index = 0; index < path.size(); index++) {
            addressList.add(new Address(path.get(index)));
        }
        DynamicArray<Address> pathList = new DynamicArray<Address>(addressList);
        Function function = new Function(
                "getAmountsOut",
                Arrays.asList(new Uint256(in), pathList),
                Arrays.asList(new TypeReference<DynamicArray<Uint256>>() {
                }));
        String responseValue = Web3Util.getInstance().callSmartContractFunction(chain, function, swap.swapRouterAddress);
        List<Type> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        DynamicArray<Uint256> array = (DynamicArray<Uint256>) response.get(0);
        return array.getValue().get(0).getValue();
    }

    public Function swapExactTokensForTokensSupportingFeeOnTransferTokensFun(
            BigInteger in, BigInteger out, List<String> path, String to
    ) {
        long deadLine = System.currentTimeMillis() / 1000 + 60;
        List<Address> addressList = new ArrayList<>();
        for (int index = 0; index < path.size(); index++) {
            addressList.add(new Address(path.get(index)));
        }
        DynamicArray<Address> pathList = new DynamicArray<Address>(addressList);
        return new Function(
                "swapExactTokensForTokensSupportingFeeOnTransferTokens",
                Arrays.asList(new Uint256(in), new Uint256(out), pathList, new Address(to), new Uint256(deadLine)),
                Collections.emptyList());
    }
}
