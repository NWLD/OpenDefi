package com.nwld.defi.tools.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.nwld.defi.tools.MyApp;
import com.nwld.defi.tools.R;
import com.nwld.defi.tools.service.RunningService;
import com.nwld.defi.tools.ui.home.HomeFragment;
import com.nwld.defi.tools.ui.more.MoreDialog;
import com.nwld.defi.tools.util.FragmentUtil;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().hasMain = true;
        Intent service = new Intent(this, RunningService.class);
        startService(service);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //API>21,设置状态栏颜色透明
            getWindow().setStatusBarColor(0);
            getWindow().setNavigationBarColor(0);
        }
        setContentView(R.layout.activity_main);
        HomeFragment homeFragment = new HomeFragment();
        FragmentUtil.addFragment(this, R.id.container, homeFragment);
        View more = findViewById(R.id.more);
        more.setOnClickListener(new OneClickListener() {
            @Override
            public void onOneClick(View v) {
                MoreDialog dialog = new MoreDialog(MainActivity.this);
                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        MyApp.getInstance().hasMain = false;
        super.onDestroy();
    }
}