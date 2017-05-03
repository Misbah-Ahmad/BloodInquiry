package com.chowdhuryfahim.bloodinquiry.CustomDesigns;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/*
 * Created by Fahim on 8/25/2016.
 */
public abstract class CustomListAdapter<D> extends BaseAdapter {

    ArrayList<D> dataSet;

    public CustomListAdapter(ArrayList<D> dataSet){
        this.dataSet = dataSet;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public abstract View getView(int position, View view, ViewGroup viewGroup);// {

//        ViewHolder holder = null;
//        if (view==null){
//            holder = new ViewHolder();

//            view = inflater.inflate(R.layout.custom_list, null);
//            holder.nameText = (TextView) view.findViewById(R.id.nameText);
//            holder.phoneText = (TextView) view.findViewById(R.id.phoneText);
//            view.setTag(holder);
//        }else{
//            holder = (ViewHolder) view.getTag();
//        }
//
//        holder.nameText.setText(nameList.get(position));
//        holder.phoneText.setText(phoneList.get(position));
//
//        return view;
//    }

//    private  class ViewHolder{
//        TextView nameText, phoneText;
//    }
}
