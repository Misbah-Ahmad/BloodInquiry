package com.chowdhuryfahim.bloodinquiry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.OrgListViewHolder;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.OrgFields;
import com.chowdhuryfahim.bloodinquiry.models.OrgProfile;
import com.chowdhuryfahim.bloodinquiry.models.OrgReply;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class OrgListActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener, AdapterView.OnItemClickListener{

    ListView listView;
    OrgListViewHolder adapter;
    ArrayList<OrgProfile> dataSet;
    GsonRequestIn gsonRequestIn;
    LoginPreference loginPreference;
    ProgressDialog pd;
    android.support.design.widget.FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_list);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        fab = (FloatingActionButton) findViewById(R.id.OrgRefreshButton);

        loginPreference = new LoginPreference(this);
        listView = (ListView) findViewById(R.id.orgListView);
        dataSet = new ArrayList<>();



        if(!(loginPreference.getBooleanPreferences(LoginPreference.ORG_LOADED))){
            loadFromServer();
        } else {
            loadFromLocal();
        }

    }

    public void refreshList(View view){
        if(!(StaticMethods.isOnline(this))){
            showToast("No Internet Connection!");
        } else {
            pd.show();
            loadFromServer();
        }
    }

    void loadFromServer(){
        HashMap<String, String> params = new HashMap<>();
        gsonRequestIn = new GsonRequestIn(RequestTag.GET_ORG_URL, OrgReply.class, params, this, this);
        VolleyHelper.getInstance(this).addToRequestQueue(gsonRequestIn);
    }

    @Override
    public void onResponse(Object response) {
        OrgReply reply = (OrgReply) response;
        pd.dismiss();
        boolean loaded=loginPreference.getBooleanPreferences(LoginPreference.ORG_LOADED);


        if(reply.success==1 && reply.orgProfiles!=null){
            if(loaded){
                clearDB();
            }

            if(initializeDB(reply.orgProfiles)==1){
                loginPreference.setBooleanPreferences(LoginPreference.ORG_LOADED, true);
                loadFromLocal();
            } else {
                loginPreference.setBooleanPreferences(LoginPreference.ORG_LOADED, false);
                showToast(reply.message);
                startActivity(new Intent(OrgListActivity.this, WelcomeActivity.class));
                finish();
            }
        } else {
            showToast(reply.message);
            startActivity(new Intent(OrgListActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        pd.dismiss();
        showToast("Connection Error!");
        startActivity(new Intent(OrgListActivity.this, WelcomeActivity.class));
        finish();
    }




    int initializeDB(ArrayList<OrgProfile> data){
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        return dbHelper.insertAllOrg(data);
    }

    void loadFromLocal(){
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        dataSet = dbHelper.getOrg(null, null);

        if(pd.isShowing()){
            pd.dismiss();
        }

        if(dataSet==null){
            showToast("No Data Found!");
            startActivity(new Intent(OrgListActivity.this, WelcomeActivity.class));
            finish();
        }
        adapter = new OrgListViewHolder(this, dataSet);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    int clearDB(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        return dataBaseHelper.dropTable(OrgFields.ORG_TABLE_NAME);

    }

    void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent  = new Intent(OrgListActivity.this, OrgProfileActivity.class);

        intent.putExtra(OrgFields.ORG_USERNAME_FIELD, dataSet.get(i).username);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }



}
