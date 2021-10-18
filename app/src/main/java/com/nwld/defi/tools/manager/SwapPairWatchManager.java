package com.nwld.defi.tools.manager;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.nwld.defi.tools.MyApp;
import com.nwld.defi.tools.async.BaseExecutor;
import com.nwld.defi.tools.async.BaseTask;
import com.nwld.defi.tools.constant.ChainConstant;
import com.nwld.defi.tools.constant.IntentConstant;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.Swap;
import com.nwld.defi.tools.entity.SwapPair;
import com.nwld.defi.tools.model.SwapPairWatchModel;
import com.nwld.defi.tools.util.SPUtil;
import com.nwld.defi.tools.util.StringUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易对监控管理器
 */
public class SwapPairWatchManager {
    private final Map<String, SwapPairWatchModel> swapPairModelMap = new HashMap<>();
    private final MutableLiveData<SwapPair> swapPairData = new MutableLiveData<>();
    private final MutableLiveData<SwapPair> balanceData = new MutableLiveData<>();
    private final List<SwapPair> swapPairList = new ArrayList<>();
    private final Map<String, SwapPair> swapPairMap = new HashMap<>();

    private static class ManagerHolder {
        private static final SwapPairWatchManager manager = new SwapPairWatchManager();
    }

    public static SwapPairWatchManager getInstance() {
        return ManagerHolder.manager;
    }

    private SwapPairWatchManager() {
        init();
    }

    public SwapPairWatchModel swapPairWatchModel(Chain chain) {
        SwapPairWatchModel model = swapPairModelMap.get(chain.symbol);
        if (null == model) {
            model = new SwapPairWatchModel(chain, ERC20Manager.getInstance().erc20Model(chain));
            swapPairModelMap.put(chain.symbol, model);
        }
        return model;
    }

    private void init() {
        List<Chain> chainList = ChainConstant.chainList();
        for (int index = 0; index < chainList.size(); index++) {
            Chain chain = chainList.get(index);
            SwapPairWatchModel model = swapPairWatchModel(chain);
            List<Swap> swapList = chain.swapList;
            for (int j = 0; j < swapList.size(); j++) {
                startWatchSwap(swapList.get(j));
            }
        }
    }

    private void startWatchSwap(Swap swap) {
        BaseExecutor.getInstance().execute(new BaseTask() {
            @Override
            public void run() {
                try {
                    String fileName = swapFileName(swap);
                    String json = SPUtil.get(MyApp.getContext(), fileName, IntentConstant.pairs);
                    if (StringUtil.isEmpty(json)) {
                        return;
                    }
                    JSONArray jsonArray = new JSONArray(json);
                    for (int index = 0; index < jsonArray.length(); index++) {
                        SwapPair swapPair = new SwapPair();
                        swapPair.address = jsonArray.getString(index);
                        swapPair.swap = swap;
                        swapPair.chain = swap.chain;
                        addSwapPair(swapPair, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }
        });
    }

    public static String swapFileName(Swap swap) {
        return swap.chain.symbol.toLowerCase() + "#" + swap.name.toLowerCase() + "#" + swap.tokenSymbol.toLowerCase();
    }

    public void watchSwapPairData(LifecycleOwner lifecycleOwner, Observer<SwapPair> observer) {
        if (null == lifecycleOwner || null == observer) {
            return;
        }
        swapPairData.observe(lifecycleOwner, observer);
    }

    public void unWatchSwapPairData(Observer<SwapPair> observer) {
        if (null == observer) {
            return;
        }
        swapPairData.removeObserver(observer);
    }

    public void addSwapPair(SwapPair swapPair, boolean add2WatchList) {
        synchronized (swapPairMap) {
            if (null == swapPairMap.get(swapPair.key())) {
                swapPairMap.put(swapPair.key(), swapPair);
                swapPairWatchModel(swapPair.chain).getPairInfo(swapPair);
            }
            if (add2WatchList) {
                if (swapPairMap.get(swapPair.key()).inWatchList) {
                    return;
                }
                swapPair.inWatchList = true;
                synchronized (swapPairList) {
                    swapPairList.add(0, swapPair);
                }
                swapPairData.postValue(swapPair);
            }
        }
    }

    public SwapPair getSwapPair(String key) {
        SwapPair swapPair;
        synchronized (swapPairMap) {
            swapPair = swapPairMap.get(key);
        }
        return swapPair;
    }

    public void removeSwapPair(SwapPair swapPair, boolean unWatch) {
        swapPair.inWatchList = false;
        boolean success;
        synchronized (swapPairList) {
            success = swapPairList.remove(swapPair);
        }
        if (success) {
            swapPairData.postValue(swapPair);
        }
        //不检测了，其实详情页只是从列表删了
        if (unWatch) {
            swapPair.stopWatch = true;
            synchronized (swapPairMap) {
                swapPairMap.remove(swapPair.key());
            }
        }
    }

    public List<SwapPair> getSwapPairList() {
        List<SwapPair> list = new ArrayList<>();
        synchronized (swapPairList) {
            list.addAll(swapPairList);
        }
        return list;
    }

    public void watchBalanceData(LifecycleOwner lifecycleOwner, Observer<SwapPair> observer) {
        if (null == lifecycleOwner || null == observer) {
            return;
        }
        balanceData.observe(lifecycleOwner, observer);
    }

    public void unWatchBalanceData(Observer<SwapPair> observer) {
        if (null == observer) {
            return;
        }
        balanceData.removeObserver(observer);
    }

    public void postBalance(SwapPair swapPair) {
        balanceData.postValue(swapPair);
    }
}
