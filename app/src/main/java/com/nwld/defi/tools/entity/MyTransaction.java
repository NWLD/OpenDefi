package com.nwld.defi.tools.entity;

import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;

public class MyTransaction {
    public String contract;
    public Function function;
    public Chain chain;
    public String from;
    public Gas gas;
    public Credentials credentials;
    public String encodedFunction;
    public String hash;
}
