package com.nwld.defi.tools.entity;

import java.math.BigInteger;

public class Gas {
    public BigInteger gasPrice;
    public BigInteger gasLimit;

    @Override
    public String toString() {
        return "Gas{" +
                "gasPrice=" + gasPrice +
                ", gasLimit=" + gasLimit +
                '}';
    }
}
