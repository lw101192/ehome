package com.org.ehome.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.org.ehome.R;
import com.org.ehome.Interface.RecycleViewOnItemClickListener;
import com.org.ehome.activities.MainActivity;
import com.org.ehome.activities.Runtime_Acyivity;
import com.org.ehome.adapter.MyItemRecyclerViewAdapter;
import com.org.ehome.bean.HistroyItem;
import com.org.ehome.bean.StaticVar;
import com.org.ehome.callback.HistroyItemTouchHelperCallback;
import com.org.ehome.util.DataBaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HistroyFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyItemRecyclerViewAdapter adapter;
    private List<HistroyItem> items = new ArrayList<>();
    private List<String> ids = new ArrayList<>();
    private ItemTouchHelper itemTouchHelper;
    private RecyclerView recyclerView;
    public static Handler handler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistroyFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HistroyFragment newInstance(int columnCount) {
        HistroyFragment fragment = new HistroyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }


    }

    /**
     * 获取item数据,并更新
     */
    private void getdata() {
        items.clear();
        if (getContext() == null)
            return;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext(), MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        for (int i = 0; i < ids.size(); i++) {
            Cursor cursor = sqliteDatabase.query("histroy", new String[]{"id", "nickname", "createtime", "content", "isread", "type", "show"}, "id=?", new String[]{ids.get(i)}, null, null, "createtime");
            cursor.moveToLast();
//            Map map = new HashMap<String, String>();
//            map.put("MachineNickName", cursor.getString(cursor.getColumnIndex("nickname")));
//            map.put("MachineID", cursor.getString(cursor.getColumnIndex("id")));
//            map.put("CreateTime", cursor.getString(cursor.getColumnIndex("createtime")));
//            map.put("Online", "否");
//            data.add(map);
            HistroyItem item = new HistroyItem();
            item.setId(cursor.getString(cursor.getColumnIndex("id")));
            item.setNickname(getNickName(cursor.getString(cursor.getColumnIndex("id"))));
            item.setLasttime(cursor.getString(cursor.getColumnIndex("createtime")));
            item.setContent(cursor.getString(cursor.getColumnIndex("content")));
            item.setIsread(cursor.getString(cursor.getColumnIndex("isread")));
            item.setType(cursor.getString(cursor.getColumnIndex("type")));
            item.setShow(cursor.getString(cursor.getColumnIndex("show")));
            item.setUnreadnum(getUnreadNum(ids.get(i)));
            items.add(item);
            cursor.close();
        }
        sqliteDatabase.close();
        adapter.notifyDataSetChanged();
//        if(items.size()==0){
//            recyclerView.setBackgroundResource(R.drawable.nohistroy_bg);
//        }else{
//            recyclerView.setBackgroundResource(0);
//        }
    }

    /**
     * 获取设备名称
     *
     * @param id 设备ID
     * @return
     */
    private String getNickName(String id) {
        String nickName = null;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext(), MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("select nickname from mymachine where id = ?", new String[]{id});
        if (cursor.moveToNext()) {
            nickName = cursor.getString(0);
        }
        cursor.close();
        sqliteDatabase.close();
        return nickName;
    }


    /**
     * 获取未读消息数目
     *
     * @param id 设备ID
     * @return
     */
    private int getUnreadNum(String id) {
        int count = 0;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext(), MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("select * from histroy where (id = ? and isread=?)", new String[]{id, "false"});
        count = cursor.getCount();
        cursor.close();
        sqliteDatabase.close();
        return count;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyItemRecyclerViewAdapter(items, getContext());
            adapter.setOnItemClickListener(new RecycleViewOnItemClickListener() {
                @Override
                public void onItemclick(View view, Object data) {
                    clearUnReadNum(((MyItemRecyclerViewAdapter.ViewHolder) data).id);
                    Intent intent = new Intent();
                    intent.setClass(getContext(), Runtime_Acyivity.class);
                    intent.putExtra("toID", ((MyItemRecyclerViewAdapter.ViewHolder) data).id);
                    intent.putExtra("nicknname", ((MyItemRecyclerViewAdapter.ViewHolder) data).nickname.getText().toString());
                    intent.putExtra("from", "histroy");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_right,
                            R.anim.out_to_left);
                }
            });
            recyclerView.setAdapter(adapter);
        }
        ItemTouchHelper.Callback callback = new HistroyItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        getids();
        getdata();
        initHandler();
        return view;
    }

    /**
     * 移除未读消息
     *
     * @param id 设备ID
     */
    private void clearUnReadNum(String id) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext(), MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("isread", "true");
        sqliteDatabase.update("histroy", contentValues, "id=?", new String[]{id});
        sqliteDatabase.close();
        handler.sendEmptyMessage(StaticVar.REFRESH_HISTROY);
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case StaticVar.REFRESH_HISTROY:
                        getids();
                        getdata();
                        break;
                    case StaticVar.CLEAR_HISTROY:
                        items.clear();
                        adapter.notifyDataSetChanged();
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext(), MainActivity.DB_NAME);
                        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
                        sqLiteDatabase.delete("histroy", null, null);
                        sqLiteDatabase.close();
                        break;
                    case StaticVar.CLEAR_UNREAD:
                        for (int i = 0; i < items.size(); i++)
                            clearUnReadNum(items.get(i).getId());

                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };
    }

    /**
     * 获取设备ID列表
     *
     * @return
     */
    private List<String> getids() {
        ids.clear();
        if (getActivity() == null)
            return null;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext(), MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("select distinct id from histroy", null);
        while (cursor.moveToNext()) {
            ids.add(cursor.getString(0));
        }
        return ids;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
    }
}
