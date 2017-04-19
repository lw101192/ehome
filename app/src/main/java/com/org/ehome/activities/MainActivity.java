package com.org.ehome.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import xm.mina.ClientCallBack;
import xm.mina.RequestCallBack;


import xm.mina.Client;

import com.org.ehome.CodeScan.MipcaActivityCapture;
import com.org.ehome.R;
import com.org.ehome.bean.StaticVar;
import com.org.ehome.bean.Tab;
import com.org.ehome.fragment.HistroyFragment;
import com.org.ehome.fragment.MachineFragment;
import com.org.ehome.fragment.MachineMessageFragment;
import com.org.ehome.fragment.UserFragment;
import com.org.ehome.service.ListenerService;
import com.org.ehome.service.RemoteKeepLiveService;
import com.org.ehome.thread.ReLoginThread;
import com.org.ehome.util.DataBaseHelper;
import com.org.ehome.widget.CustomadeDialog;
import com.xm.Bean.MessageBean;
import com.xm.Bean.UserBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements MachineFragment.OnFragmentInteractionListener, UserFragment.OnFragmentInteractionListener, HistroyFragment.OnListFragmentInteractionListener {
    public static boolean log = false; // 登陆状态
    public static String USERNAME = null; // 登陆名
    public static Handler handler;
    private CustomadeDialog.Builder builder;
    private CustomadeDialog customadeDialog;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    SharedPreferences preferences;
    ProgressDialog progressDialog;
    Timer mtimer;
    public static Activity mainActivity;
    private Toolbar toolBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MyViewPagerAdapter myViewPagerAdapter;
    private List<Tab> tabs = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private TextView title;
    Intent intent;
    NotificationCompat.Builder mBuilder;
    public NotificationManager mNotificationManager;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static boolean runninguiisshowing = false;
    private Timer checktimer;
    public static int checkcount = 0;
    public static String DB_NAME;       //数据库名称
    private int notificationID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        JniUtil ji = new JniUtil();
//        ji.getIpString();

        initData();
        init();

        initSetHandlet();
        if (getIntent().getAction() != null && getIntent().getAction().equals("loginsucceed")) {
            handler.sendEmptyMessage(StaticVar.LOGIN_SUCCEED);
        }
        if (getIntent().getAction() != null && getIntent().getAction().equals("autoLogin")) {
            if (Client.getInstance().isNetworkAvailable(getApplicationContext())) {
                autoLogin();
            }
        }

        mainActivity = this;
        USERNAME = preferences.getString("username", null);
        DB_NAME = USERNAME + "_bioreactor_db";

        registerBroadCastReceiver();
//        initSqliteHelper();
//        System.out.println("mainactivity");
    }

    private void registerBroadCastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticVar.FINISH_ACTIVITY);
        registerReceiver(broadcastReceiver, filter);
    }

    private void initSqliteHelper() {

//        ContentValues values=new ContentValues();
//        values.put("id","GLP001");
//        values.put("nickname","GLP001");
//        values.put("createtime","2015-08-01");
//        values.put("lastsynchronizetime","2016-08-01");
//        sqliteDatabase.insert("mymachine",null,values);

//        Cursor cursor = sqliteDatabase.query("mymachine", new String[]{"id", "nickname", "createtime", "lastsynchronizetime"}, "id=?", new String[]{"GLP001"}, null, null, null);
    }

    private void initData() {
        Tab tab = new Tab();
        tab.setText("历史");
        tab.setIcon_normal(R.drawable.histroy_normal);
        tab.setIcon_pressed(R.drawable.histroy_pressed);
        tabs.add(tab);

        tab = new Tab();
        tab.setText("我的设备");
        tab.setIcon_normal(R.drawable.machine_normal);
        tab.setIcon_pressed(R.drawable.machine_pressed);
        tabs.add(tab);

        tab = new Tab();
        tab.setText("用户中心");
        tab.setIcon_normal(R.drawable.user_normal);
        tab.setIcon_pressed(R.drawable.user_pressed);
        tabs.add(tab);

        fragments.add(HistroyFragment.newInstance(0));
        fragments.add(MachineFragment.newInstance(null, null));
        fragments.add(UserFragment.newInstance(null, null));


    }

    private void init() {

//        WindowManager wm = getWindowManager();
//        width = wm.getDefaultDisplay().getWidth();

        preferences = getSharedPreferences("config",
                MODE_PRIVATE);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        title = (TextView) findViewById(R.id.title);

        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myViewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeTabSelected(tab);
                invalidateOptionsMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabUnselected(tab);
                invalidateOptionsMenu();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                invalidateOptionsMenu();
            }
        });

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
////                ViewHelper.setTranslationX(title, -positionOffset*width);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });

        //viewPager.setCurrentItem(1);
//        tabLayout.setScrollPosition(0,1F,true);
        tabLayout.getTabAt(1).select();     //默认选中第1个标签


        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("测试标题")
                .setContentText("测试内容")
                .setContentIntent(PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT))
//		.setNumber(number)//显示数量
//                .setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的

                .setPriority(NotificationCompat.DEFAULT_ALL)//设置该通知优先级
//		.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
//                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancelAll();
    }

    /**
     * 修改选中的tab
     *
     * @param tab
     */
    private void changeTabSelected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        ImageView tabimage = (ImageView) view.findViewById(R.id.tab_img);
        TextView textView = (TextView) view.findViewById(R.id.tab_tv);
        title.setText(textView.getText().toString());
        textView.setTextColor(getResources().getColor(R.color.tab_pressed));
        if (textView.getText().toString().equals("历史")) {
            tabimage.setImageResource(R.drawable.histroy_pressed);
            viewPager.setCurrentItem(0);
        } else if (textView.getText().toString().equals("我的设备")) {
            tabimage.setImageResource(R.drawable.machine_pressed);
            viewPager.setCurrentItem(1);
        } else {
            tabimage.setImageResource(R.drawable.user_pressed);
            viewPager.setCurrentItem(2);
        }
    }

    /**
     * 修改未选中的tab
     *
     * @param tab
     */
    private void changeTabUnselected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_img);
        TextView textView = (TextView) view.findViewById(R.id.tab_tv);
        textView.setTextColor(getResources().getColor(R.color.tab_normal));
        if (textView.getText().toString().equals("历史")) {
            imageView.setImageResource(R.drawable.histroy_normal);
            viewPager.setCurrentItem(0);
        } else if (textView.getText().toString().equals("我的设备")) {
            imageView.setImageResource(R.drawable.machine_normal);
            viewPager.setCurrentItem(1);
        } else {
            imageView.setImageResource(R.drawable.user_normal);
            viewPager.setCurrentItem(2);
        }
    }


    /**
     * 设置tab
     */
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setCustomView(getTabView(0));
        tabLayout.getTabAt(1).setCustomView(getTabView(1));
        tabLayout.getTabAt(2).setCustomView(getTabView(2));
    }


    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        TextView txt_title = (TextView) view.findViewById(R.id.tab_tv);
        txt_title.setText(tabs.get(position).getText());
        ImageView img_title = (ImageView) view.findViewById(R.id.tab_img);
        img_title.setImageResource(tabs.get(position).getIcon_normal());
        return view;
    }


    private class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            // TODO Auto-generated method stub
//            return tabs.get(position).getText();
//        }

        @Override
        public Fragment getItem(int position) {

            return fragments.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return fragments.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);      //该句会在viewpager切换到第三个页面时销毁掉第一个页面，导致返回第一个页面时会重新调用oncreateview方法
        }
    }

    /**
     * 自动登录
     */
    private void autoLogin() {

        if (preferences.getBoolean("user_autologin", false)) { // 互联网自动登陆开启
            if (preferences.getString("username", null) == null
                    || preferences.getString("password", null) == null) {
                return;
            } else {

                MessageBean messageBean = new MessageBean();
                messageBean.setAction("login");
                UserBean from = new UserBean();
                from.setType("mob");
                from.setId(preferences.getString("username", null));
                from.setOriginpw(preferences.getString("password", null));
                messageBean.setFrom(from);
                Client.getInstance().login(messageBean, new ClientCallBack() {
                    @Override
                    public void onSuccess(int var1, String var2) {
                        System.out.println("onSuccess" + var1);
                        Client.getInstance().NewUser(preferences.getString("username", null));
                        handler.sendEmptyMessage(StaticVar.LOGIN_SUCCEED);

                    }

                    @Override
                    public void onFaliure(int var1) {
                        System.out.println("onFaliure1");
                        handler.sendEmptyMessage(StaticVar.LOGIN_FAILED);
                    }

                    @Override
                    public void onProgress(int var1, String var2) {

                    }
                });

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("请稍等");
                progressDialog.setMessage("登录中...");
                progressDialog.show();
                mtimer = new Timer();
                mtimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (progressDialog != null && progressDialog.isShowing()) {
                            handler.sendEmptyMessage(StaticVar.LOGIN_FAILED);

                        }

                    }
                }, 10000);
            }
        }
    }

    private class LoginThread extends Thread {
        private String username;
        private String password;

        public LoginThread(String username, String password) {
            // TODO Auto-generated constructor stub
            this.username = username;
            this.password = password;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            MessageBean messageBean = new MessageBean();
            messageBean.setAction("login");
            UserBean from = new UserBean();
            from.setType("mob");
            from.setId(username);
            from.setOriginpw(password);
            messageBean.setFrom(from);
            Client.getInstance().login(messageBean, new ClientCallBack() {
                @Override
                public void onSuccess(int var1, String var2) {
                    System.out.println("onSuccess" + var1);
                    Client.getInstance().NewUser(username);
                    handler.sendEmptyMessage(StaticVar.LOGIN_SUCCEED);

                }

                @Override
                public void onFaliure(int var1) {
                    System.out.println("onFaliure2");
                    handler.sendEmptyMessage(StaticVar.LOGIN_FAILED);
                }

                @Override
                public void onProgress(int var1, String var2) {

                }
            });


        }
    }


    public void initSetHandlet() {
//         TODO Auto-generated method stub
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {

                    case StaticVar.LOGIN:
                        autoLogin();
                        break;
                    case StaticVar.LADNDING_IN_DIFFERENT_PLACES:
//                        try {
//                            SocketServerThread.getSocketServerThread().stopSocketServerThread();
//                        } catch (Exception e) {
//
//                        }
                        intent = new Intent();
                        intent.setClass(MainActivity.this, Login_Activity.class);
                        intent.setAction("offline");
                        startActivity(intent);

                        intent = new Intent();
                        intent.setAction(StaticVar.FINISH_ACTIVITY);
                        sendBroadcast(intent);      //发送关闭activity广播

                        break;
                    case StaticVar.DISMISS_DIALOG:
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                        break;
                    case StaticVar.LOGIN_FAILED:
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) {
                                progressDialog.setMessage("登录失败");
                                handler.sendEmptyMessageDelayed(StaticVar.DISMISS_DIALOG, 2000);


                            }
                        System.out.println("启动重连线程");
                        ReLoginThread.getInstance().startReconnectionThread();      //启动重连线程
//                        Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        //intent = new Intent(MainActivity.this, Login_Activity.class);
                        //intent.setAction("reLogin");
                        //startActivity(intent);
                        //finish();
                        break;
                    case StaticVar.LOGIN_SUCCEED:
                        startService(new Intent(MainActivity.this, ListenerService.class));
                        startService(new Intent(MainActivity.this, RemoteKeepLiveService.class));


                        if (ReLoginThread.getReLoginThread() != null) {
                            ReLoginThread.getInstance().stopReconnectionThread();
                        }
                        if (mtimer != null) {
                            mtimer.cancel();
                            mtimer = null;
                        }
//                        mtimer = new Timer();
//                        mtimer.schedule(new TimerTask() {       //开启定时器，每隔20S发送心跳包
//                            @Override
//                            public void run() {
//                                MainActivity.client.sendFlag(" ");
//                            }
//                        }, 0, 20000);
//                        if (checktimer != null) {
//                            checktimer.cancel();
//                            checktimer = null;
//                        }
//                        checktimer = new Timer();
//                        checktimer.schedule(new TimerTask() {       //开启定时器，若在100S内服务器无回应，则自动重连
//                            @Override
//                            public void run() {
//                                MainActivity.checkcount++;
//                                if (MainActivity.checkcount > 1) {
//                                    ReLoginThread reconnectionThread = ReLoginThread.getInstance();
//                                    reconnectionThread.startReconnectionThread();
//                                    checkcount=0;
//                                    checktimer.cancel();
//                                    mtimer.cancel();
//                                    checktimer = null;
//                                    mtimer = null;
//                                }
//                            }
//                        }, 0, 50000);
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) {
                                progressDialog.setMessage("登录成功");
                                handler.sendEmptyMessageDelayed(StaticVar.DISMISS_DIALOG, 2000);
                            }
//                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                        if(SocketServerThread.getSocketServerThread()!=null)
//                            SocketServerThread.getSocketServerThread().stopSocketServerThread();
//                        SocketServerThread serverThread = SocketServerThread.getInstance(MainActivity.client.Client_Socket);
//                        serverThread.startSocketServerThread();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                requestMyMachine();
//                            }
//                        },1000);
                        if (MachineFragment.handler != null)
                            MachineFragment.handler.sendEmptyMessage(StaticVar.REQUEST_MACHINE_LIST);
                        if (UserFragment.handler != null) {
                            UserFragment.handler.sendEmptyMessage(StaticVar.LOGIN_SUCCEED);
                            System.out.println("sendEmptyMessage  UserFragment  LOGIN_SUCCEED");
                        }

                        System.out.println("mainavtivity  LOGIN_SUCCEED");

                        break;
                    case StaticVar.RELOGIN:
                        LoginThread thread = new LoginThread(preferences.getString("username", null), preferences.getString("password", null));
                        thread.start();
                        break;
//                    case StaticVar.SEND_MESSAGE_FAIILED:
//                        MainActivity.handler.sendEmptyMessage(StaticVar.RELOGIN);
////                        Toast.makeText(MainActivity.this,"请求失败，检查网络",Toast.LENGTH_SHORT).show();
//                        break;
                    case StaticVar.SEND_MESSAGE:


                        Object[] object = new Object[3];
                        object = (Object[]) msg.obj;
                        JSONObject jsonObject = null;
                        if (object[2] == null || object[2].toString().equals("NULL")) {
                            Runtime_Acyivity.handler.sendEmptyMessage(StaticVar.SYNCHRONOUS_NULL);
                            break;
                        }
                        try {
                            jsonObject = new JSONObject(object[2].toString());
                            if (jsonObject != null)
                                try {
                                    switch (jsonObject.get("messagetype").toString()) {
                                        case "notice":
                                            showNotification(jsonObject.getString("noticecontent"));

                                            Map<String, Object> map = new HashMap<>();
                                            try {
                                                map.put("id", object[0].toString());
                                                map.put("nickname", query("select * from mymachine where id=?", object[0].toString(), "nickname"));
                                                map.put("createtime", simpleDateFormat.format(new Date(System.currentTimeMillis())));
                                                map.put("content", jsonObject.getString("noticecontent"));
                                                map.put("isread", false);
                                                map.put("type", "machinenote");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            insert(map);
                                            HistroyFragment.handler.sendEmptyMessage(StaticVar.REFRESH_HISTROY);
                                            if (runninguiisshowing) {
                                                MachineMessageFragment.handler.sendEmptyMessage(StaticVar.REFRESH_MESSAGE);
                                            }
                                            break;
                                        case "synchronous":
                                            if (runninguiisshowing)
                                                Message.obtain(Runtime_Acyivity.handler, StaticVar.SYNCHRONOUS_RESULT, jsonObject).sendToTarget();
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        } catch (JSONException e) {
                            Message.obtain(Runtime_Acyivity.handler, StaticVar.SYNCHRONOUS_RESULT_IMAGE, object[2]).sendToTarget();
                        }

                        break;
                    case StaticVar.NETWORK_FAULT:
                        Toast.makeText(MainActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
//

                    default:
                        break;
                }
            }
        };
    }

    private void showNotification(String contentText) {
        try {
            mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
//                    .setVibrate(new long[]{200,300,200,300,500,300,200,300})
                    .setContentTitle("核酸提取仪")
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                    .setContentText(contentText)
//					.setNumber(number)//显示数量
                    .setTicker("核酸提取仪通知来啦");//通知首次出现在通知栏，带上升动画效果的

            Notification notificationCompat = mBuilder.build();
            mNotificationManager.notify(notificationID++, notificationCompat);

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{200, 300, 200, 300, 500, 300, 200, 300}, -1);

        } catch (Exception e) {

        }
    }


    /**
     * 数据库查询
     *
     * @param sql        sql语句
     * @param id         设备ID
     * @param columnName 列名
     * @return
     */
    private String query(String sql, String id, String columnName) {
        String result = null;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this, MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery(sql, new String[]{id});
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex(columnName));
        }
        cursor.close();
        sqliteDatabase.close();
        return result;
    }

    /**
     * 插入数据
     *
     * @param map 数据源
     */
    private void insert(Map<String, Object> map) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this, MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        ContentValues values;
        values = new ContentValues();
        values.put("id", map.get("id").toString());
        values.put("nickname", map.get("nickname").toString());
        values.put("createtime", map.get("createtime").toString());
        values.put("content", map.get("content").toString());
        values.put("isread", map.get("isread").toString());
        values.put("type", map.get("type").toString());
        values.put("show", true);
        sqliteDatabase.insert("histroy", null, values);
        sqliteDatabase.close();
    }

//    public class CheckVersionThread extends Thread {
//        private String phoneno;
//        private String feedback;
//
//
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            String[] msg = new String[2];
//            ConnctionServer cs = null;
//            try {
//                MainActivity.client.sendFlag("checkMobAPPVersion");
//            } catch (Exception e) {
//                // TODO: handle exception
//                try {
//                    cs = new ConnctionServer(); // 连接服务器
//                    cs.sendFlag("checkMobAPPVersion");
//
//                } catch (Exception e2) {
//                    // TODO: handle exception
//                }
//            } finally {
//                try {
//                    String str = cs.inceptMsg();
//                    if (str.contains("1"))
//                        FeedBack_Activity.handler.obtainMessage(0, "1").sendToTarget();
//                    if (str.contains("0"))
//                        FeedBack_Activity.handler.obtainMessage(0, "0").sendToTarget();
//                } catch (Exception e) {
//
//                }
//                try {
//                    cs.StreamClose();
//                } catch (Exception e) {
//
//                }
//
//            }
//
//        }
//    }


//    private void listPackages() {
//        ArrayList<PInfo> apps = getInstalledApps(false);
//
//        final int max = apps.size();
//        for (int i = 0; i < max; i++) {
//            apps.get(i).prettyPrint();
//            item = new HashMap<String, Object>();
//
//            int aa = apps.get(i).pname.length();
//            // String
//            // bb=apps.get(i).pname.substring(apps.get(i).pname.length()-11);
//            // Log.d("mxt", bb);
//
//            if (aa > 11) {
//                Log.d("lxf", "进来了11");
//                if (apps.get(i).pname.indexOf("clock") != -1) {
//                    if (!(apps.get(i).pname.indexOf("widget") != -1)) {
//                        try {
//                            PackageInfo pInfo = getPackageManager().getPackageInfo(
//                                    apps.get(i).pname, 0);
//                            if (isSystemApp(pInfo) || isSystemUpdateApp(pInfo)) {
//                                Log.d("mxt", "是系统自带的");
//                                Log.d("mxt",
//                                        "找到了"
//                                                + apps.get(i).pname
//                                                .substring(apps.get(i).pname
//                                                        .length() - 5)
//                                                + "  全名：" + apps.get(i).pname
//                                                + " " + apps.get(i).appname);
//                                item.put("pname", apps.get(i).pname);
//                                item.put("appname", apps.get(i).appname);
//                                pagList.add(apps.get(i).pname);
//                            }
//                        } catch (Exception e) {
//                            // TODO: handle exception
//                        }
//
//                    }
//                }
//            }
//
//
//			  if(apps.get(i).pname.subSequence(apps.get(i).pname.length()-11,
//			  apps.get(i).pname.length()) != null){
//
//			  }
//
//        }
//    }
//
//    public boolean isSystemApp(PackageInfo pInfo) {
//        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
//    }
//
//    public boolean isSystemUpdateApp(PackageInfo pInfo) {
//        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
//    }
//
//    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
//        ArrayList<PInfo> res = new ArrayList<PInfo>();
//        List<PackageInfo> packs = getPackageManager()
//                .getInstalledPackages(0);
//        for (int i = 0; i < packs.size(); i++) {
//            PackageInfo p = packs.get(i);
//            if ((!getSysPackages) && (p.versionName == null)) {
//                continue;
//            }
//            PInfo newInfo = new PInfo();
//            newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
//            newInfo.pname = p.packageName;
//            newInfo.versionName = p.versionName;
//            newInfo.versionCode = p.versionCode;
//            newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
//            res.add(newInfo);
//        }
//        return res;
//    }
//
//    class PInfo {
//        private String appname = "";
//        private String pname = "";
//        private String versionName = "";
//        private int versionCode = 0;
//        private Drawable icon;
//
//        private void prettyPrint() {
//            Log.i("taskmanger", appname + "\t" + pname + "\t" + versionName
//                    + "\t" + versionCode + "\t");
//        }
//    }


    /**
     * 扫描返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (resultCode) {
            case -1:
                Bundle bundle = data.getExtras();
                final String id = bundle.getString("result");
                final String[] temp = new String[3];
                if (id.contains("唯一ID=")) {
                    builder = new CustomadeDialog.Builder(this);
                    final EditText editText = new EditText(this);
                    builder.setTitle("给设备起个名字吧！").setContentView(editText).setNegativeButton("取消", null).setPositiveButton("确认添加", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            MessageBean messageBean = new MessageBean();
                            messageBean.setAction("AddMachine");
                            UserBean from = new UserBean();
                            from.setId(Client.getInstance().getUserID());
                            messageBean.setFrom(from);
                            UserBean to = new UserBean();
                            to.setNickname(editText.getText().toString());
                            to.setId(id.replace("唯一ID=", ""));
                            messageBean.setTo(to);
                            Client.getInstance().sendRquestForResponse(messageBean, false, new RequestCallBack<MessageBean>() {
                                @Override
                                public void Response(MessageBean messageBean) {
                                    MachineFragment.handler.obtainMessage(StaticVar.ADD_MACHINE, String.valueOf(messageBean.getResult().getCode())).sendToTarget();
                                }
                            });
//                            temp[1] = editText.getText().toString();
//                            temp[0] = MainActivity.USERNAME;
//                            temp[2] = id.replace("唯一ID=", "");
//                            MainActivity.client.sendFlag("AddMachine");
//                            MainActivity.client.sendMsg(temp);

                        }
                    }).create().show();
                } else {
                    Toast.makeText(this, "这是不是我们的设备哦", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setIconsVisible(menu, true);
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    /**
     * 解决menu不显示图标问题
     *
     * @param menu
     * @param flag
     */
    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if (menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {        //动态调整toolbar的菜单选项

        switch (viewPager.getCurrentItem()) {
            case 0:
                menu.findItem(R.id.scan).setVisible(false);
                menu.findItem(R.id.add).setVisible(false);
                menu.findItem(R.id.clear).setVisible(true);
                menu.findItem(R.id.isread).setVisible(true);
                break;
            case 1:
                menu.findItem(R.id.scan).setVisible(true);
                menu.findItem(R.id.add).setVisible(true);
                menu.findItem(R.id.clear).setVisible(false);
                menu.findItem(R.id.isread).setVisible(false);
                break;
            case 2:
                menu.findItem(R.id.scan).setVisible(false);
                menu.findItem(R.id.add).setVisible(false);
                menu.findItem(R.id.clear).setVisible(false);
                menu.findItem(R.id.isread).setVisible(false);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       //实现toolbar菜单点击
        switch (item.getItemId()) {
            case R.id.clear:
                HistroyFragment.handler.sendEmptyMessage(StaticVar.CLEAR_HISTROY);
                break;
            case R.id.isread:
                HistroyFragment.handler.sendEmptyMessage(StaticVar.CLEAR_UNREAD);
                break;
            case R.id.scan:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                break;
            case R.id.add:
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View view = inflater.inflate(R.layout.layout_addmachine, null);
                builder = new CustomadeDialog.Builder(MainActivity.this);
                final EditText nickname = (EditText) view.findViewById(R.id.nickname);
                final EditText id = (EditText) view.findViewById(R.id.machineid);
                builder.setTitle("添加设备").setAutoDismiss(false).setContentView(view).setPositiveButton("添加", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (id.getText() == null) {
                            Toast.makeText(MainActivity.this, "ID不能为空！", Toast.LENGTH_SHORT).show();
                        } else {
                            String[] temp = new String[3];
                            if (TextUtils.isEmpty(nickname.getText())) {
                                temp[1] = id.getText().toString();
                            } else {
                                temp[1] = nickname.getText().toString();
                            }

                            MessageBean messageBean = new MessageBean();
                            messageBean.setAction("AddMachine");
                            UserBean from = new UserBean();
                            from.setId(Client.getInstance().getUserID());
                            messageBean.setFrom(from);
                            UserBean to = new UserBean();
                            to.setNickname(temp[1]);
                            to.setId(id.getText().toString());
                            messageBean.setTo(to);
                            Client.getInstance().sendRquestForResponse(messageBean, false, new RequestCallBack<MessageBean>() {
                                @Override
                                public void Response(MessageBean messageBean) {
                                    MachineFragment.handler.obtainMessage(StaticVar.ADD_MACHINE, String.valueOf(messageBean.getResult().getCode())).sendToTarget();
                                }
                            });

//                            temp[0] = MainActivity.USERNAME;
//                            //temp[1]=str[0].replace("设备名=", "");
//                            temp[2] = id.getText().toString();
//                            MainActivity.client.sendFlag("AddMachine");
//                            MainActivity.client.sendMsg(temp);
                            customadeDialog.dismiss();
                            customadeDialog = null;
                        }

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        customadeDialog.dismiss();
                        customadeDialog = null;
                    }
                });
                customadeDialog = builder.create();
                customadeDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        if (mtimer != null) {
            mtimer.cancel();
            mtimer = null;
        }
        if (checktimer != null) {
            checktimer.cancel();
            checktimer = null;
        }
        unregisterReceiver(broadcastReceiver);

        System.out.println("mainactivity onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            MainActivity.this.finish();
        }
    };
}
