package com.nwld.defi.tools.model;

import com.nwld.defi.tools.async.BaseExecutor;
import com.nwld.defi.tools.async.BaseTask;
import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.ChainSwap;
import com.nwld.defi.tools.entity.Swap;
import com.nwld.defi.tools.interfaces.OnSearchPair;
import com.nwld.defi.tools.repository.SwapFactoryRepository;

public class SwapPairSearchModel {
    private Chain chain;
    private Swap swap;
    private OnSearchPair onSearchPair;
    private String pair;

    public SwapPairSearchModel(Chain chain, Swap swap) {
        this.chain = chain;
        this.swap = swap;
    }

    public void searchPair(String token0, String token1, ChainSwap chainSwap, OnSearchPair onSearchPair) {
        this.onSearchPair = onSearchPair;
        this.pair = null;
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    SwapFactoryRepository swapFactoryRepository = new SwapFactoryRepository(chain, swap.swapFactoryAddress);
                    pair = swapFactoryRepository.searchPair(token0, token1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    MainHandler.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            onSearchPair.onPair(chainSwap, pair);
                        }
                    });
                }
            }
        });
    }
}
