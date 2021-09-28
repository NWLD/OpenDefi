/*
 * Copyright 2021 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.nwld.defi.tools.web3.type;

import com.nwld.defi.tools.web3.Sign;

import org.web3j.rlp.RlpType;

import java.math.BigInteger;
import java.util.List;

public interface ITransaction {

    List<RlpType> asRlpValues(Sign.SignatureData signatureData);

    BigInteger getNonce();

    BigInteger getGasPrice();

    BigInteger getGasLimit();

    String getTo();

    BigInteger getValue();

    String getData();

    TransactionType getType();
}
