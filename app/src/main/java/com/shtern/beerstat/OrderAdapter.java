package com.shtern.beerstat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Алексей on 12.06.2015.
 */
public class OrderAdapter extends BaseAdapter {

    ArrayList<String> mBeerStringList = new ArrayList<>();
    Context mContext;

    public OrderAdapter(ArrayList<String> inlist, Context context) {
        mBeerStringList = inlist;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mBeerStringList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBeerStringList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.beeritemstring, null);
        }
        ((AutoResizeTextView)convertView.findViewById(R.id.beerstringtv)).setText(mBeerStringList.get(position));
        return convertView;
    }
}
