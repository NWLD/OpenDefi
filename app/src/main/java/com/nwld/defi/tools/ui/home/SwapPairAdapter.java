package com.nwld.defi.tools.ui.home;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.entity.ERC20;
import com.nwld.defi.tools.entity.SwapPair;
import com.nwld.defi.tools.manager.ERC20Manager;
import com.nwld.defi.tools.model.SwapRouterModel;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.util.CalcUtils;
import com.nwld.defi.tools.util.DateUtil;
import com.nwld.defi.tools.util.DisplayUtil;
import com.nwld.defi.tools.util.StringUtil;
import com.nwld.defi.tools.util.ToastUtil;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwapPairAdapter extends RecyclerView.Adapter {
    Activity activity;
    final List<SwapPair> items;
    final Map<String, ERC20> erc20Map = new HashMap<>();

    public SwapPairAdapter(Activity activity, List<SwapPair> items) {
        erc20Map.putAll(ERC20Manager.getInstance().getErc20Map());
        this.activity = activity;
        this.items = new ArrayList<>();
        if (null != items) {
            this.items.addAll(items);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = View.inflate(activity, R.layout.item_swap_pair, null);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ItemHolder itemHolder = (ItemHolder) viewHolder;
        itemHolder.setItem(items.get(i), i);
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
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

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_layout);
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
        }

        public void setItem(SwapPair swapPair, int index) {
            int padding = DisplayUtil.dip2px(activity, 10);
            if (0 == index) {
                layout.setPadding(padding, padding, padding, padding);
            } else {
                layout.setPadding(padding, padding, padding, padding);
            }
            this.swapPair = swapPair;

            pairAddressText.setText(swapPair.address);
            pairInfoText.setText(swapPair.chain.symbol + " " + swapPair.swap.name + " LP");
            if (0 == swapPair.timeStamp) {
                addTimeText.setText("");
            } else {
                addTimeText.setText(DateUtil.getDateTime(swapPair.timeStamp));
            }

            symbol0Text.setText("");
            address0Text.setText("");
            initBalance0Text.setText("");
            balance0Text.setText("");
            balance0DiffText.setText("");
            symbol1Text.setText("");
            address1Text.setText("");
            initBalance1Text.setText("");
            balance1Text.setText("");
            balance1DiffText.setText("");

            price0Text.setText("");
            price1Text.setText("");

            if (null != swapPair.token0) {
                address0Text.setText(swapPair.token0);
                ERC20 token0 = erc20Map.get(ERC20Manager.addressKey4Chain(swapPair.token0, swapPair.chain.symbol));
                if (null != token0) {
                    symbol0Text.setText(token0.symbol);
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
    }

    void reset(List<SwapPair> items) {
        if (null != items) {
            this.items.clear();
            this.items.addAll(items);
            notifyDataSetChanged();
        }
    }

    void resetMap(Map<String, ERC20> erc20Map) {
        if (null != erc20Map) {
            this.erc20Map.clear();
            this.erc20Map.putAll(erc20Map);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
