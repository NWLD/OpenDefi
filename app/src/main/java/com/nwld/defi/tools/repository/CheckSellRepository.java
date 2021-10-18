package com.nwld.defi.tools.repository;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CheckSellRepository {
    public static Function checkSellFun(
            String swapRouterAddress,
            List<String> path
    ) {
        List<Address> addressList = new ArrayList<>();
        for (int index = 0; index < path.size(); index++) {
            addressList.add(new Address(path.get(index)));
        }
        DynamicArray<Address> pathList = new DynamicArray<Address>(addressList);
        return new Function(
                "checkSell",
                Arrays.asList(new Address(swapRouterAddress), pathList),
                Collections.emptyList());
    }
}
