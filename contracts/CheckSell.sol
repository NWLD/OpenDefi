// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;

interface IToken {
    function balanceOf(address account) external view returns (uint256);

    function transfer(address recipient, uint256 amount) external returns (bool);

    function approve(address spender, uint256 value) external returns (bool);

    function decimals() external view returns (uint8);
}

interface ISwapRouter {
    function getAmountsOut(uint256 amountIn, address[] calldata path) external view returns (uint256[] memory amounts);

    function swapExactTokensForTokensSupportingFeeOnTransferTokens(
        uint256 amountIn,
        uint256 amountOutMin,
        address[] calldata path,
        address to,
        uint256 deadline
    ) external;
}

contract CheckSell {
    address owner;
    mapping(address => bool) public whiteList;
    constructor(){
        owner = msg.sender;
        whiteList[msg.sender] = true;
    }

    //先用path[0]购买path[1],然后再卖path[1]换回path[0]，需要提前给合约转入path[0]代币，例如WBNB,BUSD,USDT,只是检测，转入一点就行
    function checkSell(address swapRouterAddr, address[] calldata path) external {
        require(whiteList[msg.sender], "only whiteList can call");

        IToken token0 = IToken(path[0]);
        token0.approve(swapRouterAddr, token0.balanceOf(address(this)));

        ISwapRouter swapRouter = ISwapRouter(swapRouterAddr);
        //先用path[0]购买path[1]
        swapRouter.swapExactTokensForTokensSupportingFeeOnTransferTokens(token0.balanceOf(address(this)), 0, path, address(this), block.timestamp + 120);

        IToken token1 = IToken(path[1]);
        token1.approve(swapRouterAddr, token1.balanceOf(address(this)));

        //卖出path[1]换回path[0]
        address[] memory sellPath = new address[](2);
        sellPath[0] = path[1];
        sellPath[1] = path[0];
        swapRouter.swapExactTokensForTokensSupportingFeeOnTransferTokens(token1.balanceOf(address(this)), 0, sellPath, address(this), block.timestamp + 120);
        require(token0.balanceOf(address(this)) > 0, "path[0] balance must > 0");
        //只是检测，预估手续费正常就表示可以卖出，不要真的发起交易，如果发起交易则从白名单移除
        whiteList[msg.sender] = false;
    }

    function setWhiteList(address addr, bool enable) external {
        require(owner == msg.sender, "only owner can call");
        whiteList[addr] = enable;
    }

    function withdrawERC20(address erc20Address) external {
        IToken erc20 = IToken(erc20Address);
        erc20.transfer(owner, erc20.balanceOf(address(this)));
    }
}
