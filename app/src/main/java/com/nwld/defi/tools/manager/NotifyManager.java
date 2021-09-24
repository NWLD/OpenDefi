package com.nwld.defi.tools.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.nwld.defi.tools.MyApp;
import com.nwld.defi.tools.R;
import com.nwld.defi.tools.async.MainHandler;
import com.nwld.defi.tools.constant.IntentConstant;
import com.nwld.defi.tools.entity.ERC20;
import com.nwld.defi.tools.entity.SwapPair;
import com.nwld.defi.tools.ui.SchemeActivity;
import com.nwld.defi.tools.util.CalcUtils;
import com.nwld.defi.tools.util.StringUtil;

public class NotifyManager {
    public int notifyId = 2;
    private final String CHANNEL_TRANSACTION = "transaction";

    private static class ManagerHolder {
        private static final NotifyManager manager = new NotifyManager();
    }

    public static NotifyManager getInstance() {
        return ManagerHolder.manager;
    }

    private NotifyManager() {

    }

    public void showNewPair(SwapPair swapPair, ERC20 token0, ERC20 token1) {
        MainHandler.getHandler().post(new Runnable() {
            @Override
            public void run() {
                Intent notificationIntent = new Intent(MyApp.getContext(), SchemeActivity.class);
                notificationIntent.setData(Uri.parse(IntentConstant.newSwapPair + "://" + swapPair.chain.symbol + "/" + swapPair.swap.name + "/" + swapPair.address));
                notificationIntent.putExtra(IntentConstant.NOTIFY_DATA, swapPair.toJson());
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(MyApp.getContext(), 0, notificationIntent, 0);
                String text = StringUtil.trimZero(CalcUtils.decimals(swapPair.token0InitBalance, token0.decimals)) + " " + token0.symbol;
                text = text + " + " + StringUtil.trimZero(CalcUtils.decimals(swapPair.token1InitBalance, token1.decimals)) + " " + token1.symbol;
                Notification notification = new NotificationCompat.Builder(MyApp.getContext(), CHANNEL_TRANSACTION)  //注意了这里需要一个channelId
                        .setContentTitle(swapPair.chain.symbol + " " + swapPair.swap.name + " LP")
                        .setContentText(text)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(MyApp.getContext().getResources(), R.mipmap.ic_launcher))
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setOngoing(false)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setShowWhen(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .build();
                NotificationManager manager = (NotificationManager) MyApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(notifyId++, notification);
            }
        });
    }
}
