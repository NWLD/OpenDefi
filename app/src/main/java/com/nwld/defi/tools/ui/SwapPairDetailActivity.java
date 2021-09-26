package com.nwld.defi.tools.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.nwld.defi.tools.MyApp;
import com.nwld.defi.tools.R;
import com.nwld.defi.tools.constant.IntentConstant;
import com.nwld.defi.tools.entity.ERC20;
import com.nwld.defi.tools.entity.SwapPair;
import com.nwld.defi.tools.manager.ERC20Manager;
import com.nwld.defi.tools.manager.KeyManager;
import com.nwld.defi.tools.manager.SwapPairWatchManager;
import com.nwld.defi.tools.model.SwapRouterModel;
import com.nwld.defi.tools.ui.login.LoginDialog;
import com.nwld.defi.tools.util.CalcUtils;
import com.nwld.defi.tools.util.DateUtil;
import com.nwld.defi.tools.util.LogUtil;
import com.nwld.defi.tools.util.SPUtil;
import com.nwld.defi.tools.util.StringUtil;
import com.nwld.defi.tools.util.ToastUtil;

import org.json.JSONArray;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.cert.Certificate;
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

    LinearLayout address1layout;
    TextView symbol1Text;
    TextView address1Text;
    TextView initBalance1Text;
    TextView balance1Text;
    TextView balance1DiffText;
    SwapPair swapPair;
    View approve1View;
    View swap1View;

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

    private Activity activity;

    Observer<Integer> mapObserver;
    Observer<Integer> balanceObserver;
    Observer<Integer> keyObserver;

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
        mapObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                erc20Map = ERC20Manager.getInstance().getErc20Map();
                setItem();
            }
        };
        ERC20Manager.getInstance().watchERC20Map(this, mapObserver);
        balanceObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                setItem();
            }
        };
        SwapPairWatchManager.getInstance().watchBalanceData(this, balanceObserver);
        keyObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                if (null != data && 1 == data) {
                    getWalletAddressBalance();
                }
            }
        };
        KeyManager.getInstance().watchKeyData(this, keyObserver);
        setItem();
        getWalletAddressBalance();
    }

    private void getWalletAddressBalance() {
        walletAddress = getWalletAddress();
        if (StringUtil.isEmpty(walletAddress)) {
            return;
        }
    }

    @Override
    protected void onDestroy() {
        ERC20Manager.getInstance().unWatchERC20Map(mapObserver);
        SwapPairWatchManager.getInstance().unWatchBalanceData(balanceObserver);
        KeyManager.getInstance().unWatchKeyData(keyObserver);
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

        address1layout = itemView.findViewById(R.id.item_token1_address_layout);
        symbol1Text = itemView.findViewById(R.id.item_token1_symbol);
        address1Text = itemView.findViewById(R.id.item_token1_address);
        initBalance1Text = itemView.findViewById(R.id.item_token1_init_balance);
        balance1Text = itemView.findViewById(R.id.item_token1_balance);
        balance1DiffText = itemView.findViewById(R.id.item_token1_balance_diff);
        approve1View = itemView.findViewById(R.id.item_token1_approve);
        swap1View = itemView.findViewById(R.id.item_token1_swap);

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

        pairAddressText.setText(swapPair.address);
        pairInfoText.setText(swapPair.chain.symbol + " " + swapPair.swap.name + " LP");
        if (0 == swapPair.timeStamp) {
            addTimeText.setText("");
        } else {
            addTimeText.setText(DateUtil.getDateTime(swapPair.timeStamp));
        }

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
                    LogUtil.e("login", address);
                }
            }
        });

        swap0View.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                List<String> path = new ArrayList<>();
                path.add(swapPair.token0);
                path.add(swapPair.token1);
                SwapRouterModel model = new SwapRouterModel(swapPair.chain, swapPair.swap);
                model.getAmountsOut(BigInteger.valueOf(100000000000000000L), path);
                model.swapExactTokensForTokensSupportingFeeOnTransferTokensGas(BigInteger.valueOf(100000000000000000L),
                        BigInteger.valueOf(0L), path,
                        "0xC44F16045D94049284FE4E27ec8D46Ea4bE26560");
            }
        });

        approve1View.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {

            }
        });

        swap1View.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                List<String> path = new ArrayList<>();
                path.add(swapPair.token1);
                path.add(swapPair.token0);
                SwapRouterModel model = new SwapRouterModel(swapPair.chain, swapPair.swap);
                model.getAmountsOut(BigInteger.valueOf(100000000000000000L), path);
                model.swapExactTokensForTokensSupportingFeeOnTransferTokensGas(BigInteger.valueOf(10000000000000000L),
                        BigInteger.valueOf(0L), path,
                        "0xC44F16045D94049284FE4E27ec8D46Ea4bE26560");
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
                    } else {
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
    }

    public void setItem() {
        if (swapPair.inWatchList) {
            addWatchText.setBackgroundResource(R.drawable.base_warning_button);
            addWatchText.setText(R.string.remove_from_watch);
        } else {
            addWatchText.setBackgroundResource(R.drawable.base_button);
            addWatchText.setText(R.string.add_to_watch);
        }
        if (null != swapPair.token0) {
            address0Text.setText(swapPair.token0);
            ERC20 token0 = erc20Map.get(ERC20Manager.addressKey4Chain(swapPair.token0, swapPair.chain.symbol));
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
        if (null != swapPair.token1) {
            address1Text.setText(swapPair.token1);
            ERC20 token1 = erc20Map.get(ERC20Manager.addressKey4Chain(swapPair.token1, swapPair.chain.symbol));
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
        if (null != swapPair.token0 && null != swapPair.token1) {
            ERC20 token0 = erc20Map.get(ERC20Manager.addressKey4Chain(swapPair.token0, swapPair.chain.symbol));
            ERC20 token1 = erc20Map.get(ERC20Manager.addressKey4Chain(swapPair.token1, swapPair.chain.symbol));
            if (null != token0 && null != token1) {
                BigInteger balance0 = swapPair.token0Balance;
                BigInteger balance1 = swapPair.token1Balance;
                if (null != balance0 && null != balance1) {
                    String b0 = CalcUtils.decimals(balance0, token0.decimals);
                    String b1 = CalcUtils.decimals(balance1, token1.decimals);
                    String price0 = StringUtil.trimZero(CalcUtils.divide(b1, b0, 8, RoundingMode.DOWN));
                    String price1 = StringUtil.trimZero(CalcUtils.divide(b0, b1, 8, RoundingMode.DOWN));
                    price0Text.setText("1 " + token0.symbol + " = " + price0 + " " + token1.symbol);
                    price1Text.setText("1 " + token1.symbol + " = " + price1 + " " + token0.symbol);
                }
            }
        }
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

    @Override
    public View[] filterViews() {
        View[] views = {token0OutEdit, token1OutEdit};
        return views;
    }
}