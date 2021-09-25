package com.nwld.defi.tools.constant;

import com.nwld.defi.tools.MyApp;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.Swap;
import com.nwld.defi.tools.manager.SwapPairWatchManager;
import com.nwld.defi.tools.util.SPUtil;
import com.nwld.defi.tools.util.StringUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChainConstant {
    private static List<Chain> chainList;
    private final static Map<String, Chain> chainMap = new HashMap<>();

    public static List<Chain> chainList() {
        if (null == chainList) {
            boolean isFirstWatch = false;
            String firstWatch = SPUtil.get(MyApp.getContext(), "app", "firstWatch");
            if (StringUtil.isEmpty(firstWatch)) {
                isFirstWatch = true;
                SPUtil.set(MyApp.getContext(), "app", "firstWatch", "0");
            }
            chainList = new ArrayList<>();
            Chain CELO = CELO(isFirstWatch);
            chainList.add(CELO);
            chainMap.put(CELO.symbol, CELO);

//            Chain HT = HT();
//            chainList.add(HT);
//            chainMap.put(HT.symbol, HT);
//
//            Chain OKT = OKT();
//            chainList.add(OKT);
//            chainMap.put(OKT.symbol, OKT);
//
            Chain BNB = BNB(isFirstWatch);
            chainList.add(BNB);
            chainMap.put(BNB.symbol, BNB);

//            Chain ETH = ETH(isFirstWatch);
//            chainList.add(ETH);
//            chainMap.put(ETH.symbol, ETH);
        }
        return chainList;
    }

    public static Chain chain(String symbol) {
        return chainMap.get(symbol);
    }

    private static Chain CELO(boolean isFirstWatch) {
        Chain CELO = new Chain();
        CELO.chainId = 42220;
        CELO.rpcUrl = "https://forno.celo.org";
        CELO.symbol = "CELO";
        CELO.decimals = 18;
        Swap ubeSwap = new Swap();
        ubeSwap.chain = CELO;
        ubeSwap.name = "Ubeswap";
        ubeSwap.tokenSymbol = "UBE";
        ubeSwap.swapFactoryAddress = "0x62d5b84bE28a183aBB507E125B384122D2C25fAE";
        ubeSwap.swapRouterAddress = "0xE3D8bd6Aed4F159bc8000a9cD47CffDb95F96121";
        if (isFirstWatch) {
            JSONArray ubePairList = new JSONArray();
            ubePairList.put("0x0b81cf47c8f97275d14c006e537d5101b6c87300");
            ubePairList.put("0xf97e6168283e38fc42725082fc63b47b6cd16b18");
            ubePairList.put("0xf5b1bc6c9c180b64f5711567b1d6a51a350f8422");
            ubePairList.put("0x59b22100751b7fda0c88201fb7a0eaf6fc30bcc7");
            SPUtil.set(MyApp.getContext(), SwapPairWatchManager.swapFileName(ubeSwap), IntentConstant.pairs, ubePairList.toString());
        }
        CELO.addSwap(ubeSwap);
        return CELO;
    }

    private static Chain ETH(boolean isFirstWatch) {
        Chain ETH = new Chain();
        ETH.chainId = 1;
        ETH.rpcUrl = "https://ethjeqd0430103d.swtc.top";
        ETH.symbol = "ETH";
        ETH.decimals = 18;

        Swap uniSwapV2 = new Swap();
        uniSwapV2.chain = ETH;
        uniSwapV2.name = "uniSwapV2";
        uniSwapV2.tokenSymbol = "UNI";
        uniSwapV2.swapFactoryAddress = "0x5C69bEe701ef814a2B6a3EDD4B1652CB9cc5aA6f";
        uniSwapV2.swapRouterAddress = "0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D";
        if (isFirstWatch) {
            JSONArray pairList = new JSONArray();
            pairList.put("0xf00e80f0de9aea0b33aa229a4014572777e422ee");
            SPUtil.set(MyApp.getContext(), SwapPairWatchManager.swapFileName(uniSwapV2), IntentConstant.pairs, pairList.toString());
        }
        ETH.addSwap(uniSwapV2);
        return ETH;
    }

    private static Chain BNB(boolean isFirstWatch) {
        Chain BNB = new Chain();
        BNB.chainId = 56;
        BNB.rpcUrl = "https://bsc-dataseed1.binance.org";
        BNB.symbol = "BNB";
        BNB.decimals = 18;

//        Swap pancakeSwap = new Swap();
//        pancakeSwap.chain = BNB;
//        pancakeSwap.name = "PancakeSwap";
//        pancakeSwap.tokenSymbol = "CAKE";
//        pancakeSwap.swapFactoryAddress = "0xcA143Ce32Fe78f1f7019d7d551a6402fC5350c73";
//        pancakeSwap.swapRouterAddress = "0x10ED43C718714eb63d5aA57B78B54704E256024E";
//        if (isFirstWatch) {
//            JSONArray pairList = new JSONArray();
//            pairList.put("0x16b9a82891338f9ba80e2d6970fdda79d1eb0dae");
//            SPUtil.set(MyApp.getContext(), SwapPairWatchManager.swapFileName(pancakeSwap), IntentConstant.pairs, pairList.toString());
//        }
//        BNB.addSwap(pancakeSwap);

        Swap wfcSwap = new Swap();
        wfcSwap.chain = BNB;
        wfcSwap.name = "WfcSwap";
        wfcSwap.tokenSymbol = "WFC";
        wfcSwap.swapFactoryAddress = "0xb73103b4b701700744ac2eAe47ffd5Eb77148302";
        wfcSwap.swapRouterAddress = "0xd7d1215ddbc2f6e69d7fd2d52e5096671fff46cb";
        if (isFirstWatch) {
            JSONArray wfcPairList = new JSONArray();
            wfcPairList.put("0xa658e84e01644368c9336bfe2bcf38f7c98830f9");
            wfcPairList.put("0xc2e0d6eed68632765a788006abb1980c75cb0ac7");
            SPUtil.set(MyApp.getContext(), SwapPairWatchManager.swapFileName(wfcSwap), IntentConstant.pairs, wfcPairList.toString());
        }
        BNB.addSwap(wfcSwap);
        return BNB;
    }


    private static Chain OKT() {
        Chain OKT = new Chain();
        OKT.chainId = 66;
        OKT.rpcUrl = "https://exchainrpc.okex.org";
        OKT.symbol = "OKT";
        OKT.decimals = 18;
        Swap cherrySwap = new Swap();
        cherrySwap.chain = OKT;
        cherrySwap.name = "CherrySwap";
        cherrySwap.tokenSymbol = "CHE";
        cherrySwap.swapFactoryAddress = "0x709102921812B3276A65092Fe79eDfc76c4D4AFe";
        cherrySwap.swapRouterAddress = "0x865bfde337C8aFBffF144Ff4C29f9404EBb22b15";
        OKT.addSwap(cherrySwap);
        return OKT;
    }

    private static Chain HT() {
        Chain HT = new Chain();
        HT.chainId = 128;
        HT.rpcUrl = "https://http-mainnet.hecochain.com";
        HT.symbol = "HT";
        HT.decimals = 18;
        Swap mdex = new Swap();
        mdex.chain = HT;
        mdex.name = "Mdex";
        mdex.tokenSymbol = "MDX";
        mdex.swapFactoryAddress = "0xb0b670fc1F7724119963018DB0BfA86aDb22d941";
        mdex.swapRouterAddress = "0xED7d5F38C79115ca12fe6C0041abb22F0A06C300";
        HT.addSwap(mdex);
        return HT;
    }
}
