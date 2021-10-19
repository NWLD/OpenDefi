package com.nwld.defi.tools.ui.swap;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nwld.defi.tools.MyApp;
import com.nwld.defi.tools.R;
import com.nwld.defi.tools.constant.IntentConstant;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.entity.ChainSwap;
import com.nwld.defi.tools.entity.SwapPair;
import com.nwld.defi.tools.interfaces.OnSearchPair;
import com.nwld.defi.tools.manager.KeyManager;
import com.nwld.defi.tools.manager.SwapPairWatchManager;
import com.nwld.defi.tools.model.SwapPairSearchModel;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.ui.OneClickListener;
import com.nwld.defi.tools.util.KeyBoardUtils;
import com.nwld.defi.tools.util.LogUtil;
import com.nwld.defi.tools.util.PriKeyUtil;
import com.nwld.defi.tools.util.SPUtil;
import com.nwld.defi.tools.util.StringUtil;
import com.nwld.defi.tools.util.ToastUtil;
import com.nwld.defi.tools.widget.BaseDialog;

import org.json.JSONArray;
import org.web3j.crypto.Credentials;

public class SearchPairDialog extends BaseDialog {

    public SearchPairDialog(BaseActivity context) {
        super(context);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_search_pair;
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
        initLoginView();
    }

    EditText token0Edit;
    EditText token1Edit;
    private View searchButton;
    private View addButton;
    private TextView swapText;
    private TextView pairText;
    private ChainSwap chainSwap;

    public void initLoginView() {
        swapText = V(R.id.sel_swap);
        findViewById(R.id.sel_swap_layout).setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                SelectSwapDialog dialog = new SelectSwapDialog(baseActivity, new SwapAdapter.OnSwapSelect() {
                    @Override
                    public void onSwapSelect(ChainSwap swap) {
                        chainSwap = swap;
                        swapText.setText(chainSwap.chain.symbol + " " + chainSwap.swap.name + " LP");
                        setButtonStatus();
                    }
                });
                dialog.show();
            }
        });
        searchButton = V(R.id.search_pair);
        addButton = V(R.id.add_to_watch);
        token0Edit = V(R.id.token0_edit);
        token1Edit = V(R.id.token1_edit);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setButtonStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        token0Edit.addTextChangedListener(textWatcher);
        token1Edit.addTextChangedListener(textWatcher);
        pairText = V(R.id.pair);
        searchButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                hideSoftInput();
                String token0 = token0Edit.getText().toString().trim();
                String token1 = token1Edit.getText().toString().trim();
                SwapPairSearchModel model = new SwapPairSearchModel(chainSwap.chain, chainSwap.swap);
                model.searchPair(token0, token1, chainSwap, new OnSearchPair() {
                    @Override
                    public void onPair(ChainSwap swap, String pair) {
                        if (StringUtil.isEmpty(pair)) {
                            return;
                        }
                        if (chainSwap != swap) {
                            return;
                        }
                        pairText.setText(pair);
                        addButton.setEnabled(true);
                    }
                });
            }
        });
        addButton.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                SwapPair swapPair = new SwapPair();
                swapPair.chain = chainSwap.chain;
                swapPair.swap = chainSwap.swap;
                swapPair.address = pairText.getText().toString();
                try {
                    String fileName = SwapPairWatchManager.swapFileName(swapPair.swap);
                    String json = SPUtil.get(MyApp.getContext(), fileName, IntentConstant.pairs);
                    JSONArray jsonArray;
                    if (StringUtil.isEmpty(json)) {
                        jsonArray = new JSONArray();
                    } else {
                        jsonArray = new JSONArray(json);
                    }
                    //添加监测
                    jsonArray.put(swapPair.address);
                    SPUtil.set(MyApp.getContext(), fileName, IntentConstant.pairs, jsonArray.toString());
                    SwapPairWatchManager.getInstance().addSwapPair(swapPair, true);
                    pairText.setText("");
                    addButton.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setButtonStatus() {
        pairText.setText("");
        addButton.setEnabled(false);
        if (null == chainSwap) {
            searchButton.setEnabled(false);
            return;
        }
        String token0 = token0Edit.getText().toString().trim();
        String token1 = token1Edit.getText().toString().trim();
        if (StringUtil.isEmpty(token0) || 42 != token0.length() || !token0.trim().startsWith("0x")
                || StringUtil.isEmpty(token1) || 42 != token1.length() || !token1.trim().startsWith("0x")) {
            searchButton.setEnabled(false);
            return;
        }
        searchButton.setEnabled(true);
    }

    private void hideSoftInput() {
        KeyBoardUtils.hideSoftInput(getBaseActivity(), token0Edit);
        KeyBoardUtils.hideSoftInput(getBaseActivity(), token1Edit);
    }

}
