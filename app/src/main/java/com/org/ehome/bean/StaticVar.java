package com.org.ehome.bean;

public class StaticVar {
    final public static int REFRESH_MACHINE_LIST = 1;      //刷新我的设备
    final public static int ADD_MACHINE = 2;        //添加设备
    final public static int REMOVE_MACHINE_FROM_LIST = 3;       //删除设备
    final public static int LOGIN_SUCCEED = 8;      //登录成功
    final public static int LADNDING_IN_DIFFERENT_PLACES = 5;       //异地登陆
    final public static int LOGIN_FAILED = 15;      //登录失败

    public static final int REQUEST_MACHINE_LIST = 7;       //发送查询设备请求
    public static final String FINISH_ACTIVITY = "FINISH";      //关闭activity标志
    public static final int LOAD_FROM_LOCAL_SQL = 9;        //从本地SQL数据库加载
    //    public static final int SEND_MESSAGE_FAIILED = 10;
    public static final int SEND_MESSAGE = 11;
    public static final int REFRESH_HISTROY = 12;       //刷新历史
    public static final int CLEAR_HISTROY = 13;     //清空历史
    public static final int UPDATE = 14;        //更新
    public static final int UPDATE_DOWNLOAD_PERCENT = 16;       //更新下载百分比
    public static final int COMPLETE_DOWNLOAD = 17;     //完成下载
    public static final int REFRESH_MESSAGE = 18;       //刷新消息
    public static final int RELOGIN = 19;       //重新登录
    public static final int LOGIN = 20;     //登录
    public static final int QUERY_CONFIG = 21;      //查询配置
    public static final int QUERY_CONFIG_RESULT = 22;       //查询配置结果
    public static final int SERVER_NOT_RESPONDING = 23;     //服务器无响应
    public static final int NETWORK_FAULT = 24;     //网络无响应
    public static final int REFRESH_HISTROY_FAILED = 25;
    public static final int SYNCHRONOUS_RESULT = 26;
    public static final int SYNCHRONOUS_SUCCEED = 27;
    public static final int SYNCHRONOUS_NULL = 28;
    public static final int SYNCHRONOUS_REQUEST = 29;
    public static final int DISMISS_DIALOG = 30;
    public static final int SYNCHRONOUS_RESULT_IMAGE = 31;
    public static final int OFFLINE = 32;
    public static final int ONLINE = 33;
    public static final int SYNCHRONOUS_FILED = 34;
    public static final int CLEAR_UNREAD = 35;
}
