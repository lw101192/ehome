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
import com.org.ehome.activities.MainActivity;
import com.org.ehome.bean.MessageItem;
import com.org.ehome.util.DataBaseHelper;

import java.util.List;


/**
 * Created by liuwei on 2016/8/10.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> implements ItemTouchMoveListener {
    private List<MessageItem> messagelist;
    private Context context;
    private LayoutInflater inflater;
    private String toID;

    public MessageAdapter(String toID, List<MessageItem> messagelist, Context context) {
        this.toID = toID;
        this.messagelist = messagelist;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_message_lv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.content.setText(messagelist.get(position).getContent());
        holder.createtime.setText(messagelist.get(position).getCreatetime());
        holder.itemview.setTag(holder);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public boolean onItemRemove(int position) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context, MainActivity.DB_NAME);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        sqLiteDatabase.delete("histroy", "id=? and createtime=?", new String[]{toID, messagelist.get(position).getCreatetime()});
        sqLiteDatabase.close();
        messagelist.remove(position);
        notifyItemRemoved(position);
        return false;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView content;        //item内容
        public TextView createtime;     //创建时间
        public View itemview;

        public ViewHolder(View itemView) {
            super(itemView);
            itemview = itemView;
            content = (TextView) itemView.findViewById(R.id.content);
            createtime = (TextView) itemView.findViewById(R.id.createtime);
        }
    }

}
