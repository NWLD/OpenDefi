package com.nwld.defi.tools.interfaces;

import com.nwld.defi.tools.entity.ChainSwap;

public interface OnSearchPair {
    void onPair(ChainSwap chainSwap,String pair);
}
