package com.org.ehome.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.org.ehome.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 输入密码Dialog
 */

public class PasswordDialog extends Dialog implements View.OnClickListener {
    private Context mContext;

    private TextView tvTitle, tvInfo, tvForgetPassword;
    private ImageView ivCancel;
    private GridView gvKeyboard;

    private String strPassword;//输入的密码
    private TextView[] tvLists;//6个TextView数组
    private ArrayList<Map<String, String>> valueList;//键盘按钮对应数据

    private int currentIndex = -1;//当前密码输入到的位数

    public PasswordDialog(Context context) {
        super(context, R.style.MyFullScreenDialog_Style);
        mContext = context;
        valueList = new ArrayList<>();
        tvLists = new TextView[4];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_password);
        initView();
        initEvent();
        System.out.println("PasswordDialog onCreate");
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        gvKeyboard = (GridView) findViewById(R.id.gv_keyboard);

        for (int i = 0; i < 4; i++) {
            tvLists[i] = new TextView(mContext);
        }
        tvLists[0] = (TextView) findViewById(R.id.tv_password_1);
        tvLists[1] = (TextView) findViewById(R.id.tv_password_2);
        tvLists[2] = (TextView) findViewById(R.id.tv_password_3);
        tvLists[3] = (TextView) findViewById(R.id.tv_password_4);

        setGridView();
    }

    private void initEvent() {
        tvForgetPassword.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
    }

    //键盘布局
    private void setGridView() {
        //数字
        String[] strNum = new String[10];
        //{0,1,2,3,--9}
        for (int i = 0; i < 10; i++) {
            strNum[i] = i + "";
        }

        //字母
        String[] strWord = {"ABC", "DEF", "GHI", "JKL", "MNO", "PQRS", "TUV", "WXYZ"};

        for (int i = 0; i < 12; i++) {
            Map<String, String> map = new HashMap<>();
            if (i > 0 && i < 9) {
                map.put("num", strNum[i + 1]);
                map.put("word", strWord[i - 1]);
            } else if (i == 0) {
                map.put("num", strNum[1]);
                map.put("word", "");
            } else if (i == 9) {
                map.put("num", "");
                map.put("word", "");
            } else if (i == 10) {
                map.put("num", strNum[0]);
                map.put("word", "");
            } else if (i == 11) {
                map.put("num", "");
                map.put("word", "");
            }
            valueList.add(map);
        }

        gvKeyboard.setAdapter(adapter);

        gvKeyboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 9 && position != 11) {
                    if (currentIndex >= -1 && currentIndex < 3) {
                        tvLists[++currentIndex].setText(valueList.get(position).get("num"));
                    }
                } else {
                    if (position == 11) {
                        if (currentIndex - 1 >= -1) {
                            tvLists[currentIndex--].setText("");
                        }
                    }
                }
            }
        });
    }

    /**
     * 监听，在输入完成后触发
     *
     * @param pass
     */
    public void setOnFinishInput(final OnPasswordInputFinish pass) {
        tvLists[3].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    strPassword = "";

                    for (int i = 0; i < 4; i++) {
                        strPassword += tvLists[i].getText().toString().trim();
                    }

                    if(strPassword.length() == 4)
                        pass.inputFinish();
                }
            }
        });
    }

    //获取密码
    public String getPassword() {
        return strPassword;
    }

    //设置标题
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    //设置info
    public void setInfo(String info) {
        tvInfo.setText(info);
    }

    //底部
    public void setBottomText(String bottomText) {
        tvForgetPassword.setText(bottomText);
    }

    BaseAdapter adapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return valueList.size();
        }

        @Override
        public Object getItem(int position) {
            return valueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gv_keybtn, parent, false);
//                convertView = View.inflate(mContext, R.layout.item_gv_keybtn, parent);
                holder = new ViewHolder();
                holder.tvNum = (TextView) convertView.findViewById(R.id.tv_num);
                holder.tvWord = (TextView) convertView.findViewById(R.id.tv_word);
                holder.ivDel = (ImageView) convertView.findViewById(R.id.iv_del);
                holder.llItem = (FrameLayout) convertView.findViewById(R.id.ll_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == 9) {
                holder.llItem.setBackgroundResource(R.color.bg_keyboard_gray);
            } else if (position == 11) {
                holder.llItem.setBackgroundResource(R.color.bg_keyboard_gray);
                holder.ivDel.setImageResource(R.drawable.ic_delete);
                holder.ivDel.setVisibility(View.VISIBLE);
                holder.tvNum.setVisibility(View.GONE);
                holder.tvWord.setVisibility(View.GONE);
            } else {
                holder.tvNum.setText(valueList.get(position).get("num"));
                holder.tvWord.setText(valueList.get(position).get("word"));
            }
            return convertView;
        }

    };

    public final class ViewHolder {
        public TextView tvNum;
        public TextView tvWord;
        public ImageView ivDel;
        public FrameLayout llItem;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                this.dismiss();
                break;
            case R.id.tv_forget_password:
                break;
        }
    }

    public interface OnPasswordInputFinish {
        void inputFinish();
    }
}
