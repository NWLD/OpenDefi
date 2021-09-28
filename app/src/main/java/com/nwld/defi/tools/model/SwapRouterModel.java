package com.nwld.defi.tools.model;

import com.nwld.defi.tools.async.BaseExecutor;
import com.nwld.defi.tools.async.BaseTask;
import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.Gas;
import com.nwld.defi.tools.entity.MyTransaction;
import com.nwld.defi.tools.entity.Swap;
import com.nwld.defi.tools.interfaces.OnGetAmountsOut;
import com.nwld.defi.tools.repository.SwapRouterRepository;
import com.nwld.defi.tools.web3.Web3Util;

import org.web3j.abi.datatypes.Function;

import java.math.BigInteger;
import java.util.List;

public class SwapRouterModel {
    private Chain chain;
    private Swap swap;

    public SwapRouterModel(Chain chain, Swap swap) {
        this.chain = chain;
        this.swap = swap;
    }

    public MyTransaction swapExactTokensForTokensSupportingFeeOnTransferTokensTransaction(BigInteger in, BigInteger out, List<String> path, String from) {
        Function function = SwapRouterRepository.swapExactTokensForTokensSupportingFeeOnTransferTokensFun
                (in, out, path, from);
        MyTransaction myTransaction = new MyTransaction();
        myTransaction.from = from;
        myTransaction.contract = swap.swapRouterAddress;
        myTransaction.function = function;
        myTransaction.chain = chain;
        return myTransaction;
    }

    public void getAmountsOut(BigInteger in, List<String> path, OnGetAmountsOut onGetAmountsOut) {
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    SwapRouterRepository service = new SwapRouterRepository(chain, swap);
                    BigInteger out = service.getAmountsOut(in, path);
                    MainHandler.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            onGetAmountsOut.onGetAmountsOut(in, out);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }
        });
    }
}
