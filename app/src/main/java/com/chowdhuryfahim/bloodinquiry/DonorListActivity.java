package com.chowdhuryfahim.bloodinquiry;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.RecyclerAdapter;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataFields;
import com.chowdhuryfahim.bloodinquiry.models.DonorProfile;
import com.chowdhuryfahim.bloodinquiry.models.Products;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class DonorListActivity extends AppCompatActivity implements Response.Listener,
                                                Response.ErrorListener,
                                                View.OnClickListener, SearchView.OnQueryTextListener {

    android.support.design.widget.FloatingActionButton fab;
    RecyclerView.ItemAnimator animator;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    LoginPreference loginPreference;
    GsonRequestIn gsonRequestIn;
    String msg;
    boolean dataError = false;
    ProgressDialog pd;

    SearchView searchView;
    ArrayList<DonorProfile> searchResult;
    ArrayList<DonorProfile> tempResult;
    MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_list);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        loginPreference = new LoginPreference(this);


        fab = (FloatingActionButton) findViewById(R.id.refreshButton);
        fab.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.donorRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        animator = new DefaultItemAnimator();
        animator.setAddDuration(1500);
        animator.setMoveDuration(1500);
        animator.setRemoveDuration(1500);
        recyclerView.setItemAnimator(animator);

        if (!(loginPreference.getBooleanPreferences(LoginPreference.LOADED))) {
            loadFromServer();
        } else {
            setFilters();
        }

        if (dataError) {
            errorHandler(msg);
            gotoParent();
            //startActivity(new Intent(DonorListActivity.this, DonorSearchActivity.class));
        }

    }


    @Override
    public void onClick(View view) {
        OnlineChecker onlineChecker = new OnlineChecker(this);
        if (onlineChecker.isOnline()) {
            pd.show();
            loadFromServer();
        } else {
            errorHandler("No Internet Connection!");
        }
    }

    //Retrieve Data from Web Server
    public void loadFromServer() {
        HashMap<String, String> params = new HashMap<>();
        params.put("district", "all");
        params.put("blood_group", "all");
        //Log.d("Loading From", "server");
        gsonRequestIn = new GsonRequestIn(RequestTag.GET_DONOR_URL, Products.class, params, this, this);
        VolleyHelper.getInstance(this).addToRequestQueue(gsonRequestIn);
    }


    //Called when a successful network call is happened
    @Override
    public void onResponse(Object response) {
        Products products = (Products) response;

        //check if request is successful and list is returned
        if (products.donorProfiles == null) {
            msg = products.message;
            dataError = true;
        } else {
            msg = products.message;
            dataError = false;

            //if local DB was initialized before, then delete and insert new data from server
            if (loginPreference.getBooleanPreferences(LoginPreference.LOADED)) {
                //if dropping table is successful
                if (clearLocalDB() == 1) {
                    //if initialization of local DB with new data is successful
                    if ((initializeDB(products.donorProfiles)) == 1) {

                        setFilters();
                    } else {
                        dataError = true;
                        errorHandler("Database Error!");
                        loginPreference.setBooleanPreferences(LoginPreference.LOADED, false);
                        pd.dismiss();
                        gotoParent();
                        //startActivity(new Intent(DonorListActivity.this, DonorSearchActivity.class));
                    }
                } else {
                    pd.dismiss();
                    errorHandler("Cannot Refresh the List");
                }
            } else if (initializeDB(products.donorProfiles) == 1) { //if local DB was not initialized before, this block will be executed
                loginPreference.setBooleanPreferences(LoginPreference.LOADED, true);
                setFilters();

            } else {
                dataError = true;
                msg = "Database Error!";
            }
        }
    }


    //Called when an error is occurred during a network call
    @Override
    public void onErrorResponse(VolleyError error) {

        if (error.networkResponse == null) {
            dataError = true;
            pd.dismiss();
            errorHandler("Connection Error!");
        }

    }

    int clearLocalDB() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        return dataBaseHelper.dropTable(DataFields.DONOR_TABLE_NAME);

    }

    //Initialize and Inserts Data into Local Database
    int initializeDB(ArrayList<DonorProfile> profiles) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        return dataBaseHelper.insertRows(profiles);
    }


    //Setting Selection Criteria from Database
    public void setFilters() {
        String selection;
        String[] args;

        String org = loginPreference.getStringPreferences(LoginPreference.SEARCH_ORGANIZATION);
        if (!(org.equals("none"))) {
            selection = DataFields.DONOR_ORGANIZATION_FIELD + "=?";
            args = new String[]{org};
        } else {
            selection = DataFields.DONOR_BLOODGROUP_FIELD + "=? AND " + DataFields.DONOR_STATUS_FIELD + "=? AND " + DataFields.DONOR_DISTRICT_FIELD + "=?";
            args = new String[]{loginPreference.getStringPreferences(LoginPreference.SEARCH_BLOOD_GROUP), "1", loginPreference.getStringPreferences(LoginPreference.SEARCH_DISTRICT)};
        }

        loadFromLocal(selection, args);

    }

    //Retrieve data from Local Database
    void loadFromLocal(String selection, String[] args) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        searchResult = dataBaseHelper.getRowsByGroupDist(selection, args);
        populateData(searchResult);
    }


    //Populate Data to the List
    void populateData(ArrayList<DonorProfile> profiles) {
        if (pd != null && pd.isShowing())
            pd.dismiss();

        if (profiles == null || profiles.size() < 1) {

            errorHandler("No Data Found!");
            gotoParent();
            return;
        }

        recyclerAdapter = new RecyclerAdapter(profiles, this);
        recyclerView.setAdapter(recyclerAdapter);

    }


    void errorHandler(String error) {
        this.msg = error;
        Toast.makeText(DonorListActivity.this, error, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_search_menu, menu);
        item = menu.findItem(R.id.search_field);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) item.getActionView();


        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        //searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }


    public void searchQuery(@NonNull String s) {

        tempResult = new ArrayList<>();

        DonorProfile profile;
        if (searchResult != null) {
            for (int i = 0; i < searchResult.size(); i++) {
                profile = searchResult.get(i);
                if (profile.donorName.toLowerCase().contains(s) || profile.donorDistrict.toLowerCase().contains(s) || profile.donorLocation.toLowerCase().contains(s)) {
                    tempResult.add(profile);
                }
            }
        }

        if (tempResult != null && tempResult.size() > 0 && tempResult.size() != searchResult.size()) {
            populateData(tempResult);
        }


    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchView.clearFocus();
        return false;
    }


    @Override
    public boolean onQueryTextChange(String s) {
        if (s == null || s.length() < 1) populateData(searchResult);
        else searchQuery(s.toLowerCase());

        return true;
    }


    @Override
    public void onBackPressed() {
        InputMethodManager inputMethodManager  = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isActive()){
            inputMethodManager.hideSoftInputFromWindow(new View(this).getWindowToken(),0);
        } else {
            gotoParent();
        }
    }

    void gotoParent() {
        if (!(loginPreference.getStringPreferences(LoginPreference.SEARCH_ORGANIZATION).equals("none"))) {
            startActivity(new Intent(DonorListActivity.this, WelcomeActivity.class));
        } else {
            startActivity(new Intent(DonorListActivity.this, DonorSearchActivity.class));
        }
    }

}
