package com.chowdhuryfahim.bloodinquiry;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DistrictFields;
import com.chowdhuryfahim.bloodinquiry.models.OrgReply;
import com.chowdhuryfahim.bloodinquiry.models.Products;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    LoginPreference loginPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        loginPreference = new LoginPreference(this);

        new async().execute();
    }

    private class async extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            dataBaseHelper = new DataBaseHelper(SplashActivity.this);
            if(!(loginPreference.getBooleanPreferences(LoginPreference.IS_DISTRICT_LOADED))){
                dataBaseHelper.dropTable(DistrictFields.DISTRICT_TABLE_NAME);
                if(dataBaseHelper.insertDistrict()==1){
                    loginPreference.setBooleanPreferences(LoginPreference.IS_DISTRICT_LOADED, true);
                }
            }

            if(!(loginPreference.getBooleanPreferences(LoginPreference.LOADED)) && StaticMethods.isOnline(SplashActivity.this)){

                loadDonors();
                loadOrgs();
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                finish();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o){
                super.onPostExecute(o);

            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

            finish();
        }
    }

    void loadDonors(){
        HashMap<String, String> params = new HashMap<>();
        params.put("blood_group", "all");
        params.put("district", "all");
        GsonRequestIn requestIn = new GsonRequestIn(RequestTag.GET_DONOR_URL, Products.class, params, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Products products = (Products) response;
                if(products!=null && products.success==1 && products.donorProfiles!=null){
                    if((dataBaseHelper.insertRows(products.donorProfiles))==1) {
                        loginPreference.setBooleanPreferences(LoginPreference.LOADED, true);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleyHelper.getInstance(this).addToRequestQueue(requestIn);
    }

    void loadOrgs(){
        HashMap<String, String> params = new HashMap<>();
        GsonRequestIn requestIn = new GsonRequestIn(RequestTag.GET_ORG_URL, OrgReply.class, params, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                OrgReply reply = (OrgReply) response;
                if(reply!=null && reply.success==1 && reply.orgProfiles!=null){
                    if((dataBaseHelper.insertAllOrg(reply.orgProfiles))==1) {
                        loginPreference.setBooleanPreferences(LoginPreference.ORG_LOADED, true);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleyHelper.getInstance(this).addToRequestQueue(requestIn);
    }

}
