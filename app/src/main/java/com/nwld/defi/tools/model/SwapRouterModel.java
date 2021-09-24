package com.nwld.defi.tools.model;

import com.nwld.defi.tools.async.BaseExecutor;
import com.nwld.defi.tools.async.BaseTask;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.Gas;
import com.nwld.defi.tools.entity.MyTransaction;
import com.nwld.defi.tools.entity.Swap;
import com.nwld.defi.tools.repository.SwapRouterRepository;
import com.nwld.defi.tools.util.LogUtil;
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

    public void swapExactTokensForTokensSupportingFeeOnTransferTokensGas(BigInteger in, BigInteger out, List<String> path, String from) {
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    SwapRouterRepository service = new SwapRouterRepository(chain, swap);
                    Function function = service.swapExactTokensForTokensSupportingFeeOnTransferTokensFun
                            (in, out, path, from);
                    MyTransaction myTransaction = new MyTransaction();
                    myTransaction.from = from;
                    myTransaction.contract = swap.swapRouterAddress;
                    myTransaction.function = function;
                    myTransaction.chain = chain;
                    Gas gas = Web3Util.getInstance().ethEstimateGas(myTransaction);
                    LogUtil.e("hash", gas);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }
        });
    }

    public void getAmountsOut(BigInteger in, List<String> path) {
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    SwapRouterRepository service = new SwapRouterRepository(chain, swap);
                    BigInteger out = service.getAmountsOut(in, path);
                    LogUtil.e("getAmountsOut", out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }
        });
    }
}
