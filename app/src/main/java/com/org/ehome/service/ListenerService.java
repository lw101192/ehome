package com.org.ehome.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.example.xm.finebiopane.IMyAidlInterface;

/**
 * Created by liuwei on 2017/3/29.
 */

public class ListenerService extends Service {


    private MyBinder binder;
    private MyServiceConnection conn;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return binder;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        if (binder == null) {
            binder = new MyBinder();
        }
        conn = new MyServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ListenerService.this.bindService(new Intent(ListenerService.this, RemoteKeepLiveService.class), conn, Context.BIND_IMPORTANT);
//
//        PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, 0);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setTicker("核酸提取仪")
//                .setContentIntent(contentIntent)
//                .setContentTitle("")
//                .setAutoCancel(true)
//                .setContentText("")
//                .setWhen( System.currentTimeMillis());
//
//        //把service设置为前台运行，避免手机系统自动杀掉改服务。
//        startForeground(startId, builder.build());
//        startForeground(startId, new Notification());
        return START_STICKY;
    }


    class MyBinder extends IMyAidlInterface.Stub {

        @Override
        public String getProcessName() throws RemoteException {
            // TODO Auto-generated method stub
            return "LocalService";
        }

    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ListenerService.this.startService(new Intent(ListenerService.this, RemoteKeepLiveService.class));
            ListenerService.this.bindService(new Intent(ListenerService.this, RemoteKeepLiveService.class), conn, Context.BIND_IMPORTANT);
        }

    }

}
