package com.chowdhuryfahim.bloodinquiry.CustomDesigns;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chowdhuryfahim.bloodinquiry.R;
import com.chowdhuryfahim.bloodinquiry.models.OrgProfile;
import java.util.ArrayList;

/*
 * Created by  Fahim on 3/30/2017.
 */

public class OrgListViewHolder extends CustomListAdapter<OrgProfile>{
    private LayoutInflater inflater;

    public OrgListViewHolder(Context context, ArrayList<OrgProfile> dataSet) {
        super(dataSet);
        inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Holder holder;
        if(view==null){
            holder = new Holder();
            view = inflater.inflate(R.layout.org_list_item, null);
            holder.nameText = (TextView) view.findViewById(R.id.orgNameTextView);
            holder.areaText = (TextView) view.findViewById(R.id.orgAreaTextView);
            holder.phoneText = (TextView) view.findViewById(R.id.orgPhoneTextView);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.nameText.setText(dataSet.get(position).name);
        holder.areaText.setText(dataSet.get(position).district);
        holder.phoneText.setText(dataSet.get(position).phone);

        return view;
    }

    class Holder{
        TextView nameText;
        TextView areaText;
        TextView phoneText;

    }
}
