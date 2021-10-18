package com.nwld.defi.tools.ui.swap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.nwld.defi.tools.MyApp;
import com.nwld.defi.tools.R;
import com.nwld.defi.tools.constant.IntentConstant;
import com.nwld.defi.tools.entity.Balance;
import com.nwld.defi.tools.entity.ERC20;
import com.nwld.defi.tools.entity.MyTransaction;
import com.nwld.defi.tools.entity.SwapPair;
import com.nwld.defi.tools.interfaces.OnGetAmountsOut;
import com.nwld.defi.tools.interfaces.OnTransactionStatus;
import com.nwld.defi.tools.manager.BalanceManager;
import com.nwld.defi.tools.manager.ERC20Manager;
import com.nwld.defi.tools.manager.KeyManager;
import com.nwld.defi.tools.manager.SwapPairWatchManager;
import com.nwld.defi.tools.model.AllowanceModel;
import com.nwld.defi.tools.model.CheckSellModel;
import com.nwld.defi.tools.model.ERC20Model;
import com.nwld.defi.tools.model.SwapRouterModel;
import com.nwld.defi.tools.model.TransactionReceiptModel;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.ui.login.LoginDialog;
import com.nwld.defi.tools.ui.transaction.TransactionConfirmDialog;
import com.nwld.defi.tools.util.CalcUtils;
import com.nwld.defi.tools.util.DateUtil;
import com.nwld.defi.tools.util.SPUtil;
import com.nwld.defi.tools.util.StringUtil;
import com.nwld.defi.tools.util.ToastUtil;
import com.nwld.defi.tools.widget.TextChangedAction;

import org.json.JSONArray;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwapPairDetailActivity extends BaseActivity {
    Map<String, ERC20> erc20Map = new HashMap<>();

    LinearLayout pairInfoLayout;
    TextView pairInfoText;
    TextView addTimeText;
    TextView pairAddressText;

    LinearLayout address0layout;
    TextView symbol0Text;
    TextView address0Text;
    TextView initBalance0Text;
    TextView balance0Text;
    TextView balance0DiffText;
    View approve0View;
    View swap0View;
    View quickSwap0View;
    View checkSell0View;

    LinearLayout address1layout;
    TextView symbol1Text;
    TextView address1Text;
    TextView initBalance1Text;
    TextView balance1Text;
    TextView balance1DiffText;
    SwapPair swapPair;
    View approve1View;
    View swap1View;
    View quickSwap1View;
    View checkSell1View;

    TextView price0Text;
    TextView price1Text;

    TextView addWatchText;

    TextView token0MyBalanceText;
    EditText token0OutEdit;
    TextView token0OutSymbolText;
    TextView token1InText;
    TextView token1InSymbolText;

    TextView token1MyBalanceText;
    EditText token1OutEdit;
    TextView token1OutSymbolText;
    TextView token0InText;
    TextView token0InSymbolText;

    TextView walletAddressText;

    private Activity activity;

    Observer<ERC20> mapObserver;
    Observer<SwapPair> balanceObserver;
    Observer<Integer> keyObserver;
    Observer<Balance> myBalanceObserver;

    String walletAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        String key = intent.getStringExtra(IntentConstant.KEY);
        //从列表过来
        if (!StringUtil.isEmpty(key)) {
            swapPair = SwapPairWatchManager.getInstance().getSwapPair(key);
        } else {
            String json = intent.getStringExtra(IntentConstant.JSON_DATA);
            if (StringUtil.isEmpty(json)) {
                finish();
                return;
            }
            swapPair = SwapPair.fromJson(json);
            if (null == swapPair) {
                finish();
                return;
            }
            swapPair.inWatchList = false;
        }
        SwapPairWatchManager.getInstance().addSwapPair(swapPair, false);
        erc20Map = ERC20Manager.getInstance().getErc20Map();
        setContentView(R.layout.activity_swap_pair_detail);
        activity = this;
        initView(this.getWindow().getDecorView());
        mapObserver = new Observer<ERC20>() {
            @Override
            public void onChanged(ERC20 data) {
                if (null == data) {
                    return;
                }
                if (StringUtil.ignoreE(data.address, swapPair.token0)
                        || StringUtil.ignoreE(data.address, swapPair.token1)) {
                    erc20Map = ERC20Manager.getInstance().getErc20Map();
                    setSwapPairToken();
                    setItem();
                    getTokenAllowance();
                }
            }
        };
        ERC20Manager.getInstance().watchERC20Map(this, mapObserver);
        balanceObserver = new Observer<SwapPair>() {
            @Override
            public void onChanged(SwapPair data) {
                if (null == data) {
                    return;
                }
                if (StringUtil.ignoreE(data.address, swapPair.address)) {
                    setSwapPairToken();
                    setItem();
                    getWalletAddressBalance();
                }
            }
        };
        SwapPairWatchManager.getInstance().watchBalanceData(this, balanceObserver);
        keyObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                if (null != data && 1 == data) {
                    walletAddress = getWalletAddress();
                    getWalletAddressBalance();
                    getTokenAllowance();
                }
            }
        };
        KeyManager.getInstance().watchKeyData(this, keyObserver);
        myBalanceObserver = new Observer<Balance>() {
            @Override
            public void onChanged(Balance data) {
                if (null == data) {
                    return;
                }
                if (StringUtil.ignoreE(BalanceManager.balanceKey(token0Balance), BalanceManager.balanceKey(data)) ||
                        StringUtil.ignoreE(BalanceManager.balanceKey(token1Balance), BalanceManager.balanceKey(data)))
                    setItem();
            }
        };
        BalanceManager.getInstance().watchBalanceData(this, myBalanceObserver);
        walletAddress = getWalletAddress();
        setSwapPairToken();
        setItem();
        getWalletAddressBalance();
        getTokenAllowance();
    }

    Balance token0Balance;
    Balance token1Balance;

    private void getWalletAddressBalance() {
        if (StringUtil.isEmpty(walletAddress)) {
            return;
        }
        if (null == token0Balance && null != swapPair.token0) {
            token0Balance = BalanceManager.getInstance().balance(tokenBalanceKey(swapPair.token0));
            if (null == token0Balance) {
                token0Balance = new Balance();
                token0Balance.address = walletAddress;
                token0Balance.chain = swapPair.chain;
                token0Balance.erc20Address = swapPair.token0;
            }
            BalanceManager.getInstance().refreshBalance(token0Balance);
        }
        if (null == token1Balance && null != swapPair.token1) {
            token1Balance = BalanceManager.getInstance().balance(tokenBalanceKey(swapPair.token1));
            if (null == token1Balance) {
                token1Balance = new Balance();
                token1Balance.address = walletAddress;
                token1Balance.chain = swapPair.chain;
                token1Balance.erc20Address = swapPair.token1;
            }
            BalanceManager.getInstance().refreshBalance(token1Balance);
        }
    }

    private String tokenBalanceKey(String token) {
        if (null == walletAddress) {
            return null;
        }
        if (null == token) {
            return null;
        }
        return walletAddress.toLowerCase() + " @ " + token + " @ " + swapPair.chain.symbol;
    }

    @Override
    protected void onDestroy() {
        if (null != swapPair && !swapPair.inWatchList) {
            SwapPairWatchManager.getInstance().removeSwapPair(swapPair, true);
        }
        if (null != token0Balance) {
            token0Balance.stopRefresh = true;
        }
        if (null != token1Balance) {
            token1Balance.stopRefresh = true;
        }
        ERC20Manager.getInstance().unWatchERC20Map(mapObserver);
        SwapPairWatchManager.getInstance().unWatchBalanceData(balanceObserver);
        KeyManager.getInstance().unWatchKeyData(keyObserver);
        BalanceManager.getInstance().unWatchBalanceData(myBalanceObserver);
        super.onDestroy();
    }

    private void initView(View itemView) {
        pairInfoLayout = itemView.findViewById(R.id.item_pair_info_layout);
        pairInfoText = itemView.findViewById(R.id.item_pair_info);
        addTimeText = itemView.findViewById(R.id.item_pair_add_time);
        pairAddressText = itemView.findViewById(R.id.item_pair_address);

        address0layout = itemView.findViewById(R.id.item_token0_address_layout);
        symbol0Text = itemView.findViewById(R.id.item_token0_symbol);
        address0Text = itemView.findViewById(R.id.item_token0_address);
        initBalance0Text = itemView.findViewById(R.id.item_token0_init_balance);
        balance0Text = itemView.findViewById(R.id.item_token0_balance);
        balance0DiffText = itemView.findViewById(R.id.item_token0_balance_diff);
        approve0View = itemView.findViewById(R.id.item_token0_approve);
        swap0View = itemView.findViewById(R.id.item_token0_swap);
        quickSwap0View = itemView.findViewById(R.id.item_token0_quick_swap);
        checkSell0View = itemView.findViewById(R.id.item_token0_check_sell);

        address1layout = itemView.findViewById(R.id.item_token1_address_layout);
        symbol1Text = itemView.findViewById(R.id.item_token1_symbol);
        address1Text = itemView.findViewById(R.id.item_token1_address);
        initBalance1Text = itemView.findViewById(R.id.item_token1_init_balance);
        balance1Text = itemView.findViewById(R.id.item_token1_balance);
        balance1DiffText = itemView.findViewById(R.id.item_token1_balance_diff);
        approve1View = itemView.findViewById(R.id.item_token1_approve);
        swap1View = itemView.findViewById(R.id.item_token1_swap);
        quickSwap1View = itemView.findViewById(R.id.item_token1_quick_swap);
        checkSell1View = itemView.findViewById(R.id.item_token1_check_sell);

        price0Text = itemView.findViewById(R.id.item_token0_price);
        price1Text = itemView.findViewById(R.id.item_token1_price);

        addWatchText = itemView.findViewById(R.id.item_pair_add_watch);

        token0MyBalanceText = itemView.findViewById(R.id.item_token0_my_balance);
        token0OutEdit = itemView.findViewById(R.id.item_token0_out);
        token0OutSymbolText = itemView.findViewById(R.id.item_token0_out_symbol);
        token1InText = itemView.findViewById(R.id.item_token1_in);
        token1InSymbolText = itemView.findViewById(R.id.item_token1_in_symbol);

        token1MyBalanceText = itemView.findViewById(R.id.item_token1_my_balance);
        token1OutEdit = itemView.findViewById(R.id.item_token1_out);
        token1OutSymbolText = itemView.findViewById(R.id.item_token1_out_symbol);
        token0InText = itemView.findViewById(R.id.item_token0_in);
        token0InSymbolText = itemView.findViewById(R.id.item_token0_in_symbol);

        walletAddressText = itemView.findViewById(R.id.wallet_address);

        pairAddressText.setText(swapPair.address);
        pairInfoText.setText(swapPair.chain.symbol + " " + swapPair.swap.name + " LP");
        if (0 == swapPair.timeStamp) {
            addTimeText.setText("");
        } else {
            addTimeText.setText(DateUtil.getDateTime(swapPair.timeStamp));
        }

        token0MyBalanceText.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (null == token0Balance || null == token0Balance.balance) {
                    return;
                }
                token0OutEdit.setText(StringUtil.trimZero(CalcUtils.decimals(token0Balance.balance, token0.decimals, 8)));
            }
        });

        token1MyBalanceText.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (null == token1Balance || null == token1Balance.balance) {
                    return;
                }
                token1OutEdit.setText(StringUtil.trimZero(CalcUtils.decimals(token1Balance.balance, token1.decimals, 8)));
            }
        });

        findViewById(R.id.base_back_layout).setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                onBackPressed();
            }
        });

        pairInfoLayout.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                StringUtil.toClipboard(activity, swapPair.address);
                ToastUtil.showToast(activity, activity.getString(R.string.address_copied));
            }
        });

        approve0View.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                Credentials credentials = getCredentialsNullLogin();
                if (null != credentials) {
                    String address = Keys.toChecksumAddress(credentials.getAddress());
                    ERC20Model model = new ERC20Model(swapPair.chain);
                    MyTransaction transaction = model.approveTransaction(
                            swapPair.swap.swapRouterAddress, CalcUtils.pow("10", token0.decimals + 18), address, swapPair.token0
                    );
                    transaction.showValue = CalcUtils.pow("10", 18).toString() + " " + token0.symbol;
                    transaction.credentials = credentials;
                    transaction.isApproval = true;
                    TransactionConfirmDialog.show(SwapPairDetailActivity.this, transaction, new TransactionConfirmDialog.OnTransactionHash() {
                        @Override
                        public void onHash(String hash) {
                            confirmApproval(hash);
                        }
                    });
                }
            }
        });

        swap0View.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                swap0(80, 1);
            }
        });

        quickSwap0View.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                swap0(40, 3);
            }
        });

        checkSell0View.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (null == token0 || null == token1) {
                    return;
                }
                List<String> path = new ArrayList<>();
                path.add(swapPair.token1);
                path.add(swapPair.token0);
                checkSell(path, token0.symbol);
            }
        });

        approve1View.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                Credentials credentials = getCredentialsNullLogin();
                if (null != credentials) {
                    String address = Keys.toChecksumAddress(credentials.getAddress());
                    ERC20Model model = new ERC20Model(swapPair.chain);
                    MyTransaction transaction = model.approveTransaction(
                            swapPair.swap.swapRouterAddress, CalcUtils.pow("10", token1.decimals + 18), address, swapPair.token1
                    );
                    transaction.showValue = CalcUtils.pow("10", 18).toString() + " " + token1.symbol;
                    transaction.credentials = credentials;
                    transaction.isApproval = true;
                    TransactionConfirmDialog.show(SwapPairDetailActivity.this, transaction, new TransactionConfirmDialog.OnTransactionHash() {
                        @Override
                        public void onHash(String hash) {
                            confirmApproval(hash);
                        }
                    });
                }
            }
        });

        swap1View.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                swap1(80, 1);
            }
        });

        quickSwap1View.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                swap1(40, 3);
            }
        });

        checkSell1View.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (null == token0 || null == token1) {
                    return;
                }
                List<String> path = new ArrayList<>();
                path.add(swapPair.token0);
                path.add(swapPair.token1);
                checkSell(path, token1.symbol);
            }
        });

        address0layout.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (null != swapPair.token0) {
                    StringUtil.toClipboard(activity, swapPair.token0);
                    ToastUtil.showToast(activity, activity.getString(R.string.address_copied));
                }
            }
        });

        address1layout.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (null != swapPair.token1) {
                    StringUtil.toClipboard(activity, swapPair.token1);
                    ToastUtil.showToast(activity, activity.getString(R.string.address_copied));
                }
            }
        });

        addWatchText.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                try {
                    String fileName = SwapPairWatchManager.swapFileName(swapPair.swap);
                    String json = SPUtil.get(MyApp.getContext(), fileName, IntentConstant.pairs);
                    JSONArray jsonArray;
                    if (StringUtil.isEmpty(json)) {
                        jsonArray = new JSONArray();
                    } else {
                        jsonArray = new JSONArray(json);
                    }
                    //在列表里，移除
                    if (swapPair.inWatchList) {
                        SwapPairWatchManager.getInstance().removeSwapPair(swapPair, false);
                        for (int index = 0; index < jsonArray.length(); index++) {
                            if (StringUtil.ignoreE(swapPair.address, jsonArray.getString(index))) {
                                jsonArray.remove(index);
                                break;
                            }
                        }
                    } else {//添加监测
                        jsonArray.put(swapPair.address);
                        SwapPairWatchManager.getInstance().addSwapPair(swapPair, true);
                    }
                    setItem();
                    SPUtil.set(MyApp.getContext(), fileName, IntentConstant.pairs, jsonArray.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }
        });
        initToken0OutEdit();
        initToken1OutEdit();
    }

    private void checkSell(List<String> path, String symbol) {
        if (StringUtil.isEmpty(swapPair.token0) || StringUtil.isEmpty(swapPair.token1)) {
            return;
        }
        Credentials credentials = getCredentialsNullLogin();
        if (null != credentials) {
            String from = Keys.toChecksumAddress(credentials.getAddress());
            CheckSellModel model = new CheckSellModel(swapPair.chain, swapPair.swap);
            MyTransaction transaction = model.checkSellTransaction(from, path);
            transaction.showValue = getResources().getString(R.string.check_sell) + " " + symbol;
            transaction.credentials = credentials;
            transaction.justShow = true;
            TransactionConfirmDialog.show(SwapPairDetailActivity.this, transaction, null);
        }
    }

    private void swap0(int slide, int quickGas) {
        Credentials credentials = getCredentialsNullLogin();
        if (null != credentials) {
            BigInteger in = CalcUtils.pow(token0OutEdit.getText().toString(), token0.decimals);
            //默认20%滑点
            BigInteger out = CalcUtils.pow(token1InText.getText().toString(), token1.decimals)
                    .multiply(BigInteger.valueOf(slide)).divide(BigInteger.valueOf(100));
            List<String> path = new ArrayList<>();
            path.add(swapPair.token0);
            path.add(swapPair.token1);
            String address = Keys.toChecksumAddress(credentials.getAddress());
            SwapRouterModel model = new SwapRouterModel(swapPair.chain, swapPair.swap);
            MyTransaction transaction = model.swapExactTokensForTokensSupportingFeeOnTransferTokensTransaction(
                    in, out, path, address
            );
            transaction.showValue = token0OutEdit.getText().toString() + " " + token0.symbol;
            transaction.credentials = credentials;
            transaction.quickGas = quickGas;
            TransactionConfirmDialog.show(SwapPairDetailActivity.this, transaction, null);
        }
    }

    private void swap1(int slide, int quickGas) {
        Credentials credentials = getCredentialsNullLogin();
        if (null != credentials) {
            BigInteger in = CalcUtils.pow(token1OutEdit.getText().toString(), token1.decimals);
            //默认20%滑点
            BigInteger out = CalcUtils.pow(token0InText.getText().toString(), token0.decimals)
                    .multiply(BigInteger.valueOf(slide)).divide(BigInteger.valueOf(100));
            List<String> path = new ArrayList<>();
            path.add(swapPair.token1);
            path.add(swapPair.token0);
            String address = Keys.toChecksumAddress(credentials.getAddress());
            SwapRouterModel model = new SwapRouterModel(swapPair.chain, swapPair.swap);
            MyTransaction transaction = model.swapExactTokensForTokensSupportingFeeOnTransferTokensTransaction(
                    in, out, path, address
            );
            transaction.showValue = token1OutEdit.getText().toString() + " " + token1.symbol;
            transaction.credentials = credentials;
            transaction.quickGas = quickGas;
            TransactionConfirmDialog.show(SwapPairDetailActivity.this, transaction, null);
        }
    }

    private void confirmApproval(String hash) {
        showLoading();
        TransactionReceiptModel model = new TransactionReceiptModel(swapPair.chain, hash, new OnTransactionStatus() {
            @Override
            public void onStatus(int status) {
                hideLoading();
                if (0 == status) {
                    getTokenAllowance();
                }
            }
        });
        model.getTransactionStatus();
    }

    TextChangedAction token0OutEditTextChangedAction = new TextChangedAction();

    private void initToken0OutEdit() {
        token0OutEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onToken0OutEditChanged(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void onToken0OutEditChanged(boolean reset) {
        String inputText = token0OutEdit.getText().toString();
        if (reset) {
            token1InText.setText("");
        }
        if (null == token0 || null == token1) {
            return;
        }
        token0OutEditTextChangedAction.textChangedAction(inputText, new TextChangedAction.Action() {
            @Override
            public void action() {
                BigInteger input = CalcUtils.pow(inputText, token0.decimals);
                //0不计算
                if (0 == input.compareTo(BigInteger.ZERO)) {
                    return;
                }
                List<String> path = new ArrayList<>();
                path.add(swapPair.token0);
                path.add(swapPair.token1);
                SwapRouterModel model = new SwapRouterModel(swapPair.chain, swapPair.swap);
                model.getAmountsOut(input, path, new OnGetAmountsOut() {
                    @Override
                    public void onGetAmountsOut(BigInteger in, BigInteger out) {
                        if (isDestroyed()) {
                            return;
                        }
                        BigInteger editInt = CalcUtils.pow(token0OutEdit.getText().toString(), token0.decimals);
                        //输入框的值与计算的输入一样才显示计算结果
                        if (0 == editInt.compareTo(in)) {
                            token1InText.setText(CalcUtils.decimals(out, token1.decimals));
                        }
                    }
                });
            }

            @Override
            public String getText() {
                return inputText;
            }
        });
    }

    TextChangedAction token1OutEditTextChangedAction = new TextChangedAction();

    private void initToken1OutEdit() {
        token1OutEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onToken1OutEditChanged(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    ERC20 token0;
    ERC20 token1;

    private void setSwapPairToken() {
        if (null == token0 && null != swapPair.token0) {
            token0 = erc20Map.get(ERC20Manager.addressKey4Chain(swapPair.token0, swapPair.chain.symbol));
        }
        if (null == token1 && null != swapPair.token1) {
            token1 = erc20Map.get(ERC20Manager.addressKey4Chain(swapPair.token1, swapPair.chain.symbol));
        }
    }

    private void onToken1OutEditChanged(boolean reset) {
        String inputText = token1OutEdit.getText().toString();
        if (reset) {
            token0InText.setText("");
        }
        if (null == token0 || null == token1) {
            return;
        }
        token1OutEditTextChangedAction.textChangedAction(inputText, new TextChangedAction.Action() {
            @Override
            public void action() {
                BigInteger input = CalcUtils.pow(inputText, token1.decimals);
                //0不计算
                if (0 == input.compareTo(BigInteger.ZERO)) {
                    return;
                }
                List<String> path = new ArrayList<>();
                path.add(swapPair.token1);
                path.add(swapPair.token0);
                SwapRouterModel model = new SwapRouterModel(swapPair.chain, swapPair.swap);
                model.getAmountsOut(input, path, new OnGetAmountsOut() {
                    @Override
                    public void onGetAmountsOut(BigInteger in, BigInteger out) {
                        if (isDestroyed()) {
                            return;
                        }
                        BigInteger editInt = CalcUtils.pow(token1OutEdit.getText().toString(), token1.decimals);
                        //输入框的值与计算的输入一样才显示计算结果
                        if (0 == editInt.compareTo(in)) {
                            token0InText.setText(CalcUtils.decimals(out, token0.decimals));
                        }
                    }
                });
            }

            @Override
            public String getText() {
                return inputText;
            }
        });
    }

    public void setItem() {
        if (swapPair.inWatchList) {
            addWatchText.setBackgroundResource(R.drawable.base_warning_button);
            addWatchText.setText(R.string.remove_from_watch);
        } else {
            addWatchText.setBackgroundResource(R.drawable.base_button);
            addWatchText.setText(R.string.add_to_watch);
        }
        if (StringUtil.isEmpty(swapPair.chain.checkSell)) {
            checkSell0View.setVisibility(View.GONE);
            checkSell1View.setVisibility(View.GONE);
        } else {
            checkSell0View.setVisibility(View.VISIBLE);
            checkSell1View.setVisibility(View.VISIBLE);
        }
        //token0 信息
        if (null != swapPair.token0) {
            address0Text.setText(swapPair.token0);
            if (null != token0) {
                symbol0Text.setText(token0.symbol);
                token0InSymbolText.setText(token0.symbol);
                token0OutSymbolText.setText(token0.symbol);
                //初始余额
                if (null != swapPair.token0InitBalance) {
                    initBalance0Text.setText(StringUtil.trimZero(CalcUtils.decimals(swapPair.token0InitBalance, token0.decimals)));
                }
                //余额
                BigInteger balance0 = swapPair.token0Balance;
                if (null != balance0) {
                    balance0Text.setText(StringUtil.trimZero(CalcUtils.decimals(balance0, token0.decimals)));
                    //变动
                    BigInteger balance0Diff = swapPair.token0BalanceDiff;
                    if (null != balance0Diff) {
                        //设置颜色
                        int compare = balance0Diff.compareTo(BigInteger.ZERO);
                        if (0 < compare) {
                            balance0DiffText.setText("+" + StringUtil.trimZero(CalcUtils.decimals(balance0Diff, token0.decimals)));
                            balance0DiffText.setTextColor(activity.getResources().getColor(R.color.base_price_up));
                        } else if (0 > compare) {
                            balance0DiffText.setText(StringUtil.trimZero(CalcUtils.decimals(balance0Diff, token0.decimals)));
                            balance0DiffText.setTextColor(activity.getResources().getColor(R.color.base_price_down));
                        }
                    }
                }
            }
        }
        //token1信息
        if (null != swapPair.token1) {
            address1Text.setText(swapPair.token1);
            if (null != token1) {
                symbol1Text.setText(token1.symbol);
                token1InSymbolText.setText(token1.symbol);
                token1OutSymbolText.setText(token1.symbol);
                //初始余额
                if (null != swapPair.token1InitBalance) {
                    initBalance1Text.setText(StringUtil.trimZero(CalcUtils.decimals(swapPair.token1InitBalance, token1.decimals)));
                }
                //余额
                BigInteger balance1 = swapPair.token1Balance;
                if (null != balance1) {
                    balance1Text.setText(StringUtil.trimZero(CalcUtils.decimals(balance1, token1.decimals)));
                    //变动
                    BigInteger balance1Diff = swapPair.token1BalanceDiff;
                    if (null != balance1Diff) {
                        //设置颜色
                        int compare = balance1Diff.compareTo(BigInteger.ZERO);
                        if (0 < compare) {
                            balance1DiffText.setText("+" + StringUtil.trimZero(CalcUtils.decimals(balance1Diff, token1.decimals)));
                            balance1DiffText.setTextColor(activity.getResources().getColor(R.color.base_price_up));
                        } else if (0 > compare) {
                            balance1DiffText.setText(StringUtil.trimZero(CalcUtils.decimals(balance1Diff, token1.decimals)));
                            balance1DiffText.setTextColor(activity.getResources().getColor(R.color.base_price_down));
                        }
                    }
                }
            }
        }
        //价格

        if (null != token0 && null != token1) {
            BigInteger balance0 = swapPair.token0Balance;
            BigInteger balance1 = swapPair.token1Balance;
            if (null != balance0 && null != balance1) {
                String b0 = CalcUtils.decimals(balance0, token0.decimals, 8);
                String b1 = CalcUtils.decimals(balance1, token1.decimals, 8);
                String price0 = StringUtil.trimZero(CalcUtils.divide(b1, b0, 8, RoundingMode.DOWN));
                String price1 = StringUtil.trimZero(CalcUtils.divide(b0, b1, 8, RoundingMode.DOWN));
                price0Text.setText("1 " + token0.symbol + " = " + price0 + " " + token1.symbol);
                price1Text.setText("1 " + token1.symbol + " = " + price1 + " " + token0.symbol);
            }
        }
        //钱包余额
        walletAddressText.setText(walletAddress);
        if (null != token0 && null != token0Balance && null != token0Balance.balance) {
            token0MyBalanceText.setText(StringUtil.trimZero(CalcUtils.decimals(token0Balance.balance, token0.decimals)) + " " + token0.symbol);
        }
        if (null != token1 && null != token1Balance && null != token1Balance.balance) {
            token1MyBalanceText.setText(StringUtil.trimZero(CalcUtils.decimals(token1Balance.balance, token1.decimals)) + " " + token1.symbol);
        }
        //代币信息、价格变化，导致交易输出变化
        onToken0OutEditChanged(false);
        onToken1OutEditChanged(false);
    }

    private Credentials getCredentialsNullLogin() {
        Credentials credentials = KeyManager.getInstance().getCredentials();
        if (null == credentials) {
            LoginDialog loginDialog = new LoginDialog(this);
            loginDialog.show();
            return null;
        }
        return credentials;
    }

    private String getWalletAddress() {
        Credentials credentials = KeyManager.getInstance().getCredentials();
        if (null == credentials) {
            return null;
        }
        return Keys.toChecksumAddress(credentials.getAddress());
    }

    private BigInteger token0Allowance;
    private BigInteger token1Allowance;

    //获取代币授权信息
    private void getTokenAllowance() {
        if (null == walletAddress) {
            return;
        }
        if (null != swapPair.token0) {
            AllowanceModel allowanceModel = new AllowanceModel(swapPair.chain, swapPair.token0);
            allowanceModel.allowance(walletAddress, swapPair.swap.swapRouterAddress, new AllowanceModel.OnAllowance() {
                @Override
                public void onAllowance(BigInteger amount) {
                    token0Allowance = amount;
                    setApprove0ButtonStatus(BigInteger.ZERO);
                }
            });
        }
        if (null != swapPair.token1) {
            AllowanceModel allowanceModel = new AllowanceModel(swapPair.chain, swapPair.token1);
            allowanceModel.allowance(walletAddress, swapPair.swap.swapRouterAddress, new AllowanceModel.OnAllowance() {
                @Override
                public void onAllowance(BigInteger amount) {
                    token1Allowance = amount;
                    setApprove1ButtonStatus(BigInteger.ZERO);
                }
            });
        }
    }

    void setApprove0ButtonStatus(BigInteger token0Input) {
        if (null == token0Allowance
                || 0 == token0Allowance.compareTo(BigInteger.ZERO)
                || 0 > token0Allowance.compareTo(token0Input)) {
            approve0View.setVisibility(View.VISIBLE);
            swap0View.setEnabled(false);
            quickSwap0View.setVisibility(View.GONE);
        } else {
            approve0View.setVisibility(View.GONE);
            swap0View.setEnabled(true);
            quickSwap0View.setVisibility(View.VISIBLE);
        }
    }

    void setApprove1ButtonStatus(BigInteger token1Input) {
        if (null == token1Allowance
                || 0 == token1Allowance.compareTo(BigInteger.ZERO)
                || 0 > token1Allowance.compareTo(token1Input)) {
            approve1View.setVisibility(View.VISIBLE);
            swap1View.setEnabled(false);
            quickSwap1View.setVisibility(View.GONE);
        } else {
            approve1View.setVisibility(View.GONE);
            swap1View.setEnabled(true);
            quickSwap1View.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View[] filterViews() {
        View[] views = {token0OutEdit, token1OutEdit};
        return views;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != swapPair) {
            swapPair.pauseWatch = false;
        }
    }

    @Override
    protected void onPause() {
        if (null != swapPair) {
            swapPair.pauseWatch = true;
        }
        super.onPause();
    }
}