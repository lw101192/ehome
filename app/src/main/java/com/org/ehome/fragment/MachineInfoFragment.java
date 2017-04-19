package com.org.ehome.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.org.ehome.R;
import com.org.ehome.activities.MainActivity;
import com.org.ehome.bean.StaticVar;
import com.org.ehome.util.DataBaseHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MachineInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MachineInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MachineInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ID = "ID";
    private static final String ARG_NICKNAME = "NickName";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View view;
    private TextView nickName;
    private TextView id;
    private TextView createTime;
    private TextView lastSynchronizeTime;
    private TextView online;
    public static Handler handler;

    public MachineInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MachineInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MachineInfoFragment newInstance(String param1, String param2) {
        MachineInfoFragment fragment = new MachineInfoFragment();
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
        view = inflater.inflate(R.layout.fragment_machine_info, container, false);
        initView(view);
        initHandler();
        return view;
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case StaticVar.SYNCHRONOUS_SUCCEED:
                        getLastSynchronizeTime();
                        break;
                }
            }
        };

    }

    private void initView(View view) {
        nickName = (TextView) view.findViewById(R.id.nickname);
        id = (TextView) view.findViewById(R.id.id);
        createTime = (TextView) view.findViewById(R.id.createtime);
        lastSynchronizeTime = (TextView) view.findViewById(R.id.lastsynchronizetime);
        online = (TextView) view.findViewById(R.id.online);

        nickName.setText(mParam2);
        id.setText(mParam1);

        createTime.setText(getCreateTime());
        online.setText(getOnline());
        getLastSynchronizeTime();
    }

    /**
     * 从本地数据库获取添加时间
     *
     * @return
     */
    private String getCreateTime() {
        String createtime = "否";
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity(), MainActivity.DB_NAME);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select createtime from mymachine where id=?", new String[]{mParam1});
        cursor.moveToNext();
        createtime = cursor.getString(0);
        cursor.close();
        sqLiteDatabase.close();
        return createtime;
    }

    /**
     * 从本地数据库获取在线状态
     *
     * @return
     */
    private String getOnline() {
        String online = "否";
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity(), MainActivity.DB_NAME);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select online from mymachine where id=?", new String[]{mParam1});
        cursor.moveToNext();
        online = cursor.getString(0);
        cursor.close();
        sqLiteDatabase.close();
        return online;
    }

    /**
     * 获取最近一次同步时间
     */
    private void getLastSynchronizeTime() {
        if (getActivity() == null) {
            return;
        }
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity(), MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("select lastsynchronizetime from mymachine where id=?", new String[]{mParam1});
        if (cursor.moveToNext()) {
            lastSynchronizeTime.setText(cursor.getString(cursor.getColumnIndex("lastsynchronizetime")));
//            System.out.println("lastsynchronizetime"+cursor.getString(cursor.getColumnIndex("lastsynchronizetime")));
        }
        cursor.close();
        sqliteDatabase.close();
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
