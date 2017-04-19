package com.org.ehome.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import xm.mina.RequestCallBack;


import xm.mina.Client;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.org.ehome.R;
import com.org.ehome.activities.MainActivity;
import com.org.ehome.activities.Runtime_Acyivity;
import com.org.ehome.adapter.MachineAdapter;
import com.org.ehome.bean.StaticVar;
import com.org.ehome.util.DataBaseHelper;
import com.org.ehome.widget.CustomadeDialog;
import com.xm.Bean.MessageBean;
import com.xm.Bean.UserBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MachineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MachineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MachineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private PullToRefreshGridView pullToRefreshGridView;
    private MachineAdapter adapter;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    public static Handler handler;
    private CustomadeDialog.Builder builder;
    private CustomadeDialog customadeDialog;

    SimpleDateFormat simpleDateFormat;

    public MachineFragment() {
        // Required empty public constructsor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MachineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MachineFragment newInstance(String param1, String param2) {
        MachineFragment fragment = new MachineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_machine, container, false);
        init(view);
        init();
        initHandler();
        if (!Client.getInstance().isLogin()) {      //若用户没有成功登录，则从本地加载设备列表
            handler.sendEmptyMessage(StaticVar.LOAD_FROM_LOCAL_SQL);
        }
        return view;
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case StaticVar.REFRESH_MACHINE_LIST:
                        getdata(msg.obj);
                        saveDataToSql(data);
                        HistroyFragment.handler.sendEmptyMessage(StaticVar.REFRESH_HISTROY);
                        break;
                    case StaticVar.ADD_MACHINE:
                        switch (msg.obj.toString()) {
                            case "0":
                                Toast.makeText(getContext(), "该设备已在你的列表中，请不要重复", Toast.LENGTH_SHORT).show();
                                break;
                            case "1":
                                requestMyMachine();

                                Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();

                                break;
                            case "2":
                                Toast.makeText(getContext(), "添加失败", Toast.LENGTH_SHORT).show();
                                break;
                            case "3":
                                Toast.makeText(getContext(), "设备ID错误", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        break;
                    case StaticVar.REMOVE_MACHINE_FROM_LIST:
                        switch (msg.obj.toString()) {
                            case "0":
                                Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                                break;
                            case "1":
                                requestMyMachine();
                                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();

                                break;
                        }
                        break;
                    case StaticVar.REQUEST_MACHINE_LIST:
                        requestMyMachine();
                        break;
                    case StaticVar.LOAD_FROM_LOCAL_SQL:
                        loaddata();
                        break;
                    case StaticVar.REFRESH_HISTROY_FAILED:
                        Toast.makeText(getContext(), "拉取信息失败", Toast.LENGTH_SHORT).show();
                        MainActivity.handler.sendEmptyMessage(StaticVar.LOGIN);
                        break;
                }
            }
        };
    }

    /**
     * 从本地sqlite数据库加载设备列表
     */
    private void loaddata() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext(), MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query("mymachine", new String[]{"id", "nickname", "createtime", "lastsynchronizetime"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Map map = new HashMap<String, String>();
            map.put("MachineNickName", cursor.getString(cursor.getColumnIndex("nickname")));
            map.put("MachineID", cursor.getString(cursor.getColumnIndex("id")));
            map.put("CreateTime", cursor.getString(cursor.getColumnIndex("createtime")));
            map.put("Online", "否");
            data.add(map);
        }
        cursor.close();
        sqliteDatabase.close();
        dataBaseHelper.close();
        adapter.notifyDataSetChanged();
    }

    void init() {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * 获取设备信息列表，并更新UI
     *
     * @param obj 设备信息数据源
     */
    private void getdata(Object obj) {
        // TODO Auto-generated method stub
        data.clear();
        try {
            JSONArray jsonArray = new JSONArray(obj.toString());
            Map<String, String> map;
            for (int i = 0; i < jsonArray.length(); i++) {
                map = new HashMap<String, String>();
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                map.put("MachineNickName", jsonObject.get("MachineNickName").toString());
                map.put("MachineID", jsonObject.get("MachineID").toString());
                map.put("CreateTime", jsonObject.get("CreateTime").toString());
                map.put("Online", jsonObject.get("Online").toString());
                data.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * 将设备信息保存到sqlite
     *
     * @param data 设备信息
     */
    private void saveDataToSql(List<Map<String, String>> data) {
        if (getActivity() == null) {
            return;
        }
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity(), MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        for (int i = 0; i < data.size(); i++) {
            Cursor cursor = sqliteDatabase.rawQuery("select * from mymachine where id=?", new String[]{data.get(i).get("MachineID")});
            if (cursor.getCount() == 0) {
                insert(data.get(i));
            } else {
                update(data.get(i));
            }
            cursor.close();
        }
        sqliteDatabase.close();
        dataBaseHelper.close();

//        ContentValues values;
//        for (int i = 0;i<data.size();i++){
//            values=new ContentValues();
//            values.put("id",data.get(i).get("MachineID"));
//            values.put("nickname",data.get(i).get("MachineNickName"));
//            values.put("createtime", data.get(i).get("CreateTime"));
//            MainActivity.sqliteDatabase.update("mymachine", values, "id=?", new String[]{data.get(i).get("MachineID")});
////            MainActivity.sqliteDatabase.delete("mymachine","id=?",new String[]{data.get(i).get("MachineID")});
//
//        }
    }

    private void insert(Map<String, String> map) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext(), MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        ContentValues values;
        values = new ContentValues();
        values.put("id", map.get("MachineID"));
        values.put("nickname", map.get("MachineNickName"));
        values.put("createtime", map.get("CreateTime"));
        values.put("online", map.get("Online"));
        sqliteDatabase.insert("mymachine", null, values);
        sqliteDatabase.close();
        dataBaseHelper.close();
    }

    private void update(Map<String, String> map) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext(), MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        ContentValues values;
        values = new ContentValues();
        values.put("id", map.get("MachineID"));
        values.put("nickname", map.get("MachineNickName"));
        values.put("createtime", map.get("CreateTime"));
        values.put("online", map.get("Online"));
        sqliteDatabase.update("mymachine", values, "id=?", new String[]{map.get("MachineID")});
        sqliteDatabase.close();
        dataBaseHelper.close();
    }

    /**
     * 向服务器发送获取设备列表请求
     */
    private void requestMyMachine() {

        System.out.println("requestMyMachine");

        if (!Client.getInstance().isServerIsConnected()) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
//            MainActivity.client.sendFlag("querymymachine");
//            String[] msg = new String[1];
//            msg[0] = MainActivity.USERNAME;
//            MainActivity.client.sendMsg(msg);
            MessageBean messageBean = new MessageBean();
            messageBean.setAction("querymymachine");
            UserBean from = new UserBean();
            from.setType("mob");
            from.setId(Client.getInstance().getUserID());
            messageBean.setFrom(from);
            Client.getInstance().sendRquestForResponse(messageBean, false, new RequestCallBack<MessageBean>() {

                @Override
                public void Response(MessageBean messageBean) {
                    handler.obtainMessage(StaticVar.REFRESH_MACHINE_LIST, messageBean.getContent().getStringcontent()).sendToTarget();
                }
            });
        } catch (Exception e) {
            System.out.println("requestMyMachine失败");
        }


    }


    private void init(View view) {
//


        pullToRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.pullToRefreshGridView);
        adapter = new MachineAdapter(getActivity(), data, R.layout.item_gridview);
        pullToRefreshGridView.setAdapter(adapter);
        pullToRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
            @Override
            public void onRefresh(
                    PullToRefreshBase<GridView> refreshView) {
                String label = DateUtils.formatDateTime(
                        getActivity().getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);
                // 显示最后更新的时间
                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);

                new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... params) {
                        try {
                            System.out.println("doInBackground");
                            requestMyMachine();
                            return 1;
                        } catch (Exception e) {
                            return 0;
                        }
                    }

                    protected void onPostExecute(Integer result) {
                        if (result == 1) {
                            adapter.notifyDataSetChanged();
                        } else {
                            handler.sendEmptyMessage(StaticVar.REFRESH_HISTROY_FAILED);
                        }

                        // 隐藏头布局
                        pullToRefreshGridView.onRefreshComplete();
                    }
                }.execute();
            }
        });
        pullToRefreshGridView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                MachineAdapter.ViewHolder holder = (MachineAdapter.ViewHolder) view.getTag();
                Intent intent = new Intent();
                intent.setClass(getContext(), Runtime_Acyivity.class);
                intent.putExtra("toID", holder.MachineID);
                intent.putExtra("nicknname", holder.itemText.getText().toString());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
            }
        });
        pullToRefreshGridView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                MachineAdapter.ViewHolder holder = (MachineAdapter.ViewHolder) view.getTag();
                customadeDialog = MyMachineDialog(holder.itemText.getText().toString(), holder.MachineID);
                customadeDialog.show();
                return true;
            }
        });

        WindowManager manger = getActivity().getWindowManager();
        Display display = manger.getDefaultDisplay();
        //屏幕高度
        int screenHeight = display.getHeight();
        //屏幕宽度
        int screenWidth = display.getWidth();
        ColumnInfo colInfo = calculateColumnWidthAndCountInRow(screenWidth, 200, 2);
//        int rowNum = data.size()%colInfo.countInRow == 0 ? data.size()/colInfo.countInRow:data.size()/colInfo.countInRow+1;
//        pullToRefreshGridView.setLayoutParams(new ViewGroup.MarginLayoutParams(screenWidth, rowNum * colInfo.width + (rowNum-1)*2));
        pullToRefreshGridView.getRefreshableView().setNumColumns(colInfo.countInRow);
        pullToRefreshGridView.getRefreshableView().setGravity(Gravity.CENTER);
        pullToRefreshGridView.getRefreshableView().setHorizontalSpacing(2);
        pullToRefreshGridView.getRefreshableView().setVerticalSpacing(2);
        pullToRefreshGridView.getRefreshableView().setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
    }

    /**
     * gridview item长按对话框
     *
     * @param machineName 设备名称
     * @param machineID   设备ID
     * @return
     */
    CustomadeDialog MyMachineDialog(String machineName, final String machineID) {
        builder = new CustomadeDialog.Builder(getContext());
        ListView listView = new ListView(getContext());
        SimpleAdapter adapter = new SimpleAdapter(getContext(), getdata(), android.R.layout.simple_list_item_1, new String[]{"itemText"}, new int[]{android.R.id.text1});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        final TextView textView = new TextView(getContext());
                        textView.setText("确认删除？");
                        builder.setTitle("提示").setContentView(textView).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MessageBean messageBean = new MessageBean();
                                messageBean.setAction("RemoveMachineFromList");
                                UserBean from = new UserBean();
                                from.setId(Client.getInstance().getUserID());
                                messageBean.setFrom(from);
                                UserBean to = new UserBean();
                                to.setId(machineID);
                                messageBean.setTo(to);
                                Client.getInstance().sendRquestForResponse(messageBean, false, new RequestCallBack<MessageBean>() {
                                    @Override
                                    public void Response(MessageBean messageBean) {
                                        MachineFragment.handler.obtainMessage(StaticVar.REMOVE_MACHINE_FROM_LIST, messageBean.getResult().getCode() + "").sendToTarget();
                                    }
                                });

                                customadeDialog.dismiss();

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();


//                        String msg[] = new String[2];
//                        msg[0] = MainActivity.USERNAME;
//                        msg[1] = machineID;
//                        MainActivity.client.sendFlag("RemoveMachineFromList");
//                        MainActivity.client.sendMsg(msg);
//                        customadeDialog.dismiss();
                        break;
                    case 1:
                        final EditText edt = new EditText(getContext());
                        builder.setTitle("重命名").setContentView(edt).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MessageBean messageBean = new MessageBean();
                                messageBean.setAction("changenickname");
                                UserBean from = new UserBean();
                                from.setId(Client.getInstance().getUserID());
                                messageBean.setFrom(from);
                                UserBean to = new UserBean();
                                to.setId(machineID);
                                to.setNickname(edt.getText().toString());
                                messageBean.setTo(to);
                                Client.getInstance().sendRquestForResponse(messageBean, false, new RequestCallBack<MessageBean>() {
                                    @Override
                                    public void Response(MessageBean messageBean) {
                                        if (messageBean.getResult().getCode() == 1) {
                                            handler.obtainMessage(StaticVar.REQUEST_MACHINE_LIST).sendToTarget();
                                        } else {
                                            Toast.makeText(getContext(), "重命名失败", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
//                                String[] msg = new String[3];
//                                msg[0] = MainActivity.USERNAME;
//                                msg[1] = machineID;
//                                msg[2] = edt.getText().toString();
//                                MainActivity.client.sendFlag("changenickname");
//                                MainActivity.client.sendMsg(msg);

                                customadeDialog.dismiss();

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();
                        break;

                    default:
                        break;
                }

            }
        });
        builder.setTitle("你要对" + machineName + "做的是？").setContentView(listView).setNegativeButton("取消", null);
        return builder.create();
    }

    private List<? extends Map<String, ?>> getdata() {
        // TODO Auto-generated method stub
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("itemText", "删除该设备");
        data.add(map);
        map = new HashMap<String, String>();
        map.put("itemText", "重命名");
        data.add(map);
        return data;
    }

    //存放计算后的单元格相关信息
    class ColumnInfo {
        //单元格宽度
        public int width = 0;
        //每行所能容纳的单元格数量
        public int countInRow = 0;
    }

    /**
     * 根据手机屏幕宽度，计算gridview每个单元格的宽度
     *
     * @param screenWidth 屏幕宽度
     * @param width       单元格预设宽度
     * @param padding     单元格间距
     * @return
     */
    private ColumnInfo calculateColumnWidthAndCountInRow(int screenWidth, int width, int padding) {
        ColumnInfo colInfo = new ColumnInfo();
        int colCount = 0;
        //判断屏幕是否刚好能容纳下整数个单元格，若不能，则将多出的宽度保存到space中
        int space = screenWidth % width;
        if (space == 0) { //正好容纳下
            colCount = screenWidth / width;
        } else if (space >= (width / 2)) { //多出的宽度大于单元格宽度的一半时，则去除最后一个单元格，将其所占的宽度平分并增加到其他每个单元格中
            colCount = screenWidth / width;
            space = width - space;
            width = width + space / colCount;
        } else {  //多出的宽度小于单元格宽度的一半时，则将多出的宽度平分，并让每个单元格减去平分后的宽度
            colCount = screenWidth / width + 1;
            width = width - space / colCount;
        }
        colInfo.countInRow = colCount;
        //计算出每行的间距总宽度，并根据单元格的数量重新调整单元格的宽度
        colInfo.width = width - ((colCount + 1) * padding) / colCount;
        return colInfo;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestMyMachine();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
