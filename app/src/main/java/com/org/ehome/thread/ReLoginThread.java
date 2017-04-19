package com.org.ehome.thread;


import com.org.ehome.activities.MainActivity;
import com.org.ehome.bean.StaticVar;

/**
 * Created by liuwei on 2016/8/10.
 */
public class ReLoginThread extends Thread {

    static ReLoginThread reLoginThread;
    private int waiting = 0;
    private static boolean flag = false;

    public static ReLoginThread getInstance() {
        if (reLoginThread == null) {
            reLoginThread = new ReLoginThread();
        }
        return reLoginThread;
    }

    public static ReLoginThread getReLoginThread() {
        return reLoginThread;
    }

    @Override
    public void run() {
        try {
            while (flag) {
                Thread.sleep((long) waiting() * 1000L);
//                xmppManager.connect();
                if (flag)
                    MainActivity.handler.sendEmptyMessage(StaticVar.RELOGIN);
                waiting++;
                System.out.println("ReconnectionThread  run");
            }
        } catch (final InterruptedException e) {
//            xmppManager.getHandler().post(new Runnable() {
//                public void run() {
//                    xmppManager.getConnectionListener().reconnectionFailed(e);
//                }
//            });
        }
    }


    public void startReconnectionThread() {
        synchronized (reLoginThread) {
            if (reLoginThread != null && !reLoginThread.isAlive() && !flag) {
                flag = true;
                reLoginThread.start();
            }
        }
    }

    public void stopReconnectionThread() {
        flag = false;
        reLoginThread = null;
        System.out.println("stopReconnectionThread");
    }

    private int waiting() {
        if (waiting > 20) {
            return 600;
        }
        if (waiting > 13) {
            return 300;
        }
        return waiting <= 7 ? 10 : 60;
    }
}
