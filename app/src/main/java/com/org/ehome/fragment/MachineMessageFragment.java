package com.org.ehome.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.org.ehome.R;
import com.org.ehome.activities.MainActivity;
import com.org.ehome.adapter.MessageAdapter;
import com.org.ehome.bean.MessageItem;
import com.org.ehome.bean.StaticVar;
import com.org.ehome.callback.HistroyItemTouchHelperCallback;
import com.org.ehome.util.DataBaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MachineMessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MachineMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MachineMessageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ID = "param1";
    private static final String ARG_NICKNAME = "param2";

    // TODO: Rename and change types of parameters
    private String mID;
    private String mNickName;

    private OnFragmentInteractionListener mListener;
    private View view;
    public static Handler handler;
    private RecyclerView messagelv;
    private MessageAdapter adapter;
    private ItemTouchHelper itemTouchHelper;
    private List<MessageItem> messagelist = new ArrayList();

    public MachineMessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MachineMessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MachineMessageFragment newInstance(String param1, String param2) {
        MachineMessageFragment fragment = new MachineMessageFragment();
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
            mID = getArguments().getString(ARG_ID);
            mNickName = getArguments().getString(ARG_NICKNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_machine_message, container, false);
        initView(view);
        initHandler();
        return view;
    }

    private void initView(View view) {
        messagelv = (RecyclerView) view.findViewById(R.id.messge_lv);
        adapter = new MessageAdapter(mID, messagelist, getContext());
        messagelv.setLayoutManager(new LinearLayoutManager(getContext()));
        messagelv.setAdapter(adapter);
//        messagelv.setItemAnimator(new SlideInOutLeftItemAnimator(messagelv));
        ItemTouchHelper.Callback callback = new HistroyItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(messagelv);
        getMessageList();
    }

    private List<MessageItem> getMessageList() {
        messagelist.clear();
        MessageItem item = null;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext(), MainActivity.DB_NAME);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from histroy where id=? order by createtime", new String[]{mID});
        while (cursor.moveToNext()) {
            item = new MessageItem(cursor.getString(cursor.getColumnIndex("content")), cursor.getString(cursor.getColumnIndex("createtime")));
            messagelist.add(item);
        }
        cursor.close();
        sqLiteDatabase.close();
        adapter.notifyDataSetChanged();
        return messagelist;
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case StaticVar.REFRESH_MESSAGE:
                        getMessageList();
                        messagelv.smoothScrollToPosition(messagelist.size());
                        break;
                }

            }
        };

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
