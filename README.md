# OpenDefi
OpenDefi Android web3j
# 检测Dex新增交易对，目前支持以下链，以主链币符号为开头
1、BNB pancake Swap
2、HT MdexSwap
3、OKT KSap、CherrySwap、JSwap
4、CELO UbeSwap、SushiSwap
# 支持导入私钥或者创建新地址进行交易，当然必须有相应的资产，并且有主链币作为手续费
1、兑换内置20%滑点
2、抢兑内置60%滑点，3倍手续费，这个是为了抢购代币的
# 检测当前代币能否卖出，需要添加白名单
只是初步检测到当前时刻，该代币能否卖出
代币能卖出并不表示就安全，开发者还有可能在某个时刻关闭其他人的卖出操作，或者增发更多代币砸盘，还需要查看合约代码。
