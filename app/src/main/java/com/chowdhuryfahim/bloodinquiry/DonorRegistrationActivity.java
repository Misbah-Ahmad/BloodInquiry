package com.chowdhuryfahim.bloodinquiry;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.CustomSpinnerAdapter;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataFields;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.OrgFields;
import com.chowdhuryfahim.bloodinquiry.models.DonorProfile;
import com.chowdhuryfahim.bloodinquiry.models.OrgProfile;
import com.chowdhuryfahim.bloodinquiry.models.OrgReply;
import com.chowdhuryfahim.bloodinquiry.models.Products;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class DonorRegistrationActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener{

    int orgPos = 0;

    ProgressDialog pd;
    EditText nameEditText;
    EditText passEditText;
    EditText phoneEditText;
    EditText ageEditText;
    EditText locationEditText;
    AppCompatAutoCompleteTextView autoComplete;

    TextInputLayout passwordInputLayout;
    TextInputLayout phoneInputLayout;

    RadioGroup genderRadioGroup;
    RadioGroup statusRadioGroup;
    RadioButton radioButton;
    RadioButton statusRadioButton;

    String bloodGroup;
    String organization;
    Spinner bloodGroupSpinner;
    Spinner orgSpinner;
    ArrayList<String> bloodGroups;
    ArrayList<String> orgs;
    CustomSpinnerAdapter bloodAdapter;
    CustomSpinnerAdapter orgAdapter;
    OnlineChecker onlineChecker;
    DataBaseHelper dataBaseHelper;
    LoginPreference loginPreference;

    ArrayList<String> orgUsername;
    ArrayList<String> dists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration);

        loginPreference = new LoginPreference(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        pd = new ProgressDialog(this);

        dataBaseHelper = new DataBaseHelper(this);
        onlineChecker =  new OnlineChecker(this);

        initViews();
        setDistrictEditText();
        initData(null);

        //Gather Organization Spinner from online or local database
        if(onlineChecker.isOnline()){
            gatherOrg();
        } else if (loginPreference.getBooleanPreferences(LoginPreference.ORG_LOADED)) {
            dataBaseHelper = new DataBaseHelper(this);
            initData(dataBaseHelper.getOrg(null, null));
        } else if(!(loginPreference.getBooleanPreferences(LoginPreference.ORG_LOADED))){
            showToast("Connect to the Internet");
            startActivity(new Intent(DonorRegistrationActivity.this, WelcomeActivity.class));
        }
    }

    private void initViews(){
        statusRadioButton = (RadioButton) findViewById(R.id.donorStatusNo);
        radioButton = (RadioButton) findViewById(R.id.donorGenderFemale);
        bloodGroupSpinner = (Spinner) findViewById(R.id.donorRegBloodGroupSpinner);
        orgSpinner = (Spinner) findViewById(R.id.donorOrganizationSpinner);
        nameEditText = (EditText) findViewById(R.id.donorNameEditText);
        passEditText = (EditText) findViewById(R.id.donorPasswordEditText);
        phoneEditText = (EditText) findViewById(R.id.donorCellEditText);
        ageEditText = (EditText) findViewById(R.id.donorAgeEditText);
        locationEditText = (EditText) findViewById(R.id.donorLocationEditText);
        genderRadioGroup = (RadioGroup) findViewById(R.id.donorGenderRadio);
        statusRadioGroup = (RadioGroup) findViewById(R.id.donorStatusRadio);

        passwordInputLayout = (TextInputLayout) findViewById(R.id.donorRegistrationPassInputLayout);
        passwordInputLayout.setPasswordVisibilityToggleEnabled(true);

        phoneInputLayout = (TextInputLayout) findViewById(R.id.donorRegistrationPhoneInputLayout);
        phoneInputLayout.setCounterEnabled(true);
        phoneInputLayout.setCounterMaxLength(11);


    }

    /*
     * Initializing Spinner Data Set
     **/
    private void initData(ArrayList<OrgProfile> orgList){
        bloodGroups = new ArrayList<>();
        bloodGroups.add("Blood Group");
        bloodGroups.add("A+(ve)");
        bloodGroups.add("A-(ve)");
        bloodGroups.add("B+(ve)");
        bloodGroups.add("B-(ve)");
        bloodGroups.add("O+(ve)");
        bloodGroups.add("O-(ve)");
        bloodGroups.add("AB+(ve)");
        bloodGroups.add("AB-(ve)");
        initBloodGroupSpinner();

        orgs = new ArrayList<>();
        orgUsername = new ArrayList<>();

        orgs.add("Select Organization");
        orgs.add("None");
        orgUsername.add("none");
        if(orgList!=null) {
            for (OrgProfile profile : orgList) {
                orgs.add(profile.name);
                orgUsername.add(profile.username);
            }
        }

        initOrgSpinner();
    }

    /** Initializing Blood Group Spinner with Adapter and Data*/
    private void initBloodGroupSpinner(){
        bloodAdapter = new CustomSpinnerAdapter(bloodGroups, this);
        bloodGroupSpinner.setAdapter(bloodAdapter);

        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    bloodGroup = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }


    /* *
     * Initializing Organization Spinner with Adapter and Data
     *
     * */

    private void initOrgSpinner(){
        orgAdapter = new CustomSpinnerAdapter(orgs, this);
        orgSpinner.setAdapter(orgAdapter);

        orgSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    organization = adapterView.getItemAtPosition(i).toString();
                    orgPos = i-1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    /*
     * Get Selected Option from Radio Button
     * */
    public String getRadioString(RadioGroup radioGroup){
        return  ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();

    }

    public void setDistrictEditText(){
        dists = new ArrayList<>();

        dists = dataBaseHelper.getDistricts();

        autoComplete = (AppCompatAutoCompleteTextView) findViewById(R.id.districtAutoCompleteText);
        autoComplete.setThreshold(1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dists);
        autoComplete.setAdapter(adapter);


//        autoComplete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                selectedDist = dists.get(i);
//                isSelected = true;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                isSelected = false;
//            }
//        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_button_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_submit_form){
            if(onlineChecker.isOnline()) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(new View(this).getWindowToken(), 0);
                registerDonor();
            }else {
                showToast("Connect to the Internet");
            }
        } else if(id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Validate and Sign up
     *
     */
    public boolean registerDonor(){

        int age = 0;
        int status= 1;
        boolean res = true;
        String name = nameEditText.getText().toString();
        String pass = passEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String gender="";
        String location = locationEditText.getText().toString();
        String selectedDist;


        if(name.length()<3){
            nameEditText.setError("Name is not Valid");
            res = false;
        }

        if(pass.length()<6){
            passEditText.setError("Minimum Password Length is 6");
            res = false;
        }
        if(!(StaticMethods.validatePhone(phone))){
            phoneEditText.setError("Invalid Phone Number");
            res = false;
        }

        if(ageEditText.getText().toString().equals("")){
            ageEditText.setError("Invalid Age");
            res  = false;
        }else{
            age = Integer.parseInt(ageEditText.getText().toString());
            if(age<18 || age>60){
                ageEditText.setError("Invalid Age");
                res  = false;
            }
        }
        if(bloodGroup!=null && bloodGroup.toLowerCase().contains("blood")){
            showToast("Select a Blood Group");
            res = false;
        }
        if(organization!=null && organization.toLowerCase().contains("select")){
            showToast("Select an Organization");
            res = false;
        }

        if(genderRadioGroup.getCheckedRadioButtonId()==-1){
            radioButton.setError("Select Gender");
            res = false;
        } else {
            gender = getRadioString(genderRadioGroup);
        }
        if(statusRadioGroup.getCheckedRadioButtonId()==-1){
            statusRadioButton.setError("Select Status");
            res = false;
        } else {
            status = (getRadioString(statusRadioGroup).equals("Available")) ?  1 : 0;
        }

       if(location.length()<4){
           locationEditText.setError("Invalid Location");
           res = false;
        }



        try{
            selectedDist = autoComplete.getText().toString();
            if(selectedDist.length()>=2) {
                char[] arr = selectedDist.toCharArray();
                boolean flag = false;
                for(int i=0;i<arr.length; i++){
                    if(!flag){
                        arr[i] = Character.toUpperCase(arr[i]);
                        flag = true;
                    }
                    else if(arr[i] == ' ')
                        flag = false;
                    else{
                        arr[i] = Character.toLowerCase(arr[i]);
                    }
                }
                selectedDist = "";
                for (char c : arr) selectedDist += c;
            }
            selectedDist = selectedDist.trim();
        }
        catch (Exception e){
            selectedDist = "aa";
        }

        if(!(dists.contains(selectedDist))){
            autoComplete.setError("Select a valid district");
            res = false;
        }

        if(res) {
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
            if(bloodGroup.contains("AB"))
                bloodGroup = bloodGroup.substring(0,3);
            else
                bloodGroup = bloodGroup.substring(0, 2);
            signUp(new DonorProfile(name,pass,phone,gender,bloodGroup,status,location, selectedDist,organization,age,0,1));
        }
        return res;
    }

    private void signUp(DonorProfile donorProfile){
        //Log.d("DATA", donorProfile.getDonorName()+" "+donorProfile.getDonorPhone()+" "+donorProfile.getDonorBloodGroup()+" "+donorProfile.getDonorGender());
        HashMap<String, String> params = getParams(donorProfile);
        //Log.d("MAP",  params.get(DataFields.DONOR_NAME_FIELD)+" "+ params.get(DataFields.DONOR_PHONE_FIELD)+" "+  params.get(DataFields.DONOR_GENDER_FIELD)+" "+ params.get(DataFields.DONOR_BLOODGROUP_FIELD));
        GsonRequestIn gsonRequestIn = new GsonRequestIn(RequestTag.DONOR_REG_URL, Products.class, params, this, this);
        VolleyHelper.getInstance(this).addToRequestQueue(gsonRequestIn);
    }

    void gatherOrg(){
        HashMap<String, String> params = new HashMap<>();
        GsonRequestIn gsonRequestIn = new GsonRequestIn(RequestTag.GET_ORG_URL, OrgReply.class, params,this, this);
        VolleyHelper.getInstance(this).addToRequestQueue(gsonRequestIn);
    }


    @Override
    public void onResponse(Object response) {
        if(response.getClass().getName().contains("Products")) {
            Products products = (Products) response;
            showToast(products.message);
            if (products.success == 1 && products.donorProfiles != null) {
                DonorProfile profile = products.donorProfiles.get(0);
                //                    Log.d("Inserting", Integer.toString(dataBaseHelper.insertSingle(profile)));
                dataBaseHelper.insertSingle(profile);
                if(pd.isShowing()) {
                    pd.dismiss();
                }
                gotoParent();
                return;
            }
            if(pd.isShowing()) {
                pd.dismiss();
            }
        } else {
            OrgReply reply = (OrgReply) response;
            if(reply.success==1 && reply.orgProfiles!=null){
                boolean canLoad = false;
                if(loginPreference.getBooleanPreferences(LoginPreference.ORG_LOADED)) {
                    if(clearOrgTable()==1) canLoad = true;
                } else {
                    canLoad = true;
                }
                if(canLoad){
                    if(dataBaseHelper.insertAllOrg(reply.orgProfiles)==1) {
                        loginPreference.setBooleanPreferences(LoginPreference.ORG_LOADED, true);
                        initData(reply.orgProfiles);
                    }

                }

            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(pd.isShowing()){
            pd.dismiss();
        }
        showToast("Connection Error!");
    }

    private HashMap<String, String> getParams(DonorProfile donorProfile){
        HashMap<String, String> params = new HashMap<>();
        params.put(DataFields.DONOR_NAME_FIELD, donorProfile.getDonorName());
        params.put(DataFields.DONOR_PASSWORD_FILED, donorProfile.getDonorPassword());
        params.put(DataFields.DONOR_PHONE_FIELD, donorProfile.getDonorPhone());
        params.put(DataFields.DONOR_GENDER_FIELD, donorProfile.getDonorGender());
        params.put(DataFields.DONOR_BLOODGROUP_FIELD, donorProfile.getDonorBloodGroup());
        params.put(DataFields.DONOR_STATUS_FIELD, String.valueOf(donorProfile.getDonorStatus()));
        params.put(DataFields.DONOR_LOCATION_FIELD, donorProfile.getDonorLocation());
        params.put(DataFields.DONOR_DISTRICT_FIELD, donorProfile.getDonorDistrict());
        params.put(DataFields.DONOR_AGE_FIELD, String.valueOf(donorProfile.getDonorAge()));
        params.put(DataFields.DONOR_ORGANIZATION_FIELD, String.valueOf(orgUsername.get(orgPos)));

        return params;
    }

    int clearOrgTable(){
        return dataBaseHelper.dropTable(OrgFields.ORG_TABLE_NAME);
    }


    @Override
    public void onBackPressed() {
        gotoParent();
    }

    private void gotoParent(){
        if(getIntent().hasExtra("FROM_LOGIN")) {
            startActivity(new Intent(DonorRegistrationActivity.this, LoginActivity.class));
            finish();
        } else {
            startActivity(new Intent(DonorRegistrationActivity.this, WelcomeActivity.class));
            finish();
        }

    }




    private void showToast(String o){
        Toast.makeText(getApplicationContext(), o, Toast.LENGTH_SHORT).show();
    }


}

