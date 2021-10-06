package com.nwld.defi.tools.web3;

import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.Gas;
import com.nwld.defi.tools.entity.MyTransaction;
import com.nwld.defi.tools.util.LogUtil;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Async;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Web3Util {
    static final Credentials ALICE =
            Credentials.create(
                    "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
            );
    private final Map<String, Web3j> web3jMap = new HashMap<>();

    private static class Web3UtilHolder {
        final static Web3Util instance = new Web3Util();
    }

    public static Web3Util getInstance() {
        return Web3UtilHolder.instance;
    }

    private Web3Util() {

    }

    private synchronized Web3j getWeb3j(Chain chain) {
        Web3j web3j = web3jMap.get(chain.symbol);
        if (null == web3j) {
            web3j = Web3j.build(new HttpService(chain.rpcUrl), 3000, Async.defaultExecutorService());
            web3jMap.put(chain.symbol, web3j);
        }
        return web3j;
    }

    //手续费价格
    public BigInteger ethGasPrice(Chain chain) throws Exception {
        Web3j web3j = getWeb3j(chain);
        EthGasPrice ethGasPrice =
                web3j.ethGasPrice().sendAsync().get();
        return ethGasPrice.getGasPrice();
    }

    public BigInteger getNonce(Chain chain, String address) throws Exception {
        Web3j web3j = getWeb3j(chain);
        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();
        return ethGetTransactionCount.getTransactionCount();
    }

    //预估手续费
    public Gas ethEstimateGas(MyTransaction myTransaction)
            throws Exception {
        String encodedFunction = myTransaction.encodedFunction;
        if (null == encodedFunction) {
            encodedFunction = FunctionEncoder.encode(myTransaction.function);
            myTransaction.encodedFunction = encodedFunction;
        }
        LogUtil.e("encodedFunction", encodedFunction);
        Web3j web3j = getWeb3j(myTransaction.chain);
        EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(Transaction.createEthCallTransaction(
                myTransaction.from, myTransaction.contract, encodedFunction)).sendAsync().get();
        EthGasPrice ethGasPrice =
                web3j.ethGasPrice().sendAsync().get();
        Gas gas = new Gas();
        gas.gasPrice = ethGasPrice.getGasPrice();
        if (ethEstimateGas.hasError()) {
            throw new Exception("code=" + ethEstimateGas.getError().getCode()
                    + ",msg=" + ethEstimateGas.getError().getMessage());
        } else {
            //加20%，避免交易失败
            gas.gasLimit = ethEstimateGas.getAmountUsed().multiply(BigInteger.valueOf(120)).divide(BigInteger.valueOf(100));
        }
        myTransaction.gas = gas;
        return gas;
    }

    //发送交易，调用合约方法，写操作
    public String execute(MyTransaction myTransaction)
            throws Exception {
        BigInteger nonce = getNonce(myTransaction.chain, myTransaction.from);
        LogUtil.e("nonce", nonce.toString());
        String encodedFunction = myTransaction.encodedFunction;
        if (null == encodedFunction) {
            encodedFunction = FunctionEncoder.encode(myTransaction.function);
        }
        RawTransaction rawTransaction =
                RawTransaction.createTransaction(
                        nonce, myTransaction.gas.gasPrice, myTransaction.gas.gasLimit,
                        myTransaction.contract, encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, myTransaction.chain.chainId, myTransaction.credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        Web3j web3j = getWeb3j(myTransaction.chain);
        EthSendTransaction transactionResponse =
                web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (transactionResponse.hasError()) {
            throw new Exception("code=" + transactionResponse.getError().getCode()
                    + ",msg=" + transactionResponse.getError().getMessage());
        }
        myTransaction.hash = transactionResponse.getTransactionHash();
        return myTransaction.hash;
    }

    //读取合约数据
    public String callSmartContractFunction(Chain chain, Function function, String contractAddress)
            throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);
        Web3j web3j = getWeb3j(chain);
        org.web3j.protocol.core.methods.response.EthCall response =
                web3j.ethCall(
                        Transaction.createEthCallTransaction(
                                ALICE.getAddress(), contractAddress, encodedFunction),
                        DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();
        if (response.hasError()) {
            throw new Exception("code=" + response.getError().getCode()
                    + ",msg=" + response.getError().getMessage());
        }
        return response.getValue();
    }

    public TransactionReceipt transactionReceipt(Chain chain, String transactionHash)
            throws Exception {
        Web3j web3j = getWeb3j(chain);
        EthGetTransactionReceipt transactionReceipt =
                web3j.ethGetTransactionReceipt(transactionHash).sendAsync().get();
        if (transactionReceipt.hasError()) {
            throw new Exception("code=" + transactionReceipt.getError().getCode()
                    + ",msg=" + transactionReceipt.getError().getMessage());
        }
        return transactionReceipt.getResult();
    }
}