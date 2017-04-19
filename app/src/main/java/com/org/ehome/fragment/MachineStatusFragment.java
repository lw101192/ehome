package com.org.ehome.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import xm.mina.RequestCallBack;

import com.org.ehome.R;

import xm.mina.Client;

import com.org.ehome.activities.Runtime_Acyivity;
import com.org.ehome.adapter.StatusExpandAdapter;
import com.org.ehome.adapter.TempAdapter;
import com.org.ehome.bean.OneStatusEntity;
import com.org.ehome.bean.StaticVar;
import com.org.ehome.bean.TempItem;
import com.org.ehome.bean.TwoStatusEntity;
import com.org.ehome.util.Util;
import com.org.ehome.widget.DragZoomLocationView;
import com.xm.Bean.ContentBean;
import com.xm.Bean.MessageBean;
import com.xm.Bean.UserBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MachineStatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MachineStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MachineStatusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ID = "param1";
    private static final String ARG_NICKNAME = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView tv_config;

    private OnFragmentInteractionListener mListener;

    public static Handler handler;
    private View view;
    private StatusExpandAdapter statusAdapter;
    private String start_stop;
    private TextView defaultview;
    private TextView processtitle;
    List<OneStatusEntity> oneList;
    private ExpandableListView expandableListView;
    private TempAdapter tempAdapter;
    private List<TempItem> item_TempList = new ArrayList<>();
    private RecyclerView tempListview;
    private LinearLayoutManager lm;
    private Button statusmode;
    private CheckBox quality;
    private DragZoomLocationView img;
    private LinearLayout imglayout;
    private LinearLayout qualitylinearlayout;

    public MachineStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MachineStatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MachineStatusFragment newInstance(String param1, String param2) {
        MachineStatusFragment fragment = new MachineStatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, param1);
        args.putString(ARG_NICKNAME, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_ID);
            mParam2 = getArguments().getString(ARG_NICKNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_machine_status, container, false);
        initView(view);
        initHandler();
        return view;
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case StaticVar.SEND_MESSAGE:
                        sendMsg("TR:" + msg.obj.toString() + "&&" + Util.getConfig(getActivity(), "quality", "SD"));
                        break;
                    case StaticVar.SYNCHRONOUS_NULL:
                        defaultview.setText("设备休息中，啥事也没干！");
                        img.setImageResource(0);
                        imglayout.setVisibility(View.GONE);
                        ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(0).setVisibility(View.VISIBLE);
                        ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(1).setVisibility(View.INVISIBLE);
                        ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(2).setVisibility(View.INVISIBLE);
                        break;
                    case StaticVar.SYNCHRONOUS_RESULT_IMAGE:
//                        Runtime_Acyivity.handler.sendEmptyMessage(StaticVar.SYNCHRONOUS_SUCCEED);
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        if (getActivity() == null)
                            break;
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        imglayout.setVisibility(View.VISIBLE);
                        img.setImageBitmap(Util.GenerateImage(msg.obj.toString(), displayMetrics.widthPixels, displayMetrics.widthPixels * 3 / 5));
                        ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(0).setVisibility(View.GONE);
                        ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(1).setVisibility(View.GONE);
                        ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(2).setVisibility(View.GONE);
                        break;
                    case StaticVar.SYNCHRONOUS_RESULT:
                        img.setImageResource(0);
                        imglayout.setVisibility(View.GONE);

                        System.out.println("StaticVar.SYNCHRONOUS_RESULT" + msg.obj.toString());
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        try {
                            switch (jsonObject.get("paramstype").toString()) {
                                case "standard":
                                    ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(0).setVisibility(View.INVISIBLE);
                                    ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(1).setVisibility(View.VISIBLE);
                                    ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(2).setVisibility(View.INVISIBLE);
                                    getData(jsonObject, "standard");
                                    statusAdapter = new StatusExpandAdapter(getContext(), oneList);
                                    expandableListView.setAdapter(statusAdapter);

                                    // 遍历所有group,将所有项设置成默认展开
                                    int groupCount = expandableListView.getCount();
                                    for (int i = 0; i < groupCount; i++) {
                                        expandableListView.expandGroup(i);
                                    }
                                    break;
                                case "nonstandard":
                                    ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(0).setVisibility(View.INVISIBLE);
                                    ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(1).setVisibility(View.VISIBLE);
                                    ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(2).setVisibility(View.INVISIBLE);
                                    getData(jsonObject, "nonstandard");
                                    statusAdapter = new StatusExpandAdapter(getActivity(), oneList);
                                    expandableListView.setAdapter(statusAdapter);

                                    // 遍历所有group,将所有项设置成默认展开
                                    groupCount = expandableListView.getCount();
                                    for (int i = 0; i < groupCount; i++) {
                                        expandableListView.expandGroup(i);
                                    }
                                    break;
                                case "config":
                                    if (jsonObject.getString("device").equals("下载")) {
                                        tv_config.setText("下载中。。。");
                                    } else {
                                        ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(0).setVisibility(View.INVISIBLE);
                                        ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(1).setVisibility(View.INVISIBLE);
                                        ((FrameLayout) view.findViewById(R.id.layout_status)).getChildAt(2).setVisibility(View.VISIBLE);
                                        StringBuffer sb = new StringBuffer();
                                        sb.append("设备名称：" + jsonObject.getString("device") + "\n");
                                        try {
                                            sb.append("共" + Integer.parseInt(jsonObject.getString("totalsteps")) / 60 + "分钟，剩余" + Integer.parseInt(jsonObject.getString("currentstep")) / 60 + "分" + Integer.parseInt(jsonObject.getString("currentstep")) % 60 + "秒" + "\n");
                                            start_stop = jsonObject.getString("runningstate").equals("运行") ? "停止" : "启动";
                                            sb.append("运行状态：" + jsonObject.getString("runningstate") + "\n");
                                        } catch (Exception e) {
                                            start_stop = "启动";
                                        }


                                        tv_config.setText(sb.toString());
                                    }
//                                    getData(jsonObject, "nonstandard");
//                                    statusAdapter = new StatusExpandAdapter(Runtime_Acyivity.this, oneList);
//                                    expandableListView.setAdapter(statusAdapter);
//
//                                    // 遍历所有group,将所有项设置成默认展开
//                                    groupCount = expandableListView.getCount();
//                                    for (int i = 0; i < groupCount; i++) {
//                                        expandableListView.expandGroup(i);
//                                    }
                                    break;

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }

            }
        };

    }

    private void getData(JSONObject res, String type) {
        try {
            start_stop = res.getString("runningstate").equals("运行") ? "停止" : "启动";
        } catch (Exception e) {
            start_stop = "启动";
        }
        switch (type) {
            case "standard":
                int count = 0;
                int currentstep = 0;
                oneList = new ArrayList<OneStatusEntity>();
                try {
                    JSONArray grouparray = res.getJSONArray("paramsgroup");
                    JSONArray childarray = res.getJSONArray("paramschild");
                    JSONArray temperarray = res.getJSONArray("paramstemperature");
                    try {
                        currentstep = Integer.parseInt(res.getString("currentstep"));
                    } catch (Exception e) {
                    }
                    processtitle.setText("流程：" + res.getString("paramstitle"));
                    for (int i = 0; i < grouparray.length(); i++) {
                        OneStatusEntity group = new OneStatusEntity();
                        group.setStatusName(grouparray.getString(i));
                        List<TwoStatusEntity> twoList = new ArrayList<TwoStatusEntity>();
                        TwoStatusEntity two = new TwoStatusEntity();

                        if (count < currentstep)
                            two.setIsfinished(true);
                        count++;

                        StringBuffer sb = new StringBuffer();
                        try {
                            sb.append("混合速度:" + childarray.getJSONObject(i).getString("混合速度") + "\n");
                        } catch (Exception e) {
                        }
                        try {
                            sb.append("混合幅度:" + childarray.getJSONObject(i).getString("混合幅度") + "\n");
                        } catch (Exception e) {
                        }
                        try {
                            sb.append("混合时间:" + childarray.getJSONObject(i).getString("混合时间") + " min\n");
                        } catch (Exception e) {
                        }
                        try {
                            sb.append("静置反应时间:" + childarray.getJSONObject(i).getString("静置反应时间") + " min\n");
                        } catch (Exception e) {
                        }
                        try {
                            sb.append("振荡起始位:" + childarray.getJSONObject(i).getString("振荡起始位") + " mm\n");
                        } catch (Exception e) {
                        }
                        if (i == 0)
                            try {
                                sb.append("裂解1温度:" + temperarray.get(0) + " ℃\n");
                                sb.append("裂解2温度:" + temperarray.get(2) + " ℃\n");
                                if (res.getString("machinetype").equals("pnae32")) {
                                    sb.append("裂解3温度:" + temperarray.get(4) + " ℃\n");
                                    sb.append("裂解4温度:" + temperarray.get(6) + " ℃\n");
                                }
                            } catch (Exception e) {
                            }
                        try {
                            sb.append("磁珠吸附时间:" + childarray.getJSONObject(i).getString("磁珠吸附时间") + " min\n");
                        } catch (Exception e) {
                        }
                        try {
                            sb.append("磁珠转移速度:" + childarray.getJSONObject(i).getString("磁珠转移速度"));
                        } catch (Exception e) {
                        }
                        try {
                            sb.append("洗涤剂挥发时间:" + childarray.getJSONObject(i).getString("洗涤剂挥发时间") + " min");
                        } catch (Exception e) {
                        }
                        if (i == 6)
                            try {
                                sb.append("洗脱温度:" + temperarray.get(1) + " ℃\n");
                                sb.append("洗脱温度:" + temperarray.get(3) + " ℃\n");
                                if (res.getString("machinetype").equals("pnae32")) {
                                    sb.append("洗脱温度:" + temperarray.get(5) + " ℃\n");
                                    sb.append("洗脱温度:" + temperarray.get(7) + " ℃\n");
                                }
                            } catch (Exception e) {
                            }


                        two.setStatusName(sb.toString());
//                    if(time[j].equals("")){
//                        two.setCompleteTime("暂无");
//                        two.setIsfinished(false);
//                    }else{
//                        two.setCompleteTime(time[j]+" 完成");
//                        two.setIsfinished(true);
//                    }

                        twoList.add(two);
                        group.setTwoList(twoList);
                        oneList.add(group);
                    }
                    getParamsTemperature(res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "nonstandard":
                count = 0;
                currentstep = 0;
                oneList = new ArrayList<OneStatusEntity>();
                try {
                    processtitle.setText("流程：" + res.getString("paramstitle"));
                } catch (Exception e) {
                    processtitle.setText("流程：自定义");
                }
                try {
                    getParamsTemperature(res);

                    JSONArray grouparray = res.getJSONArray("paramsgroup");
                    JSONArray childarray = res.getJSONArray("paramschild");


                    try {
                        currentstep = Integer.parseInt(res.getString("currentstep"));
                    } catch (Exception e) {
                    }

                    for (int i = 0; i < grouparray.length(); i++) {
                        OneStatusEntity group = new OneStatusEntity();
                        group.setStatusName(grouparray.getString(i));
                        List<TwoStatusEntity> twoList = new ArrayList<TwoStatusEntity>();
                        for (int l = 0; l < childarray.getJSONArray(i).length(); l++) {
                            TwoStatusEntity obj = new TwoStatusEntity();
                            if (count < currentstep)
                                obj.setIsfinished(true);
                            count++;

                            StringBuffer sb = new StringBuffer();
                            try {
                                sb.append("工位:" + childarray.getJSONArray(i).getJSONObject(l).getString("工位") + "\n");
                            } catch (Exception e) {
                            }
                            try {
                                sb.append("动作:" + childarray.getJSONArray(i).getJSONObject(l).getString("动作") + "\n");
                            } catch (Exception e) {
                            }
                            try {
                                sb.append("振动速度:" + childarray.getJSONArray(i).getJSONObject(l).getString("振动速度") + "\n");
                            } catch (Exception e) {
                            }
                            try {
                                sb.append("振动幅度:" + childarray.getJSONArray(i).getJSONObject(l).getString("振动幅度") + "\n");
                            } catch (Exception e) {
                            }
                            try {
                                sb.append("联动速度:" + childarray.getJSONArray(i).getJSONObject(l).getString("联动速度") + "\n");
                            } catch (Exception e) {
                            }
                            try {
                                sb.append("时间:" + childarray.getJSONArray(i).getJSONObject(l).getString("时间") + " min");
                            } catch (Exception e) {
                            }
                            obj.setStatusName(sb.toString());
                            twoList.add(obj);
                        }
                        group.setTwoList(twoList);
                        oneList.add(group);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }


    }

    private void getParamsTemperature(JSONObject res) {
        item_TempList.clear();
        try {
            TempItem tempItem;
            JSONArray temperarray = res.getJSONArray("paramstemperature");
            int length = temperarray.length() / 2;
            for (int i = 0; i < length; i++) {
                tempItem = new TempItem();
                tempItem.setNumber(i + 1 + "");
                tempItem.setTargetTemp(temperarray.get(i).toString());
                if (Boolean.parseBoolean(temperarray.get(i + length).toString()))
                    tempItem.setSwitchStatus("开");
                else
                    tempItem.setSwitchStatus("关");
                item_TempList.add(tempItem);
            }
            tempAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < item_TempList.size(); i++) {
            try {
                item_TempList.get(i).setCurrentTemp(res.getString("工位" + (i + 1)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        try {
            switch (res.getString("machinetype")) {
                case "pnae16":
//                    try{
//                        JSONArray temperarray = res.getJSONArray("paramstemperature");
//                        targettmp1.setText(temperarray.get(0)+" ℃");
//                        targettmp2.setText(temperarray.get(1)+ " ℃");
//                        targettmp3.setText(temperarray.get(2)+" ℃");
//                        targettmp4.setText(temperarray.get(3)+ " ℃");
//                        if(temperarray.getBoolean(5)){
//                            switch1.setText("开");
//                        }else{
//                            switch1.setText("关");
//                        }
//                        if(temperarray.getBoolean(6)){
//                            switch2.setText("开");
//                        }else{
//                            switch2.setText("关");
//                        }
//                        if(temperarray.getBoolean(7)){
//                            switch3.setText("开");
//                        }else{
//                            switch3.setText("关");
//                        }
//                        if(temperarray.getBoolean(8)){
//                            switch4.setText("开");
//                        }else{
//                            switch4.setText("关");
//                        }
//                    }catch (Exception e){}
                    break;
                case "pnae32":
//                    try{
//                        JSONArray temperarray = res.getJSONArray("paramstemperature");
//                        targettmp1.setText(temperarray.get(0)+" ℃");
//                        targettmp2.setText(temperarray.get(1)+ " ℃");
//                        targettmp3.setText(temperarray.get(2)+" ℃");
//                        targettmp4.setText(temperarray.get(3)+ " ℃");
//                        if(temperarray.getBoolean(8)){
//                            switch1.setText("开");
//                        }else{
//                            switch1.setText("关");
//                        }
//                        if(temperarray.getBoolean(9)){
//                            switch2.setText("开");
//                        }else{
//                            switch2.setText("关");
//                        }
//                        if(temperarray.getBoolean(10)){
//                            switch3.setText("开");
//                        }else{
//                            switch3.setText("关");
//                        }
//                        if(temperarray.getBoolean(11)){
//                            switch4.setText("开");
//                        }else{
//                            switch4.setText("关");
//                        }
//                    }catch (Exception e){}
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initView(View view) {
        imglayout = (LinearLayout) view.findViewById(R.id.imglayout);
        qualitylinearlayout = (LinearLayout) view.findViewById(R.id.qualitylinearlayout);
        img = (DragZoomLocationView) view.findViewById(R.id.img);
        statusmode = (Button) view.findViewById(R.id.statusmode);
        if (Util.getConfig(getContext(), "statusmode", "paramsmode").equals("paramsmode")) {
            statusmode.setText("数据模式");
            qualitylinearlayout.setVisibility(View.GONE);
        } else {
            statusmode.setText("图像模式");
            qualitylinearlayout.setVisibility(View.VISIBLE);
        }
        statusmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusmode.getText().toString().equals("数据模式")) {
                    statusmode.setText("图像模式");
                    qualitylinearlayout.setVisibility(View.VISIBLE);
                    Util.setConfig(getContext(), "statusmode", "imagesmode");
                } else {
                    statusmode.setText("数据模式");
                    qualitylinearlayout.setVisibility(View.GONE);
                    Util.setConfig(getContext(), "statusmode", "paramsmode");
                }
                getActivity().invalidateOptionsMenu();
                Runtime_Acyivity.handler.sendEmptyMessage(StaticVar.SYNCHRONOUS_REQUEST);
            }
        });
        quality = (CheckBox) view.findViewById(R.id.quality);
        quality.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Util.setConfig(getContext(), "quality", "HD");
                else
                    Util.setConfig(getContext(), "quality", "SD");


            }
        });
        quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runtime_Acyivity.handler.sendEmptyMessage(StaticVar.SYNCHRONOUS_REQUEST);
            }
        });
        if (Util.getConfig(getContext(), "quality", "SD").equals("SD")) {
            quality.setChecked(false);
        } else {
            quality.setChecked(true);
        }

        defaultview = (TextView) view.findViewById(R.id.defaultview);


        tv_config = (TextView) view.findViewById(R.id.tv_config_rt);
        processtitle = (TextView) view.findViewById(R.id.processtitle);

        tempListview = (RecyclerView) view.findViewById(R.id.templistview);
        tempAdapter = new TempAdapter(item_TempList, getContext());
        lm = new LinearLayoutManager(getContext());
        tempListview.setLayoutManager(lm);
        tempListview.setAdapter(tempAdapter);

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandlist);
        expandableListView.setGroupIndicator(null); // 去掉默认带的箭头

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // TODO Auto-generated method stub
                return true;
            }
        });
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


    public void sendMsg(final String content) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
//                    Object[] msg = new Object[3];
//                    msg[0] = MainActivity.USERNAME;
//                    msg[1] = mParam1;
//                    msg[2] = content;
//                    MainActivity.client.sendFlag("SendCmd");
//                    for (int i = 0; i < (int) msg.length; i++) {
//                        try {
//                            MainActivity.client.Client_out.writeObject(msg[i]);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }

                    MessageBean messageBean = new MessageBean();
                    messageBean.setAction("SendCmd");
                    UserBean from = new UserBean();
                    from.setId(Client.getInstance().getUserID());
                    UserBean to = new UserBean();
                    to.setId(mParam1);
                    ContentBean contentBean = new ContentBean();
                    contentBean.setStringcontent(content);
                    messageBean.setFrom(from);
                    messageBean.setTo(to);
                    messageBean.setContent(contentBean);
                    Client.getInstance().sendRquestForResponse(messageBean, false, new RequestCallBack<MessageBean>() {
                        @Override
                        public void Response(MessageBean messageBean) {
                            if (messageBean.getResult().getCode() == 1) {
                                handler.sendEmptyMessage(4);
                            } else {
                                handler.sendEmptyMessage(2);
                            }
                        }
                    });

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
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
