package com.chowdhuryfahim.bloodinquiry.CustomDesigns;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.chowdhuryfahim.bloodinquiry.R;
import java.util.ArrayList;

/*
 * Created and Designed by  Fahim on 12/27/2016.
 * This class is for implementing A Custom Spinner
 * Change Data Set and View Holder as Required
 */

public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private ArrayList<String> dataSet;
    protected Context context;
    private LayoutInflater inflater;

    public CustomSpinnerAdapter(ArrayList<String> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int i) {
        return dataSet.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if(view==null){
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.donor_blood_group_spinner, null);
            viewHolder.itemText = (TextView) view.findViewById(R.id.spinner_item_text);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.itemText.setText(dataSet.get(position));

        return view;
    }

    public int getPositionByItem(String item){
        return  dataSet.indexOf(item);

    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        return getDropDownView(position, view, viewGroup);
    }

    private class ViewHolder {
        TextView itemText;
    }
}