package com.org.ehome.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.org.ehome.R;
import com.org.ehome.bean.TempItem;

import java.util.List;


/**
 * Created by liuwei on 2016/10/26.
 */
public class TempAdapter extends RecyclerView.Adapter<TempAdapter.ViewHolder> {

    private List<TempItem> items;
    private Context context;
    private LayoutInflater inflater;

    public TempAdapter(List<TempItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public TempAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.item_runtime_templistview, null));
    }

    @Override
    public void onBindViewHolder(TempAdapter.ViewHolder holder, int position) {
        holder.number.setText(items.get(position).getNumber());
        holder.currentTemp.setText(items.get(position).getCurrentTemp());
        holder.targetTemp.setText(items.get(position).getTargetTemp());
        holder.switchStatus.setText(items.get(position).getSwitchStatus());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView number;
        private TextView currentTemp;
        private TextView targetTemp;
        private TextView switchStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.number);
            currentTemp = (TextView) itemView.findViewById(R.id.currenttemp);
            targetTemp = (TextView) itemView.findViewById(R.id.targettemp);
            switchStatus = (TextView) itemView.findViewById(R.id.switchstatus);
        }
    }
}
