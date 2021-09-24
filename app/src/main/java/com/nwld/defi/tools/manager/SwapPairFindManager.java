package com.nwld.defi.tools.manager;

import com.nwld.defi.tools.constant.ChainConstant;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.Swap;
import com.nwld.defi.tools.model.SwapPairFindModel;

import java.util.List;

public class SwapPairFindManager {

    private static class ManagerHolder {
        private static final SwapPairFindManager manager = new SwapPairFindManager();
    }

    public static SwapPairFindManager getInstance() {
        return ManagerHolder.manager;
    }

    private SwapPairFindManager() {
        find();
    }

    private void find() {
        List<Chain> chainList = ChainConstant.chainList();
        for (int index = 0; index < chainList.size(); index++) {
            Chain chain = chainList.get(index);
            List<Swap> swapList = chain.swapList;
            for (int j = 0; j < swapList.size(); j++) {
                SwapPairFindModel model = new SwapPairFindModel(chain, swapList.get(j), ERC20Manager.getInstance().erc20Model(chain));
                model.refreshList();
            }
        }
    }
}
