package com.org.ehome.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.org.ehome.R;
import com.org.ehome.Interface.ItemTouchMoveListener;
import com.org.ehome.Interface.RecycleViewOnItemClickListener;
import com.org.ehome.activities.MainActivity;
import com.org.ehome.bean.HistroyItem;
import com.org.ehome.util.DataBaseHelper;

import java.util.Collections;
import java.util.List;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> implements ItemTouchMoveListener, View.OnClickListener {

    private final List<HistroyItem> mValues;
    private Context context;
    private RecycleViewOnItemClickListener onItemClickListener;

    public MyItemRecyclerViewAdapter(List<HistroyItem> items, Context context) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_histroy, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.nickname.setText(mValues.get(position).getNickname());
        holder.lasttime.setText(mValues.get(position).getLasttime());
        holder.content.setText(mValues.get(position).getContent());
        holder.unreadnum.setText(mValues.get(position).getUnreadnum() + "");
        if (mValues.get(position).getUnreadnum() <= 0) {
            holder.unreadnum.setBackgroundResource(0);      //移除背景
        } else {
            holder.unreadnum.setBackgroundResource(R.drawable.unreadnum_bg);
        }
        holder.unreadnum.setPadding(0, -5, 0, 0);
        holder.id = mValues.get(position).getId();
        holder.itemview.setTag(holder);

//        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
//
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        // 1.数据交换；2.刷新
        Collections.swap(mValues, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    @Override
    public boolean onItemRemove(int position) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context, MainActivity.DB_NAME);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        sqLiteDatabase.delete("histroy", "id=?", new String[]{mValues.get(position).getId()});
        sqLiteDatabase.close();
        mValues.remove(position);
        notifyItemRemoved(position);
        return true;

    }

    public void setOnItemClickListener(RecycleViewOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public void onClick(View view) {
        if (onItemClickListener != null)

            onItemClickListener.onItemclick(view, view.getTag());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nickname;     //名称
        public final TextView lasttime;     //最新一条消息的时间
        public final TextView content;      //消息内容
        public final TextView unreadnum;        //未读消息数目
        public final View itemview;     //
        public String id;       //消息对应的设备ID

        public ViewHolder(View view) {
            super(view);
            itemview = view;
            nickname = (TextView) view.findViewById(R.id.nickname);
            lasttime = (TextView) view.findViewById(R.id.lasttime);
            content = (TextView) view.findViewById(R.id.content);
            unreadnum = (TextView) view.findViewById(R.id.unreadnum);

        }

    }
}
