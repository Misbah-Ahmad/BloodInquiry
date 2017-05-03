package com.chowdhuryfahim.bloodinquiry.CustomDesigns;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.OrgFields;
import com.chowdhuryfahim.bloodinquiry.LoginPreference;
import com.chowdhuryfahim.bloodinquiry.models.DonorProfile;
import com.chowdhuryfahim.bloodinquiry.R;
import com.chowdhuryfahim.bloodinquiry.models.OrgProfile;
import java.util.ArrayList;

/*
 *
 * Created by Fahim on 3/24/2017.
 *
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.DonorViewHolder> {

    private ArrayList<DonorProfile> data = new ArrayList<>();
    private LayoutInflater inflater;
    public Context context;
    private AlertDialog dialog;

    public RecyclerAdapter(ArrayList<DonorProfile> data, Context context){
        this.data = data;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public DonorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);

        return new DonorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DonorViewHolder holder, final int position) {
            int lastPosition = -1;
            Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.list_to_up : R.anim.list_to_down);
            holder.itemView.startAnimation(animation);
            //lastPosition = position;

            holder.nameText.setText(data.get(position).getDonorName());
            String str = data.get(position).getDonorBloodGroup()+ "(ve), ";
            str += (data.get(position).donorStatus==1) ? "Available" : "Unavailable";
            holder.groupText.setText(str);
            holder.locationText.setText(data.get(position).getDonorLocation()+", "+data.get(position).getDonorDistrict());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = "";
                    if(data.get(holder.getAdapterPosition()).donorGender.toLowerCase().equals("female") && !(data.get(holder.getAdapterPosition()).donorOrganization.toLowerCase().equals("none")) && (new LoginPreference(context).getStringPreferences(LoginPreference.SEARCH_ORGANIZATION).equals("none"))) {
                        OrgProfile profile = new DataBaseHelper(context).getOrgProfile(OrgFields.ORG_USERNAME_FIELD + "=?", new String[]{data.get(holder.getAdapterPosition()).donorOrganization});
                        if(profile==null){
                            Toast.makeText(context, "Admin not found!", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            phone = profile.phone;
                        }
                    } else {
                        phone = data.get(holder.getAdapterPosition()).donorPhone;
                    }

                    Uri uri = Uri.parse("tel:"+phone);

                    final Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Call Confirmation")
                            .setMessage("Are you sure, you want to make a call?")
                            .setCancelable(false);
                    builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(dialog!=null && dialog.isShowing())
                                dialog.dismiss();
                            context.startActivity(callIntent);
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(dialog!=null && dialog.isShowing())
                                dialog.dismiss();
                        }
                    });

                    dialog = builder.create();


                    if(dialog.getWindow()!=null)
                        dialog.getWindow().getAttributes().windowAnimations = R.style.LoginDialogAnimation;
                    dialog.show();
                }
            });
    }

    @Override
    public void onViewDetachedFromWindow(DonorViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class DonorViewHolder extends RecyclerView.ViewHolder {

        TextView nameText;
        TextView groupText;
        TextView locationText;
        CardView cardView;
        DonorViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.donorListCard);
            nameText = (TextView) itemView.findViewById(R.id.donorListNameText);
            groupText = (TextView) itemView.findViewById(R.id.donorListBloodGroupText);
            locationText = (TextView) itemView.findViewById(R.id.donorListLocationText);
        }

    }

}
