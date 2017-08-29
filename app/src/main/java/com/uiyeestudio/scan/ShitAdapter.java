package com.uiyeestudio.scan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uiyeestudio.scan.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Michael on 2017/8/9.
 */

public class ShitAdapter extends BaseAdapter {

    private ArrayList<Map<String, Object>> mData;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public ShitAdapter(Context context, ArrayList<Map<String, Object>> data) {
        this.mContext = context;
        this.mData = data;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.mData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item, null);
            viewHolder.title = convertView.findViewById(R.id.list_item_title);
            viewHolder.author = convertView.findViewById(R.id.list_item_author);
            viewHolder.type = convertView.findViewById(R.id.list_item_type);
            viewHolder.year = convertView.findViewById(R.id.list_item_year);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // viewHolder.author.setText();

        return convertView;
    }

    private static class ViewHolder {
        TextView title, author, type, year;
    }
}
