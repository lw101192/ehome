package com.org.ehome.util;

/**
 * Created by liuwei on 2017/2/14.
 */

public class GetIpPort {
    public native String getIpString();

    public native String getPortString();

    static {
        System.loadLibrary("IpPort");
    }

}
