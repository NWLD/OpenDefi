package com.nwld.defi.tools.ui.web;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.nwld.defi.tools.constant.IntentConstant;
import com.nwld.defi.tools.ui.BaseActivity;
import com.nwld.defi.tools.util.StringUtil;


/**
 * Created by denghaofa
 * on 2019-06-21 18:21
 */
public class WebUtil {

    public static void toWeb(BaseActivity baseActivity, String url) {
        if (StringUtil.isEmpty(url)) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(IntentConstant.WEB_URL, url);
    }

    public static void toBrowser(Activity activity, String url) {
        if (StringUtil.isEmpty(url)) {
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
