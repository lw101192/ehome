package com.org.ehome.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import xm.mina.RequestCallBack;


import xm.mina.Client;

import com.org.ehome.R;
import com.org.ehome.bean.StaticVar;
import com.org.ehome.bean.Tab;
import com.org.ehome.fragment.MachineInfoFragment;
import com.org.ehome.fragment.MachineMessageFragment;
import com.org.ehome.fragment.MachineStatusFragment;
import com.org.ehome.util.DataBaseHelper;
import com.org.ehome.util.Util;
import com.org.ehome.widget.CustomadeDialog;
import com.xm.Bean.ContentBean;
import com.xm.Bean.MessageBean;
import com.xm.Bean.UserBean;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Runtime_Acyivity extends AppCompatActivity implements MachineInfoFragment.OnFragmentInteractionListener, MachineMessageFragment.OnFragmentInteractionListener, MachineStatusFragment.OnFragmentInteractionListener {

    Intent intent;
    public static Handler handler = null;
    Timer timer = null;


    ProgressDialog progressDialog;
    private String toID;            //目标设备ID

    //    NotificationCompat.Builder mBuilder;
//    public NotificationManager mNotificastionManager;
    String start_stop = "启动";

    private Toolbar toolbar;
    private TextView title;
    private CheckBox cb_push;

    private RecyclerView.LayoutManager lm;
    private Toolbar toolBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> fragments = new ArrayList<>();
    private MyViewPagerAdapter myViewPagerAdapter;
    private List<Tab> tabs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runtime__acyivity);
        initData();
        init();
        initHandler();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case StaticVar.SYNCHRONOUS_SUCCEED:

                        if (timer != null)
                            timer.cancel();

                        handler.sendEmptyMessage(3);
                        if (progressDialog != null) {
//                            progressDialog.setMessage("同步成功");
//                            Message message = new Message();
//                            message.what = 3;
//                            handler.sendMessageDelayed(message, 2000);
                            String s = "";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            s = simpleDateFormat.format(new Date(System.currentTimeMillis()));

                            DataBaseHelper dataBaseHelper = new DataBaseHelper(Runtime_Acyivity.this, MainActivity.DB_NAME);
                            SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
                            ContentValues values;
                            values = new ContentValues();
                            values.put("lastsynchronizetime", s);
                            sqliteDatabase.update("mymachine", values, "id=?", new String[]{toID});
                            sqliteDatabase.close();
                        }

                        break;
                    case StaticVar.SYNCHRONOUS_FILED:

                        if (timer != null)
                            timer.cancel();

                        handler.sendEmptyMessage(3);
//                        if (progressDialog != null) {
//                            progressDialog.setMessage("同步失败");
//                            Message message = new Message();
//                            message.what = 3;
//                            handler.sendMessageDelayed(message, 2000);
//
//                        }

                        break;

                    case 2:
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        if (timer != null)
                            timer.cancel();
                        Toast.makeText(Runtime_Acyivity.this, "目标已下线", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        if (timer != null)
                            timer.cancel();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
//                        MachineInfoFragment.handler.sendEmptyMessage(StaticVar.SYNCHRONOUS_SUCCEED);
                        break;
                    case 4:

                        Toast.makeText(Runtime_Acyivity.this, "消息发送成功", Toast.LENGTH_SHORT).show();
                        break;

                    case StaticVar.QUERY_CONFIG:
                        if (msg.obj.toString().equals("是"))
                            cb_push.setChecked(true);
                        break;
                    case StaticVar.QUERY_CONFIG_RESULT:
                        Toast.makeText(Runtime_Acyivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case StaticVar.SYNCHRONOUS_NULL:
                        start_stop = "启动";
                        if (progressDialog != null)
                            progressDialog.setMessage("同步成功");
                        handler.sendEmptyMessageDelayed(StaticVar.SYNCHRONOUS_SUCCEED, 1000);
                        MachineStatusFragment.handler.sendEmptyMessage(StaticVar.SYNCHRONOUS_NULL);
                        break;
//                    case 4:
//                        mBuilder.setContentTitle("生物反应器")
//                                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
//                                .setContentText(plantname.getText().toString() + "的仪器已完成所有循环")
////						.setNumber(number)//显示数量
//                                .setTicker("生物反应器通知来啦");//通知首次出现在通知栏，带上升动画效果的
//                        mNotificationManager.notify(100, mBuilder.build());
//                        break;


                    case StaticVar.SYNCHRONOUS_RESULT:
                        if (msg.obj != null) {
                            handler.sendEmptyMessage(StaticVar.SYNCHRONOUS_SUCCEED);
                            Message.obtain(MachineStatusFragment.handler, StaticVar.SYNCHRONOUS_RESULT, msg.obj).sendToTarget();


                            try {
                                JSONObject object = (JSONObject) msg.obj;
                                if (object.getString("runningstate").equals("运行")) {
                                    start_stop = "停止";
                                } else {
                                    start_stop = "启动";
                                }

                                invalidateOptionsMenu();
                            } catch (Exception e) {
                                start_stop = "启动";
                            }
                        }
                        break;
                    case StaticVar.SYNCHRONOUS_RESULT_IMAGE:
//                        System.out.println("msg.obj.length>>>"+msg.obj.toString().length());

                        if (msg.obj.toString().length() > 0) {
                            if (progressDialog != null) {
                                progressDialog.setMessage("同步成功");
                                handler.sendEmptyMessageDelayed(StaticVar.SYNCHRONOUS_SUCCEED, 1000);
                                Message.obtain(MachineStatusFragment.handler, StaticVar.SYNCHRONOUS_RESULT_IMAGE, msg.obj).sendToTarget();
                            }
                        } else {
                            if (progressDialog != null) {
                                progressDialog.setMessage("同步失败");
                                handler.sendEmptyMessageDelayed(StaticVar.SYNCHRONOUS_FILED, 1000);
                            }
                        }

                        break;
                    case StaticVar.SYNCHRONOUS_REQUEST:
                        synchornize();
                        break;
                }

            }
        };

    }


    private void initData() {
        Tab tab = new Tab();
        tab.setText("消息");
        tab.setIcon_normal(R.drawable.ic_message_normal);
        tab.setIcon_pressed(R.drawable.ic_message_pressed);
        tabs.add(tab);

        tab = new Tab();
        tab.setText("状态");
        tab.setIcon_normal(R.drawable.ic_status_normal);
        tab.setIcon_pressed(R.drawable.ic_status_pressed);
        tabs.add(tab);

        tab = new Tab();
        tab.setText("信息");
        tab.setIcon_normal(R.drawable.ic_information_normal);
        tab.setIcon_pressed(R.drawable.ic_information_pressed);
        tabs.add(tab);


        fragments.add(MachineMessageFragment.newInstance(getIntent().getStringExtra("toID"), getIntent().getStringExtra("nicknname")));
        fragments.add(MachineStatusFragment.newInstance(getIntent().getStringExtra("toID"), getIntent().getStringExtra("nicknname")));
        fragments.add(MachineInfoFragment.newInstance(getIntent().getStringExtra("toID"), getIntent().getStringExtra("nicknname")));


    }

    private void init() {
        intent = getIntent();

        toID = intent.getStringExtra("toID");
//        WindowManager wm = getWindowManager();
//        width = wm.getDefaultDisplay().getWidth();

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

//
        tabLayout.getTabAt(1).select();     //默认选中第1个标签

        initToolBar();

//        try{
//            if(intent.getStringExtra("from").equals("histroy"));
//            tabLayout.getTabAt(0).select();     //默认选中第1个标签
//        }catch (Exception e){}

    }


    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title.setText(intent.getStringExtra("nicknname"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_in_from_left, R.anim.back_out_to_right);
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
        if (textView.getText().toString().equals("消息")) {
            tabimage.setImageResource(R.drawable.ic_message_pressed);
            viewPager.setCurrentItem(0);
        } else if (textView.getText().toString().equals("状态")) {
            tabimage.setImageResource(R.drawable.ic_status_pressed);
            viewPager.setCurrentItem(1);
        } else {
            tabimage.setImageResource(R.drawable.ic_information_pressed);
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
        if (textView.getText().toString().equals("消息")) {
            imageView.setImageResource(R.drawable.ic_message_normal);
            viewPager.setCurrentItem(0);
        } else if (textView.getText().toString().equals("状态")) {
            imageView.setImageResource(R.drawable.ic_status_normal);
            viewPager.setCurrentItem(1);
        } else {
            imageView.setImageResource(R.drawable.ic_information_normal);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

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

    private void synchornize() {
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (progressDialog != null) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            progressDialog.setMessage("同步失败，请检查网络");
                            Message msg = new Message();
                            msg.what = 3;
                            handler.sendMessageDelayed(msg, 2000);
//                                    try {
//                                        Thread.sleep(2000);
//                                        progressDialog.dismiss();
//                                    } catch (InterruptedException e) {
//                                        // TODO Auto-generated catch block
//                                        e.printStackTrace();
//                                    }
                        }
                    });

                }
            }
        }, 10000);
        progressDialog = new ProgressDialog(Runtime_Acyivity.this);
        progressDialog.setMessage("同步中...");
        progressDialog.show();
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (timer != null)
                    timer.cancel();
            }
        });

        StringBuffer sb = new StringBuffer();
        sb.append("RE:SynchronousRequest&&");
//        sendMsg("RE:SynchronousRequest\r\n");

        if (Util.getConfig(this, "statusmode", "paramsmode").equals("paramsmode")) {
//            sendMsg("paramsmode\r\n");
            sb.append("paramsmode&&");
        } else {
//            sendMsg("imagemode\r\n");
            sb.append("imagemode&&");
        }

        if (Util.getConfig(this, "quality", "SD").equals("SD")) {
//            sendMsg("SD\r\n");
            sb.append("SD\r\n");
        } else {
//            sendMsg("HD\r\n");
            sb.append("HD\r\n");
        }

        sendMsg(sb.toString());
    }

    public void sendMsg(final String content) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    MessageBean requestBean = new MessageBean();
                    requestBean.setAction("SendCmd");
                    UserBean from = new UserBean();
                    from.setId(Client.getInstance().getUserID());
                    requestBean.setFrom(from);
                    UserBean to = new UserBean();
                    to.setId(toID);
                    requestBean.setTo(to);
                    ContentBean contentBean = new ContentBean();
                    contentBean.setStringcontent(content);
                    requestBean.setContent(contentBean);
                    Client.getInstance().sendRquestForResponse(requestBean, false, new RequestCallBack<MessageBean>() {
                        @Override
                        public void Response(MessageBean messageBean) {
                            if (messageBean.getResult().getCode() == 1) {
                                handler.sendEmptyMessage(4);
                            } else {
                                handler.sendEmptyMessage(2);
                            }
                        }
                    });

//                    Object[] msg = new Object[3];
//                    msg[0] = MainActivity.USERNAME;
//                    msg[1] = toID;
//                    msg[2] = content;
//                    MainActivity.client.sendFlag("SendCmd");
//                    for (int i = 0; i < (int) msg.length; i++) {
//                        try {
//                            MainActivity.client.Client_out.writeObject(msg[i]);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticVar.FINISH_ACTIVITY);
        registerReceiver(broadcastReceiver, filter);
        synchornize();
        MainActivity.runninguiisshowing = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        MainActivity.runninguiisshowing = false;

        if (progressDialog != null)
            progressDialog.dismiss();
        super.onDestroy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Runtime_Acyivity.this.finish();
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {        //动态调整toolbar的菜单选项
        menu.findItem(R.id.start_stop).setTitle(start_stop);
        switch (viewPager.getCurrentItem()) {
            case 0:
                menu.findItem(R.id.clear).setVisible(true);
                menu.findItem(R.id.synchronize).setVisible(true);
                menu.findItem(R.id.set).setVisible(true);
                menu.findItem(R.id.start_stop).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.clear).setVisible(false);
                menu.findItem(R.id.synchronize).setVisible(true);
                menu.findItem(R.id.set).setVisible(true);
                if (Util.getConfig(Runtime_Acyivity.this, "statusmode", "paramsmode").equals("paramsmode")) {
                    menu.findItem(R.id.start_stop).setVisible(true);
                } else {
                    menu.findItem(R.id.start_stop).setVisible(false);
                }

                break;
            case 2:
                menu.findItem(R.id.clear).setVisible(false);
                menu.findItem(R.id.synchronize).setVisible(true);
                menu.findItem(R.id.set).setVisible(true);
                menu.findItem(R.id.start_stop).setVisible(false);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.machinemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       //实现toolbar菜单点击
        switch (item.getItemId()) {
            case R.id.clear:
                DataBaseHelper dataBaseHelper = new DataBaseHelper(Runtime_Acyivity.this, MainActivity.DB_NAME);
                SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
                sqLiteDatabase.delete("histroy", "id=?", new String[]{toID});
                sqLiteDatabase.close();
                MachineMessageFragment.handler.sendEmptyMessage(StaticVar.REFRESH_MESSAGE);
                break;
            case R.id.set:
                final MessageBean messageBean = new MessageBean();
                CustomadeDialog.Builder builder = new CustomadeDialog.Builder(Runtime_Acyivity.this);
                View view = LayoutInflater.from(Runtime_Acyivity.this).inflate(R.layout.layout_set, null);
//                final EditText edt_timer = (EditText)view.findViewById(R.id.edt_timer);
//                final CheckBox cb_timer = (CheckBox) view.findViewById(R.id.cb_timer);
                cb_push = (CheckBox) view.findViewById(R.id.cb_push);
                builder.setContentView(view).setTitle("设置").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String msg[] = new String[4];
//                        msg[0] = MainActivity.USERNAME;
//                        msg[1] = toID;
//                        msg[2] = "push";
//
                        if (cb_push.isChecked()) {
                            messageBean.getContent().setStringcontent("open");
//                            msg[3] = "open";
                        } else {
                            messageBean.getContent().setStringcontent("close");
//                            msg[3] = "close";
                        }
//                        MainActivity.client.sendFlag("config");
//                        MainActivity.client.sendMsg(msg);
                        messageBean.setAction("config");
                        Client.getInstance().sendRquestForResponse(messageBean, false, new RequestCallBack<MessageBean>() {
                            @Override
                            public void Response(MessageBean messageBean) {
                                if (messageBean.getResult().getCode() == 1)
                                    Message.obtain(Runtime_Acyivity.handler, StaticVar.QUERY_CONFIG_RESULT, "配置成功").sendToTarget();
                                else
                                    Message.obtain(Runtime_Acyivity.handler, StaticVar.QUERY_CONFIG_RESULT, "配置失败").sendToTarget();
                            }
                        });

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create().show();


//                String msg[] = new String[3];
//                msg[0] = MainActivity.USERNAME;
//                msg[1] = toID;
//                msg[2] = "push";
//
//                MainActivity.client.sendFlag("queryconfig");
//                MainActivity.client.sendMsg(msg);
                messageBean.setAction("queryconfig");
                UserBean from = new UserBean();
                from.setId(Client.getInstance().getUserID());
                messageBean.setFrom(from);
                UserBean to = new UserBean();
                to.setId(toID);
                messageBean.setTo(to);
                ContentBean contentBean = new ContentBean();
                contentBean.setContenttype("push");
                messageBean.setContent(contentBean);
                Client.getInstance().sendRquestForResponse(messageBean, false, new RequestCallBack<MessageBean>() {
                    @Override
                    public void Response(MessageBean messageBean) {
                        System.out.println("messageBean.getContent().getStringcontent()" + messageBean.getContent().getStringcontent());
                        switch (messageBean.getContent().getContenttype()) {
                            case "push":

                                Message.obtain(Runtime_Acyivity.handler, StaticVar.QUERY_CONFIG, messageBean.getContent().getStringcontent()).sendToTarget();
                                break;
                        }
                    }
                });

                break;
            case R.id.synchronize:
                synchornize();
                break;
            case R.id.start_stop:
                switch (item.getTitle().toString()) {
                    case "启动":
                        sendMsg("TR:Start\r\n");
                        start_stop = "停止";
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Runtime_Acyivity.handler.sendEmptyMessage(StaticVar.SYNCHRONOUS_REQUEST);
                            }
                        }, 500);

                        break;
                    case "停止":
                        sendMsg("TR:Stop\r\n");
                        start_stop = "启动";
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Runtime_Acyivity.handler.sendEmptyMessage(StaticVar.SYNCHRONOUS_REQUEST);
                            }
                        }, 500);
                        break;

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
