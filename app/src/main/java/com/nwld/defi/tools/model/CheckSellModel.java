package com.nwld.defi.tools.model;

import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.MyTransaction;
import com.nwld.defi.tools.entity.Swap;
import com.nwld.defi.tools.repository.CheckSellRepository;

import org.web3j.abi.datatypes.Function;

import java.util.List;

public class CheckSellModel {
    private Chain chain;
    private Swap swap;

    public CheckSellModel(Chain chain, Swap swap) {
        this.chain = chain;
        this.swap = swap;
    }

    public MyTransaction checkSellTransaction(String from, List<String> path) {
        Function function = CheckSellRepository.checkSellFun(swap.swapRouterAddress, path);
        MyTransaction myTransaction = new MyTransaction();
        myTransaction.from = from;
        myTransaction.contract = chain.checkSell;
        myTransaction.function = function;
        myTransaction.chain = chain;
        return myTransaction;
    }
}
