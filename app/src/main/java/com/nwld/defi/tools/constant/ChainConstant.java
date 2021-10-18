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

            Chain HT = HT();
            chainList.add(HT);
            chainMap.put(HT.symbol, HT);

            Chain OKT = OKT();
            chainList.add(OKT);
            chainMap.put(OKT.symbol, OKT);

            Chain BNB = BNB(isFirstWatch);
            chainList.add(BNB);
            chainMap.put(BNB.symbol, BNB);

            Chain ETH = ETH(isFirstWatch);
            chainList.add(ETH);
            chainMap.put(ETH.symbol, ETH);

            Chain FTM = FTM(isFirstWatch);
            chainList.add(FTM);
            chainMap.put(FTM.symbol, FTM);

            Chain MATIC = MATIC(isFirstWatch);
            chainList.add(MATIC);
            chainMap.put(MATIC.symbol, MATIC);
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
        CELO.browser = "https://explorer.celo.org";
        CELO.checkSell = "0xc70BBdb696b9fF09Edba52688F1A7b8FA1EFDF2a";

        Swap sushiSwap = new Swap();
        sushiSwap.chain = CELO;
        sushiSwap.name = "sushiSwap";
        sushiSwap.tokenSymbol = "SUSHI";
        sushiSwap.swapFactoryAddress = "0xc35DADB65012eC5796536bD9864eD8773aBc74C4";
        sushiSwap.swapRouterAddress = "0x1421bDe4B10e8dd459b3BCb598810B1337D56842";
        sushiSwap.findAll = true;
        CELO.addSwap(sushiSwap);

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
            ubePairList.put("0x3bed4abf3d1abf5e95deda3d33c23ab69cabb184");
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
        ETH.browser = "https://etherscan.io";

        Swap sushiSwap = new Swap();
        sushiSwap.chain = ETH;
        sushiSwap.name = "SushiSwap";
        sushiSwap.tokenSymbol = "SUSHI";
        sushiSwap.swapFactoryAddress = "0xC0AEe478e3658e2610c5F7A4A2E1777cE9e4f2Ac";
        sushiSwap.swapRouterAddress = "0xd9e1cE17f2641f24aE83637ab66a2cca9C378B9F";
        ETH.addSwap(sushiSwap);

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
        BNB.browser = "https://bscscan.com";
        BNB.checkSell = "0x07038b34898b56fB6edbaAb9723ED3C30F8cd910";

        Swap pancakeSwap = new Swap();
        pancakeSwap.chain = BNB;
        pancakeSwap.name = "PancakeSwap";
        pancakeSwap.tokenSymbol = "CAKE";
        pancakeSwap.swapFactoryAddress = "0xcA143Ce32Fe78f1f7019d7d551a6402fC5350c73";
        pancakeSwap.swapRouterAddress = "0x10ED43C718714eb63d5aA57B78B54704E256024E";
        if (isFirstWatch) {
            JSONArray pairList = new JSONArray();
            pairList.put("0x16b9a82891338f9ba80e2d6970fdda79d1eb0dae");
            SPUtil.set(MyApp.getContext(), SwapPairWatchManager.swapFileName(pancakeSwap), IntentConstant.pairs, pairList.toString());
        }
        BNB.addSwap(pancakeSwap);

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
        OKT.browser = "https://www.oklink.com/okexchain";
        OKT.checkSell = "0x56a6A8DDcB246C4e6e44A3590C1d5B1E98664d11";

        Swap jSwap = new Swap();
        jSwap.chain = OKT;
        jSwap.name = "JSwap";
        jSwap.tokenSymbol = "JF";
        jSwap.swapFactoryAddress = "0xd654CbF99F2907F06c88399AE123606121247D5C";
        jSwap.swapRouterAddress = "0x069A306A638ac9d3a68a6BD8BE898774C073DCb3";
        OKT.addSwap(jSwap);

        Swap cherrySwap = new Swap();
        cherrySwap.chain = OKT;
        cherrySwap.name = "CherrySwap";
        cherrySwap.tokenSymbol = "CHE";
        cherrySwap.swapFactoryAddress = "0x709102921812B3276A65092Fe79eDfc76c4D4AFe";
        cherrySwap.swapRouterAddress = "0x865bfde337C8aFBffF144Ff4C29f9404EBb22b15";
        OKT.addSwap(cherrySwap);

        Swap kSwap = new Swap();
        kSwap.chain = OKT;
        kSwap.name = "KSwap";
        kSwap.tokenSymbol = "KST";
        kSwap.swapFactoryAddress = "0x60DCD4a2406Be12dbe3Bb2AaDa12cFb762A418c1";
        kSwap.swapRouterAddress = "0xc3364A27f56b95f4bEB0742a7325D67a04D80942";
        OKT.addSwap(kSwap);
        return OKT;
    }

    private static Chain HT() {
        Chain HT = new Chain();
        HT.chainId = 128;
        HT.rpcUrl = "https://http-mainnet.hecochain.com";
        HT.symbol = "HT";
        HT.decimals = 18;
        HT.browser = "https://hecoinfo.com";
        HT.checkSell = "0x5e0985D5D3C0cd358b8A8b31110B442a150d6A85";

        Swap mdex = new Swap();
        mdex.chain = HT;
        mdex.name = "Mdex";
        mdex.tokenSymbol = "MDX";
        mdex.swapFactoryAddress = "0xb0b670fc1F7724119963018DB0BfA86aDb22d941";
        mdex.swapRouterAddress = "0xED7d5F38C79115ca12fe6C0041abb22F0A06C300";
        HT.addSwap(mdex);
        return HT;
    }

    private static Chain FTM(boolean isFirstWatch) {
        Chain FTM = new Chain();
        FTM.chainId = 250;
        FTM.rpcUrl = "https://rpc.ftm.tools";
        FTM.symbol = "FTM";
        FTM.decimals = 18;
        FTM.browser = "https://ftmscan.com";
        FTM.checkSell = "0xc70BBdb696b9fF09Edba52688F1A7b8FA1EFDF2a";

        Swap spiritSwap = new Swap();
        spiritSwap.chain = FTM;
        spiritSwap.name = "SpiritSwap";
        spiritSwap.tokenSymbol = "SPIRIT";
        spiritSwap.swapFactoryAddress = "0xEF45d134b73241eDa7703fa787148D9C9F4950b0";
        spiritSwap.swapRouterAddress = "0x16327E3FbDaCA3bcF7E38F5Af2599D2DDc33aE52";
        FTM.addSwap(spiritSwap);

        Swap spookySwap = new Swap();
        spookySwap.chain = FTM;
        spookySwap.name = "SpookySwap";
        spookySwap.tokenSymbol = "BOO";
        spookySwap.swapFactoryAddress = "0x152eE697f2E276fA89E96742e9bB9aB1F2E61bE3";
        spookySwap.swapRouterAddress = "0xF491e7B69E4244ad4002BC14e878a34207E38c29";
        FTM.addSwap(spookySwap);
        return FTM;
    }

    private static Chain MATIC(boolean isFirstWatch) {
        Chain MATIC = new Chain();
        MATIC.chainId = 137;
        MATIC.rpcUrl = "https://polygon-rpc.com";
        MATIC.symbol = "MATIC";
        MATIC.decimals = 18;
        MATIC.browser = "https://polygonscan.com";
        MATIC.checkSell = "0x216ac876cE5c7C5CBfED7E4Fb28DCAe0832611bd";

        Swap quickSwap = new Swap();
        quickSwap.chain = MATIC;
        quickSwap.name = "QuickSwap";
        quickSwap.tokenSymbol = "QUICK";
        quickSwap.swapFactoryAddress = "0x5757371414417b8C6CAad45bAeF941aBc7d3Ab32";
        quickSwap.swapRouterAddress = "0xa5E0829CaCEd8fFDD4De3c43696c57F7D7A678ff";
        MATIC.addSwap(quickSwap);
        return MATIC;
    }
}
