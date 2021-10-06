package com.nwld.defi.tools.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.constant.IntentConstant;
import com.nwld.defi.tools.entity.ERC20;
import com.nwld.defi.tools.entity.SwapPair;
import com.nwld.defi.tools.manager.ERC20Manager;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.ui.swap.SwapPairDetailActivity;
import com.nwld.defi.tools.util.CalcUtils;
import com.nwld.defi.tools.util.DisplayUtil;
import com.nwld.defi.tools.util.StringUtil;

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
        LinearLayout itemLayout;
        TextView pairInfoText;

        TextView symbol0Text;
        TextView balance0Text;
        TextView balance0DiffText;

        TextView symbol1Text;
        TextView balance1Text;
        TextView balance1DiffText;
        SwapPair swapPair;

        TextView price0Text;
        TextView price1Text;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_layout);
            itemLayout = itemView.findViewById(R.id.item);
            pairInfoText = itemView.findViewById(R.id.item_pair_info);

            symbol0Text = itemView.findViewById(R.id.item_token0_symbol);
            balance0Text = itemView.findViewById(R.id.item_token0_balance);
            balance0DiffText = itemView.findViewById(R.id.item_token0_balance_diff);

            symbol1Text = itemView.findViewById(R.id.item_token1_symbol);
            balance1Text = itemView.findViewById(R.id.item_token1_balance);
            balance1DiffText = itemView.findViewById(R.id.item_token1_balance_diff);

            price0Text = itemView.findViewById(R.id.item_token0_price);
            price1Text = itemView.findViewById(R.id.item_token1_price);

            itemLayout.setOnClickListener(new OneClickListener() {
                @Override
                public void onOneClick(View v) {
                    Intent detail = new Intent(activity, SwapPairDetailActivity.class);
                    detail.putExtra(IntentConstant.KEY, swapPair.key());
                    activity.startActivity(detail);
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

            pairInfoText.setText(swapPair.chain.symbol + " " + swapPair.swap.name + " LP");

            symbol0Text.setText("");
            balance0Text.setText("");
            balance0DiffText.setText("");
            symbol1Text.setText("");
            balance1Text.setText("");
            balance1DiffText.setText("");

            price0Text.setText("");
            price1Text.setText("");

            if (null != swapPair.token0) {
                ERC20 token0 = erc20Map.get(ERC20Manager.addressKey4Chain(swapPair.token0, swapPair.chain.symbol));
                if (null != token0) {
                    symbol0Text.setText(token0.symbol);
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
                ERC20 token1 = erc20Map.get(ERC20Manager.addressKey4Chain(swapPair.token1, swapPair.chain.symbol));
                if (null != token1) {
                    symbol1Text.setText(token1.symbol);
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

    public void onResume() {
        int size = items.size();
        for (int index = 0; index < size; index++) {
            items.get(index).pauseWatch = false;
        }
    }

    public void onPause() {
        int size = items.size();
        for (int index = 0; index < size; index++) {
            items.get(index).pauseWatch = true;
        }
    }

}
