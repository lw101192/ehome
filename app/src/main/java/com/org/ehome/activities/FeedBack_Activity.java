package com.org.ehome.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import xm.mina.RequestCallBack;


import xm.mina.Client;

import com.org.ehome.R;
import com.org.ehome.bean.StaticVar;
import com.xm.Bean.ContentBean;
import com.xm.Bean.MessageBean;
import com.xm.Bean.UserBean;


public class FeedBack_Activity extends AppCompatActivity {

    private EditText phoneno;
    private EditText feedback;
    private Button submit;
    public static Handler handler;
    private Toolbar toolbar;
    private TextView title;
    private TextView complete;
    private boolean shortConnnection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feedback);
        init();
        initToolBar();
        initHandler();
    }

    private void initHandler() {
        // TODO Auto-generated method stub
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case 0:
                        if (msg.obj.equals("1")) {
                            Toast.makeText(FeedBack_Activity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            ;
                        } else {
                            Toast.makeText(FeedBack_Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                            ;
                        }

                        break;

                    default:
                        break;
                }
            }
        };
    }

    private void init() {
        // TODO Auto-generated method stub
        phoneno = (EditText) findViewById(R.id.phoneno);
        feedback = (EditText) findViewById(R.id.feedback);
        submit = (Button) findViewById(R.id.submit);

        if (getIntent().getAction() != null) {
            if (getIntent().getAction().toString().equals("fromloginactivity")) {
                shortConnnection = true;
            } else if (getIntent().getAction().toString().equals("fromUserFragment")) {
                shortConnnection = false;
            }
        }

        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String phonenostr = "";
                if (!TextUtils.isEmpty(phoneno.getText())) {
                    phonenostr = phoneno.getText().toString();
                } else {
                    if (Client.getInstance().getUserID() != null)
                        phonenostr = Client.getInstance().getUserID();
                }
                if (TextUtils.isEmpty(feedback.getText()))
                    return;
                FeedbackThread thread = new FeedbackThread(phonenostr, feedback.getText().toString());
                thread.start();
            }
        });

    }


    public class FeedbackThread extends Thread {
        private String phoneno;
        private String feedback;

        public FeedbackThread(String phoneno, String feedback) {
            // TODO Auto-generated constructor stub
            this.phoneno = phoneno;
            this.feedback = feedback;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            MessageBean messageBean = new MessageBean();
            messageBean.setAction("feedback");
            UserBean from = new UserBean();
            from.setId(phoneno);
            messageBean.setFrom(from);
            ContentBean contentBean = new ContentBean();
            contentBean.setStringcontent(feedback);
            messageBean.setContent(contentBean);
            Client.getInstance().sendRquestForResponse(messageBean, shortConnnection, new RequestCallBack<MessageBean>() {
                @Override
                public void Response(MessageBean messageBean) {
                    if (messageBean.getResult().getCode() == 1)
                        FeedBack_Activity.handler.obtainMessage(0, "1").sendToTarget();
                    if (messageBean.getResult().getCode() == 0)
                        FeedBack_Activity.handler.obtainMessage(0, "0").sendToTarget();
                }
            });
//            String[] msg = new String[2];
//            msg[0] = phoneno;
//            msg[1] = feedback;
//            ConnctionServer cs = null;
//            try {
//                MainActivity.client.sendFlag("feedback");
//                MainActivity.client.sendMsg(msg);
//            } catch (Exception e) {
//                // TODO: handle exception
//                try {
//                    cs = new ConnctionServer(); // 连接服务器
//                    cs.sendFlag("feedback");
//                    cs.sendMsg(msg);
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
//            }
//
//            try {
//                if (cs!=null) {
//                    cs.StreamClose();
//                }
//            } catch (Exception e) {
//
//            }

        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticVar.FINISH_ACTIVITY);
        registerReceiver(broadcastReceiver, filter);
        super.onResume();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FeedBack_Activity.this.finish();
        }
    };

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title.setText("反馈");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        complete = (TextView) findViewById(R.id.tv_config);
        complete.setText("完成");
        complete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_in_from_left, R.anim.back_out_to_right);
    }
}
