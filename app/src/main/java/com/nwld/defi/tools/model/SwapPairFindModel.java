package com.nwld.defi.tools.model;

import com.nwld.defi.tools.MyApp;
import com.nwld.defi.tools.async.BaseExecutor;
import com.nwld.defi.tools.async.BaseTask;
import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.constant.IntentConstant;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.ERC20;
import com.nwld.defi.tools.entity.Swap;
import com.nwld.defi.tools.entity.SwapPair;
import com.nwld.defi.tools.manager.ERC20Manager;
import com.nwld.defi.tools.manager.NotifyManager;
import com.nwld.defi.tools.manager.SwapPairWatchManager;
import com.nwld.defi.tools.repository.ERC20Repository;
import com.nwld.defi.tools.repository.SwapFactoryRepository;
import com.nwld.defi.tools.repository.SwapPairRepository;
import com.nwld.defi.tools.util.LogUtil;
import com.nwld.defi.tools.util.SPUtil;
import com.nwld.defi.tools.util.StringUtil;

import java.math.BigInteger;

public class SwapPairFindModel {
    private int lastLength;

    private Chain chain;
    private Swap swap;
    private ERC20Model erc20Model;

    public SwapPairFindModel(Chain chain, Swap swap, ERC20Model erc20Model) {
        this.chain = chain;
        this.swap = swap;
        this.erc20Model = erc20Model;
        String fileName = SwapPairWatchManager.swapFileName(swap);
        String lastLength = SPUtil.get(MyApp.getContext(), fileName, IntentConstant.lastLength);
        if (!StringUtil.isEmpty(lastLength)) {
            this.lastLength = StringUtil.str2Int(lastLength);
        }
    }

    private boolean loadingList;

    public void refreshList() {
        if (loadingList) {
            return;
        }
        loadingList = true;
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    SwapFactoryRepository swapFactoryRepository = new SwapFactoryRepository(chain, swap.swapFactoryAddress);
                    int len = swapFactoryRepository.allPairsLength().intValue();
                    LogUtil.e(chain.symbol, len);
                    if (len <= lastLength) {
                        return;
                    }
                    int index = lastLength;
                    lastLength = len;
                    String fileName = SwapPairWatchManager.swapFileName(swap);
                    SPUtil.set(MyApp.getContext(), fileName, IntentConstant.lastLength, lastLength + "");
                    //这里只拉取新增的交易对
                    if (0 == index) {
                        return;
                    }
                    for (; index < len; index++) {
                        SwapPair swapPair = new SwapPair();
                        swapPair.chain = chain;
                        swapPair.swap = swap;
                        swapPair.address = swapFactoryRepository.pair(index);
                        swapPair.timeStamp = System.currentTimeMillis();
                        getPairInfo(swapPair);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    loadingList = false;
                    repeatGetList();
                }
            }
        });
    }

    void repeatGetList() {
        MainHandler.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshList();
            }
        }, 6000);
    }

    public void getPairInfo(SwapPair swapPair) {
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    //交易对token
                    SwapPairRepository pairService = new SwapPairRepository(chain, swapPair.address);
                    swapPair.token0 = pairService.token0();
                    erc20Model.getERC20Info(swapPair.token0);
                    swapPair.token1 = pairService.token1();
                    erc20Model.getERC20Info(swapPair.token1);
                    //交易对token余额
                    ERC20Repository service = new ERC20Repository(chain, swapPair.token0);
                    swapPair.token0Balance = service.balanceOf(swapPair.address);
                    service.erc20Address = swapPair.token1;
                    swapPair.token1Balance = service.balanceOf(swapPair.address);
                    swapPair.token0InitBalance = swapPair.token0Balance;
                    swapPair.token1InitBalance = swapPair.token1Balance;
                    //余额为0，过3秒再拉取一次试试
                    if (swapPair.token0InitBalance.equals(BigInteger.ZERO)
                            && swapPair.token1InitBalance.equals(BigInteger.ZERO)) {
                        Thread.sleep(3000);
                        service.erc20Address = swapPair.token0;
                        swapPair.token0Balance = service.balanceOf(swapPair.address);
                        service.erc20Address = swapPair.token1;
                        swapPair.token1Balance = service.balanceOf(swapPair.address);
                        swapPair.token0InitBalance = swapPair.token0Balance;
                        swapPair.token1InitBalance = swapPair.token1Balance;
                    }
                    confirmERC20Token(swapPair);
                } catch (Exception e) {
                    e.printStackTrace();
                    repeatGetPairInfo(swapPair);
                }
            }
        });
    }

    public void confirmERC20Token(SwapPair swapPair) {
        ERC20 token0 = ERC20Manager.getInstance().erc20(swapPair.token0, swapPair.chain.symbol);
        if (null == token0) {
            repeatConfirmERC20Token(swapPair);
            return;
        }
        ERC20 token1 = ERC20Manager.getInstance().erc20(swapPair.token1, swapPair.chain.symbol);
        if (null == token1) {
            repeatConfirmERC20Token(swapPair);
            return;
        }
        NotifyManager.getInstance().showNewPair(swapPair, token0, token1);
    }

    void repeatConfirmERC20Token(SwapPair swapPair) {
        MainHandler.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                confirmERC20Token(swapPair);
            }
        }, 3000);
    }

    void repeatGetPairInfo(SwapPair swapPair) {
        MainHandler.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getPairInfo(swapPair);
            }
        }, 3000);
    }
}
