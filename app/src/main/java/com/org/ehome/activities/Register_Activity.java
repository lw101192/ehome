package com.org.ehome.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import xm.mina.RequestCallBack;

import com.org.ehome.R;

import xm.mina.Client;

import com.org.ehome.bean.StaticVar;
import com.org.ehome.widget.PasswordDialog;
import com.xm.Bean.MessageBean;
import com.xm.Bean.UserBean;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class Register_Activity extends AppCompatActivity implements OnClickListener {

    EditText registerpasssword;
    EditText registerphone;
    Button requestCodeBtn;
    int i = 30;
    private Toolbar toolbar;
    private TextView title;
    private TextView complete;
    private PasswordDialog passwordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        init();
        initToolBar();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title.setText("注册账号");
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

    private void init() {
        // TODO Auto-generated method stub
        registerpasssword = (EditText) findViewById(R.id.registerPassword);
        registerphone = (EditText) findViewById(R.id.phonenumber);
        requestCodeBtn = (Button) findViewById(R.id.request_code_btn);
        requestCodeBtn.setOnClickListener(this);

        registerphone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(registerphone.getText())) {
                        registerphone.setError(null);
                        return;
                    }
                    CheckThread thread = new CheckThread(registerphone.getText().toString());
                    thread.start();
                }
            }
        });


        initSMSSDK();


    }

    private void initSMSSDK() {
        // TODO Auto-generated method stub
        SMSSDK.initSDK(this, "1586c90098160", "e62c8f1639972c94b56eaa6f971597b9");
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    //
    Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Drawable drawable = getResources()
                            .getDrawable(R.drawable.right);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                    registerphone.setError("该手机号可用", drawable);
                    break;
                case 2:
                    registerphone.setError("该手机号已被注册");
                    break;
                case 3:
                    Toast.makeText(Register_Activity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 4:
                    Toast.makeText(Register_Activity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

        ;
    };
    //
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                requestCodeBtn.setText("重新发送(" + i + ")");
            } else if (msg.what == -8) {
                requestCodeBtn.setText("获取验证码");
                requestCodeBtn.setEnabled(true);
                i = 30;
            } else {
                //int event = msg.arg1;
                //int result = msg.arg2;
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                        Toast.makeText(getApplicationContext(), "提交验证码成功",
                                Toast.LENGTH_SHORT).show();

                        passwordDialog.dismiss();
                        RegisterThread thread = new RegisterThread(registerphone.getText().toString(), registerpasssword.getText().toString());
                        thread.start();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "验证码已经发送，请查收",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                } else if (result == 0) {
                    Toast.makeText(getApplicationContext(), "验证码错误",
                            Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    @Override
    public void onClick(View v) {
        final String phoneNums = registerphone.getText().toString();
        switch (v.getId()) {
            case R.id.request_code_btn:
                // 1. 通过规则判断手机号
                // TODO Auto-generated method stub

                // 1. 通过规则判断手机号
                if (!judgePhoneNums(phoneNums) || TextUtils.isEmpty(registerpasssword.getText())) {
                    Toast.makeText(this, "请检查手机号和密码是否输入正确！", Toast.LENGTH_SHORT).show();
                    return;
                }
            /*
             * if (!judgePhoneNums(phoneNums)) { return; }
			 */// 2. 通过sdk发送短信验证
                SMSSDK.getVerificationCode("86", phoneNums);

                // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                requestCodeBtn.setEnabled(false);
                requestCodeBtn.setText("重新发送(" + i + ")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; i > 0; i--) {
                            handler.sendEmptyMessage(-9);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-8);
                    }
                }).start();

                passwordDialog = new PasswordDialog(Register_Activity.this);
                passwordDialog.show();
                passwordDialog.setTitle("请输入验证码");
                passwordDialog.setOnFinishInput(new PasswordDialog.OnPasswordInputFinish() {
                    @Override
                    public void inputFinish() {
                        SMSSDK.submitVerificationCode("86", phoneNums,passwordDialog.getPassword());
                    }
                });
                break;

        }
    }

    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str == null) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }


    public class RegisterThread extends Thread {
        private String password;
        private String tel;

        public RegisterThread(String phonenumber, String password
        ) {
            // TODO Auto-generated constructor stub
            this.password = password;
            this.tel = phonenumber;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (TextUtils.isEmpty(tel) || TextUtils.isEmpty(password)) {
                return;
            }
            try {
                MessageBean messageBean = new MessageBean();
                messageBean.setAction("register");
                UserBean from = new UserBean();
                from.setId(tel);
                from.setNewpw(password);
                messageBean.setFrom(from);
                Client.getInstance().sendRquestForResponse(messageBean, true, new RequestCallBack<MessageBean>() {
                    @Override
                    public void Response(MessageBean messageBean) {
                        if (messageBean.getResult().getCode() == 1) {
                            handler2.sendEmptyMessage(3);
                        } else {
                            handler2.sendEmptyMessage(4);
                        }

                    }
                });

//                String msg[] = new String[2];
//                ConnctionServer cs = new ConnctionServer(); // 连接服务器
//
//
//                msg[0] = tel;
//                msg[1] = password;
//                cs.sendFlag("register");
//
//                cs.sendMsg(msg); // 发送注册信息
//                String str = cs.inceptMsg();
//
//                if (str.equals("1")) {
//                    handler2.sendEmptyMessage(3);
//                } else {
//                    handler2.sendEmptyMessage(4);
//                }
            } catch (Exception e) {
                // TODO: handle exception
                try {
                    sleep(2000);
                    handler2.sendEmptyMessage(4);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        }
    }

    public class CheckThread extends Thread {
        private String tel;

        public CheckThread(String tel) {
            // TODO Auto-generated constructor stub
            this.tel = tel;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (TextUtils.isEmpty(tel)) {
                return;
            }
            try {
//                String msg[] = new String[1];
//                ConnctionServer cs = new ConnctionServer(); // 连接服务器
//
//
//                msg[0] = tel;
//                cs.sendFlag("check");
//
//                cs.sendMsg(msg); // 发送注册信息
//                String str = cs.inceptMsg();
//
//                if (str.equals("0")) {
//                    handler2.sendEmptyMessage(1);
//                } else {
//                    handler2.sendEmptyMessage(2);
//                }

                MessageBean messageBean = new MessageBean();
                messageBean.setAction("check");
                UserBean from = new UserBean();
                from.setId(tel);
                messageBean.setFrom(from);
                Client.getInstance().sendRquestForResponse(messageBean, true, new RequestCallBack<MessageBean>() {
                    @Override
                    public void Response(MessageBean messageBean) {
                        if (messageBean.getResult().getCode() == 1) {
                            handler2.sendEmptyMessage(2);
                        } else {
                            handler2.sendEmptyMessage(1);
                        }
                    }
                });
            } catch (Exception e) {
                // TODO: handle exception

            }
        }
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
            Register_Activity.this.finish();
        }
    };

}
