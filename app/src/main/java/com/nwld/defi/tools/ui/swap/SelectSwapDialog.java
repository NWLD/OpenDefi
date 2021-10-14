package com.nwld.defi.tools.ui.swap;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.constant.ChainConstant;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.ChainSwap;
import com.nwld.defi.tools.entity.Swap;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.widget.BaseDialog;

import java.util.ArrayList;
import java.util.List;

public class SelectSwapDialog extends BaseDialog {
    private SwapAdapter.OnSwapSelect onSwapSelect;

    public SelectSwapDialog(BaseActivity context, SwapAdapter.OnSwapSelect onSwapSelect) {
        super(context);
        this.onSwapSelect = onSwapSelect;
    }

    @Override
    public int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_select_swap;
    }

    @Override
    public double getWidthRatio() {
        return 1;
    }

    private void setContentHeight() {
        View content = V(R.id.dialog_container);
        LinearLayout.LayoutParams contentLayoutParams = (LinearLayout.LayoutParams) content.getLayoutParams();
        contentLayoutParams.height = baseActivity.heightPixels * 80 / 100;
        content.setLayoutParams(contentLayoutParams);
    }


    public void initView() {
        setContentHeight();
        View dismissView = V(R.id.dialog_dismiss);
        dismissView.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                hideThisDialog();
            }
        });

        View closeView = V(R.id.dialog_back_layout);
        closeView.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                hideThisDialog();
            }
        });
        initSwapView();
    }


    public void initSwapView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<ChainSwap> swaps = new ArrayList<>();
        List<Chain> chainList = ChainConstant.chainList();
        for (int index = 0; index < chainList.size(); index++) {
            Chain chain = chainList.get(index);
            List<Swap> swapList = chain.swapList;
            for (int j = 0; j < swapList.size(); j++) {
                swaps.add(new ChainSwap(chain, swapList.get(j)));
            }
        }
        SwapAdapter adapter = new SwapAdapter(baseActivity, swaps);
        adapter.setOnSwapSelect(new SwapAdapter.OnSwapSelect() {
            @Override
            public void onSwapSelect(ChainSwap swap) {
                hideThisDialog();
                onSwapSelect.onSwapSelect(swap);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
