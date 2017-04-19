package com.org.ehome.adapter;

import java.util.List;
import java.util.Map;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.org.ehome.R;

public class MachineAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private FragmentActivity activity;
    private List<Map<String, String>> data;
    private int itemListview;

    public MachineAdapter(FragmentActivity activity,
                          List<Map<String, String>> data, int itemListview) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.data = data;
        this.itemListview = itemListview;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(itemListview, null);
            viewHolder.itemText = (TextView) convertView.findViewById(R
                    .id.ItemText);
            viewHolder.itemImage = (ImageView) convertView.findViewById(R.id.ItemImage);
            viewHolder.Online = "";
            viewHolder.MachineID = "";
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemText.setText(data.get(position).get("MachineNickName"));
        viewHolder.MachineID = data.get(position).get("MachineID");
        viewHolder.CreateTime = data.get(position).get("CreateTime");
        if (data.get(position).get("Online").toString().equals("是")) {

            viewHolder.itemImage.setImageBitmap(getBitmap(R.drawable.machineonline, 5));
            viewHolder.Online = "是";
        } else {
            viewHolder.itemImage.setImageBitmap(getBitmap(R.drawable.machineoffline, 5));
            viewHolder.Online = "否";
        }
        return convertView;
    }

    /**
     * 加载图片
     *
     * @param resId        资源ID
     * @param inSampleSize 宽高缩放倍数
     * @return
     */
    private Bitmap getBitmap(int resId, int inSampleSize) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        bitmap = BitmapFactory.decodeResource(activity.getResources(), resId, options);
        return bitmap;
    }

    public class ViewHolder {
        public TextView itemText;        //设备名称
        public ImageView itemImage;        //状态图片
        public String MachineID;        //设备ID
        public String Online;            //设备在线状态
        public String CreateTime;            //设备在线状态
    }

}
