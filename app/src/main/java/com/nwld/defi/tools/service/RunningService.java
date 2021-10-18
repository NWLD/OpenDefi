package com.nwld.defi.tools.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import com.nwld.defi.tools.MyApp;
import com.nwld.defi.tools.R;
import com.nwld.defi.tools.constant.ChainConstant;
import com.nwld.defi.tools.entity.Chain;
import com.nwld.defi.tools.manager.SwapPairFindManager;
import com.nwld.defi.tools.manager.SwapPairWatchManager;
import com.nwld.defi.tools.ui.SchemeActivity;

import java.util.List;

public class RunningService extends Service implements MediaPlayer.OnCompletionListener {
    private final String CHANNEL_TRANSACTION = "transaction";
    private final String CHANNEL_REFRESH_TRANSACTION = "refreshTransaction";

    public RunningService() {

    }

    PowerManager.WakeLock wakeLock;
    MediaPlayer mMediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        //创建通知通道
        //这部分代码可以写在任何位置，只需要保证在通知弹出之前调用就可以了
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /*
             * 渠道ID可以随便定义，只要保证全局唯一性就可以。
             * 渠道名称是给用户看的，需要能够表达清楚这个渠道的用途。
             * 重要等级的不同则会决定通知的不同行为，当然这里只是初始状态下的重要等级，用户可以随时手动更改某个渠道的重要等级，App是无法干预的。
             * */
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(CHANNEL_REFRESH_TRANSACTION, CHANNEL_REFRESH_TRANSACTION, importance);
            createTransactionNotificationChannel(CHANNEL_TRANSACTION, CHANNEL_TRANSACTION, NotificationManager.IMPORTANCE_HIGH);
            List<Chain> chainList = ChainConstant.chainList();
            for (int index = 0; index < chainList.size(); index++) {
                createTransactionNotificationChannel(chainList.get(index).symbol
                        , chainList.get(index).symbol
                        , NotificationManager.IMPORTANCE_HIGH);
            }
        }
//        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                RunningService.class.getName());
//        wakeLock.acquire();
//        repeat();
//        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.none);
//        mMediaPlayer.setLooping(true);
//        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
//            mMediaPlayer.setOnCompletionListener(this);
//            mMediaPlayer.start();
//        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        return START_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
//        startForeground();
    }

    @Override
    public void onDestroy() {
//        if (null != mMediaPlayer) {
//            mMediaPlayer.stop();
//            mMediaPlayer = null;
//        }
//        if (null != wakeLock) {
//            wakeLock.release();
//        }
        super.onDestroy();
    }

    public void startForeground() {
        Intent notificationIntent = new Intent(this, SchemeActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(MyApp.getContext(), CHANNEL_REFRESH_TRANSACTION)  //注意了这里需要一个channelId
                .setContentTitle(MyApp.getContext().getResources().getString(R.string.refresh_transaction))
                .setContentText(MyApp.getContext().getResources().getString(R.string.refresh_transaction_dex))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(MyApp.getContext().getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setShowWhen(true)
                .setVibrate(new long[]{0})
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
        startForeground(1, notification);
        SwapPairWatchManager.getInstance();
        SwapPairFindManager.getInstance();
    }

    /**
     * 创建通知通道
     *
     * @param channelId
     * @param channelName
     * @param importance
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        //设置震动响铃等，高版本仅在此处设置有效，notification中设置无效
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});//设置震动：静止0ms，震动1000ms，静止1000毫秒，震动1000毫秒，需要声明权限
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);//设置是否在锁屏中显示
        NotificationManager notificationManager = (NotificationManager) MyApp.getContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createTransactionNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        //设置震动响铃等，高版本仅在此处设置有效，notification中设置无效
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400});//设置震动：静止0ms，震动1000ms，静止1000毫秒，震动1000毫秒，需要声明权限
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);//设置是否在锁屏中显示
        NotificationManager notificationManager = (NotificationManager) MyApp.getContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}