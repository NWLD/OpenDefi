package com.nwld.defi.tools.ui.swap;

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
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.ChainSwap;
import com.nwld.defi.tools.entity.ERC20;
import com.nwld.defi.tools.entity.SwapPair;
import com.nwld.defi.tools.manager.ERC20Manager;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.util.CalcUtils;
import com.nwld.defi.tools.util.DisplayUtil;
import com.nwld.defi.tools.util.StringUtil;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwapAdapter extends RecyclerView.Adapter {
    Activity activity;
    final List<ChainSwap> items;
    private OnSwapSelect onSwapSelect;

    public SwapAdapter(Activity activity, List<ChainSwap> items) {
        this.activity = activity;
        this.items = new ArrayList<>();
        if (null != items) {
            this.items.addAll(items);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = View.inflate(activity, R.layout.item_chain_swap, null);
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
        TextView swapText;
        ChainSwap item;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_layout);
            itemLayout = itemView.findViewById(R.id.item);
            swapText = itemView.findViewById(R.id.item_swap_info);
            itemLayout.setOnClickListener(new OneClickListener() {
                @Override
                public void onOneClick(View v) {
                    if (null != onSwapSelect) {
                        onSwapSelect.onSwapSelect(item);
                    }
                }
            });
        }

        public void setItem(ChainSwap item, int index) {
            int padding = DisplayUtil.dip2px(activity, 10);
            if (0 == index) {
                layout.setPadding(padding, padding, padding, padding);
            } else {
                layout.setPadding(padding, padding, padding, padding);
            }
            this.item = item;
            swapText.setText(item.chain.symbol + " " + item.swap.name + " LP");
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

    public interface OnSwapSelect {
        void onSwapSelect(ChainSwap swap);
    }

    public void setOnSwapSelect(OnSwapSelect onSwapSelect) {
        this.onSwapSelect = onSwapSelect;
    }
}
