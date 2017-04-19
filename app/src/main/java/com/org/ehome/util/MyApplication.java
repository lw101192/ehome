package com.org.ehome.util;

import android.app.Application;
import android.content.Context;

import xm.mina.Client;

/**
 * Created by liuwei on 2017/2/20.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        init(this.getApplicationContext());
    }

    void init(Context context) {
        Client.getInstance().init(context);
    }
}
