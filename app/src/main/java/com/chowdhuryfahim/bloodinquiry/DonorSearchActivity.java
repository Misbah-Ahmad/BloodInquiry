package com.chowdhuryfahim.bloodinquiry;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.CustomSpinnerAdapter;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataFields;

import java.util.ArrayList;


public class DonorSearchActivity extends AppCompatActivity {

    AppCompatSpinner spinner;
    CustomSpinnerAdapter spinnerAdapter;
    ArrayList<String> districtNames;
    String district;
    LoginPreference loginPreference;
    DataBaseHelper dataBaseHelper;

    String preferredLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_search);



        dataBaseHelper = new DataBaseHelper(this);
        loginPreference = new LoginPreference(this);
        loginPreference.setStringPreference(LoginPreference.SEARCH_ORGANIZATION, "none");
        spinner = (AppCompatSpinner) findViewById(R.id.districtSearchSpinner);
        preferredLocation = loginPreference.getStringPreferences(LoginPreference.USER_PREFERRED_LOCATION);
        setAdapter();

    }


    public void findDonor(View view){

        if(!(loginPreference.getBooleanPreferences(LoginPreference.LOADED)) && !StaticMethods.isOnline(this)){
            Toast.makeText(this, "Connect to the internet for 1st Loading", Toast.LENGTH_SHORT).show();
        } else {
            //TextView groupText = (TextView) view;
            if(district.contains("Select") && !(loginPreference.getBooleanPreferences(LoginPreference.IS_PREFERRED_LOCATION_SET))){
                showToast("Select a district");
                return;
            }

            if(district.contains("Select")){
                district = loginPreference.getStringPreferences(LoginPreference.USER_PREFERRED_LOCATION);
                loginPreference.setStringPreference(LoginPreference.SEARCH_DISTRICT, district);
            } else {
                loginPreference.setStringPreference(LoginPreference.SEARCH_DISTRICT, district);
            }

            String group = view.getContentDescription().toString().toUpperCase();
            Intent intent = new Intent(DonorSearchActivity.this, DonorListActivity.class);
            loginPreference.setStringPreference(LoginPreference.SEARCH_BLOOD_GROUP, group);

            if(loginPreference.getBooleanPreferences(LoginPreference.LOADED)){
                String[] selection = new String[]{DataFields.DONOR_BLOODGROUP_FIELD, DataFields.DONOR_DISTRICT_FIELD, DataFields.DONOR_STATUS_FIELD};
                String[] args = new String[]{group, district, "1"};
                if(dataBaseHelper.countRows(DataFields.DONOR_TABLE_NAME, selection, args)<1){
                    showToast("No Donor was found with "+group+ " in "+district);
                    return;
                }
            }

            //intent.putExtra("GROUP", group);
            //intent.putExtra("DISTRICT", "all");
            //Log.d("GROUP", group);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

        }
    }

    void setAdapter(){
        districtNames = new ArrayList<>();
        districtNames.add("Select District");
        districtNames.addAll(dataBaseHelper.getDistricts());
        spinnerAdapter = new CustomSpinnerAdapter(districtNames, this);
        spinner.setAdapter(spinnerAdapter);

        String choice = loginPreference.getStringPreferences(LoginPreference.SEARCH_DISTRICT);
        if(!(choice.equals("none"))){
            spinner.setSelection(spinnerAdapter.getPositionByItem(choice));
        } else if(!preferredLocation.equals("none")){
            spinner.setSelection(spinnerAdapter.getPositionByItem(preferredLocation));

        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                district = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}

        });
    }



    void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!(loginPreference.getStringPreferences(LoginPreference.SEARCH_DISTRICT).equals("none"))){
            loginPreference.setStringPreference(LoginPreference.SEARCH_DISTRICT, "none");
        }
        startActivity(new Intent(DonorSearchActivity.this, WelcomeActivity.class));
        super.onBackPressed();
    }
}
