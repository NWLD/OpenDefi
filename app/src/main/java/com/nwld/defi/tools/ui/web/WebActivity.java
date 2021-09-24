package com.nwld.defi.tools.ui.web;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.nwld.defi.tools.R;
import com.nwld.defi.tools.constant.IntentConstant;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.util.StringUtil;
import com.nwld.defi.tools.util.UrlUtil;


public class WebActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_web);
        Bundle bundle = this.getIntent().getExtras();
        url = bundle.getString(IntentConstant.WEB_URL);
        if (StringUtil.isEmpty(url)) {
            finish();
            return;
        }
        initView();
    }

    private String url;

    WebView webView;

    public void initView() {
        //Android 5.0/5.1 xml webview 系统崩溃
        webView = new WebView(this);
        RelativeLayout webLayout = findViewById(R.id.base_web_layout);
        webLayout.addView(webView);
        RelativeLayout.LayoutParams webLayoutParams = (RelativeLayout.LayoutParams) webView.getLayoutParams();
        webLayoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        webLayoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        webView.setLayoutParams(webLayoutParams);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        initWebSetting();
        webView.loadUrl(url);
    }

    private void initWebSetting() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String scheme = UrlUtil.getScheme(url);
                //放行非http的scheme
                if (!scheme.startsWith("http")) {
                    WebUtil.toBrowser(WebActivity.this, url);
                    return true;
                }
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);

        webSettings.setBlockNetworkImage(false); // 解决图片不显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setAppCachePath(this.getCacheDir().getAbsolutePath());
        // 支持缩放
        webSettings.setSupportZoom(true);
        // 支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        // 允许加载本地 html 文件/false
        webSettings.setAllowFileAccess(true);
    }

    @Override
    protected void onDestroy() {
        if (null != webView) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
