package com.chowdhuryfahim.bloodinquiry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.OrgFields;
import com.chowdhuryfahim.bloodinquiry.models.OrgReply;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class OrgRegistrationActivity extends AppCompatActivity implements OrgFields, Response.Listener, Response.ErrorListener{

    EditText nameEditText;
    EditText adminEditText;
    EditText emailEditText;
    EditText phoneEditText;
    EditText usernameEditText;
    EditText passEditText;
    AutoCompleteTextView districtEditText;
    GsonRequestIn gsonRequestIn;
    ProgressDialog pd;
    DataBaseHelper dataBaseHelper;
    ArrayList<String> districtNames;

    TextInputLayout phoneInputLayout;
    TextInputLayout passInputLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_registration);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dataBaseHelper = new DataBaseHelper(this);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...\nPlease wait");
        pd.setCancelable(false);

        nameEditText = (EditText) findViewById(R.id.orgNameEditText);
        adminEditText = (EditText) findViewById(R.id.orgAdminEditText);
        emailEditText = (EditText) findViewById(R.id.orgEmailEditText);
        phoneEditText = (EditText) findViewById(R.id.orgPhoneEditText);
        usernameEditText = (EditText) findViewById(R.id.orgUsernameEditText);
        passEditText = (EditText) findViewById(R.id.orgPasswordEditText);
        districtEditText = (AutoCompleteTextView) findViewById(R.id.orgDistrictEditText);
        initAutoCompleteText();

        passInputLayout = (TextInputLayout) findViewById(R.id.orgRegistrationPassInputLayout);
        phoneInputLayout = (TextInputLayout) findViewById(R.id.orgRegistrationPhoneInputLayout);

        passInputLayout.setPasswordVisibilityToggleEnabled(true);
        phoneInputLayout.setCounterEnabled(true);
        phoneInputLayout.setCounterMaxLength(11);
    }

    void initAutoCompleteText(){
        districtEditText.setThreshold(1);
        districtNames = dataBaseHelper.getDistricts();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, districtNames);
        districtEditText.setAdapter(adapter);

//        districtEditText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=  item.getItemId();
        if(id==R.id.action_submit_form){
            registerOrg();
        }else if(id==android.R.id.home){
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    //Validate and Register Organization
    private void registerOrg(){
        boolean ok=true;
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String admin = adminEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passEditText.getText().toString();
        String username = usernameEditText.getText().toString().trim();
        String district;

        if(name.length()<4){
            nameEditText.setError("Enter a valid name");
            ok= false;
        } else if(!(name.matches("[a-zA-Z0-9 _]+"))){
            nameEditText.setError("Only alphabets & numbers are allowed");
            ok= false;
        }

        if(admin.length()<3){
            adminEditText.setError("Enter a valid name");
            ok = false;
        } else if(!(admin.matches("[a-zA-Z ]+"))){
            adminEditText.setError("Only alphabets are allowed");
            ok= false;
        }

        if(!StaticMethods.validateEmail(email)){
            emailEditText.setError("Invalid email address");
            ok = false;
        }

        if(!(StaticMethods.validatePhone(phone))){
            phoneEditText.setError("Invalid Phone Number");
            ok = false;
        }

        if(password.length()<6){
            passEditText.setError("Minimum password length is 6");
            ok = false;
        }

        if(username.length()<6){
            usernameEditText.setError("Minimum length is 6");
            ok = false;
        }else if(!(username.matches("[a-zA-Z0-9_]+"))){
            usernameEditText.setError("Use only letters, numbers & underscore");
            ok = false;
        }

        try {
            district = districtEditText.getText().toString();
            if(district.length()>=2) {
                char[] arr = district.toCharArray();
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
                district = "";
                for (char c : arr) district += c;
            }
            district = district.trim();
        } catch (Exception e){
            district = "aa";
        }

        if(!(districtNames.contains(district))){
            districtEditText.setError("Select a valid district");
            ok = false;
        }

        if(ok){
            pd.show();
            HashMap<String, String> params = new HashMap<>();
            params.put(ORG_NAME_FIELD, name);
            params.put(ORG_ADMIN_FIELD, admin);
            params.put(ORG_EMAIL_FIELD, email);
            params.put(ORG_PHONE_FIELD, phone);
            params.put(ORG_USERNAME_FIELD, username);
            params.put(ORG_PASSWORD_FIELD, password);
            params.put(ORG_DISTRICT_FIELD, district);
            gsonRequestIn = new GsonRequestIn(RequestTag.ORG_REG_URL, OrgReply.class, params, this, this);
            VolleyHelper.getInstance(this).addToRequestQueue(gsonRequestIn);
        }

    }


    @Override
    public void onResponse(Object response) {
        OrgReply reply = (OrgReply) response;
        pd.dismiss();
        showToast(reply.message);
        if(reply.success==1){
            dataBaseHelper.insertSingleOrg(reply.orgProfiles.get(0));
            startActivity(new Intent(OrgRegistrationActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(pd.isShowing()){
            pd.dismiss();
        }
        showToast("Connection Error!");
    }

    void showToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

}
