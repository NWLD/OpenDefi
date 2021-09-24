package com.nwld.defi.tools.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;

import com.nwld.defi.tools.MyApp;
import com.nwld.defi.tools.constant.IntentConstant;
import com.nwld.defi.tools.service.RunningService;
import com.nwld.defi.tools.util.StringUtil;

public class SchemeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyApp.getInstance().hasMain) {
            Intent service = new Intent(this, RunningService.class);
            startService(service);
        } else {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        }
        Intent intent = getIntent();
        if (intent != null) {
            String json = intent.getStringExtra(IntentConstant.NOTIFY_DATA);
            Uri dataUri = intent.getData();
            if (null != dataUri) {
                String scheme = dataUri.getScheme();
                if (StringUtil.ignoreE(IntentConstant.newSwapPair, scheme)) {
                    Intent detail = new Intent(this, SwapPairDetailActivity.class);
                    detail.putExtra(IntentConstant.JSON_DATA, json);
                    startActivity(detail);
                }
            }
        }
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }
}
