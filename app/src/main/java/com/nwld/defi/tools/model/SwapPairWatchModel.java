package com.nwld.defi.tools.model;

import com.nwld.defi.tools.async.BaseExecutor;
import com.nwld.defi.tools.async.BaseTask;
import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.SwapPair;
import com.nwld.defi.tools.manager.SwapPairWatchManager;
import com.nwld.defi.tools.repository.ERC20Repository;
import com.nwld.defi.tools.repository.SwapPairRepository;

import java.math.BigInteger;

public class SwapPairWatchModel {
    private Chain chain;
    private ERC20Model erc20Model;

    public SwapPairWatchModel(Chain chain, ERC20Model erc20Model) {
        this.chain = chain;
        this.erc20Model = erc20Model;
    }

    public void getPairInfo(SwapPair swapPair) {
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    SwapPairRepository pairService = new SwapPairRepository(chain, swapPair.address);
                    swapPair.token0 = pairService.token0();
                    erc20Model.getERC20Info(swapPair.token0);
                    swapPair.token1 = pairService.token1();
                    erc20Model.getERC20Info(swapPair.token1);
                    balanceOf(swapPair);
                } catch (Exception e) {
                    e.printStackTrace();
                    repeatGetPairInfo(swapPair);
                }
            }
        });
    }

    private void repeatGetPairInfo(SwapPair swapPair) {
        MainHandler.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getPairInfo(swapPair);
            }
        }, 3000);
    }

    private void balanceOf(SwapPair swapPair) {
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    ERC20Repository service = new ERC20Repository(chain, swapPair.token0);
                    BigInteger token0BalanceLast = swapPair.token0Balance;
                    swapPair.token0Balance = service.balanceOf(swapPair.address);
                    if (null != token0BalanceLast && !token0BalanceLast.equals(swapPair.token0Balance)) {
                        swapPair.token0BalanceDiff = swapPair.token0Balance.subtract(token0BalanceLast);
                    }
                    service.erc20Address = swapPair.token1;
                    BigInteger token1BalanceLast = swapPair.token1Balance;
                    swapPair.token1Balance = service.balanceOf(swapPair.address);
                    if (null != token1BalanceLast && !token1BalanceLast.equals(swapPair.token1Balance)) {
                        swapPair.token1BalanceDiff = swapPair.token1Balance.subtract(token1BalanceLast);
                    }
                    //第一次拉到数据时的池子余额，用于对比
                    if (null == swapPair.token0InitBalance) {
                        swapPair.token0InitBalance = swapPair.token0Balance;
                        swapPair.token1InitBalance = swapPair.token1Balance;
                    }
                    //价格有变动才通知更新
                    if (null == token0BalanceLast
                            || null == token1BalanceLast
                            || !token0BalanceLast.equals(swapPair.token0Balance)
                            || !token1BalanceLast.equals(swapPair.token1Balance)) {
                        SwapPairWatchManager.getInstance().postBalance();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    repeatBalanceOf(swapPair);
                }
            }
        });
    }

    void repeatBalanceOf(SwapPair swapPair) {
        MainHandler.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                balanceOf(swapPair);
            }
        }, 3000);
    }
}
