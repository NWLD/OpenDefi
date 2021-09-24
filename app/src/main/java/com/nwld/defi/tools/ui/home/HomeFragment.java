package com.nwld.defi.tools.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.entity.SwapPair;
import com.nwld.defi.tools.manager.ERC20Manager;
import com.nwld.defi.tools.manager.SwapPairWatchManager;

import java.util.List;

public class HomeFragment extends Fragment {

    private List<SwapPair> swapPairList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        swapPairList = SwapPairWatchManager.getInstance().getSwapPairList();
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initView(root);
        return root;
    }

    @Override
    public void onDestroyView() {
        SwapPairWatchManager.getInstance().unWatchSwapPairData(listObserver);
        ERC20Manager.getInstance().unWatchERC20Map(mapObserver);
        SwapPairWatchManager.getInstance().unWatchBalanceData(balanceObserver);
        super.onDestroyView();
    }

    SwapPairAdapter adapter;
    RecyclerView recyclerView;
    Observer<Integer> listObserver;
    Observer<Integer> mapObserver;
    Observer<Integer> balanceObserver;

    public void initView(View root) {
        recyclerView = root.findViewById(R.id.home_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SwapPairAdapter(getActivity(), swapPairList);
        recyclerView.setAdapter(adapter);
        listObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                adapter.reset(SwapPairWatchManager.getInstance().getSwapPairList());
            }
        };
        SwapPairWatchManager.getInstance().watchSwapPairData(getViewLifecycleOwner(), listObserver);
        mapObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                adapter.resetMap(ERC20Manager.getInstance().getErc20Map());
            }
        };
        ERC20Manager.getInstance().watchERC20Map(getViewLifecycleOwner(), mapObserver);
        balanceObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                adapter.notifyDataSetChanged();
            }
        };
        SwapPairWatchManager.getInstance().watchBalanceData(getViewLifecycleOwner(), balanceObserver);
    }
}