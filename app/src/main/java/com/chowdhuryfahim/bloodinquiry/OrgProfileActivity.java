package com.chowdhuryfahim.bloodinquiry;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataFields;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.OrgFields;
import com.chowdhuryfahim.bloodinquiry.models.OrgProfile;
import com.chowdhuryfahim.bloodinquiry.models.OrgReply;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;


import java.util.HashMap;


public class OrgProfileActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener{

    TextView nameText;
    TextView adminText;
    TextView emailText;
    TextView phoneText;
    TextView donorsText;
    TextView locationText;
    FloatingActionButton fab;
    EditText adminEditText;
    EditText emailEditText;
    EditText phoneEditText;
    EditText passEditText;
    TextView userNameText;
    TextView emailLabel;
    ScrollView scrollView;

    DataBaseHelper dataBaseHelper;
    LoginPreference loginPreference;


    AlertDialog dialog;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_profile);

        fab = (FloatingActionButton) findViewById(R.id.orgProfileEditButton);
        emailText = (TextView) findViewById(R.id.orgProfileEmailText);
        emailLabel = (TextView) findViewById(R.id.orgProfileEmailLabel);

        if(!(getIntent().hasExtra("EDIT"))){
            fab.setVisibility(View.GONE);
            emailText.setVisibility(View.GONE);
            emailLabel.setVisibility(View.GONE);
        }

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Updating...");

        dataBaseHelper = new DataBaseHelper(this);
        loginPreference = new LoginPreference(this);

        initViews();

        scrollView.setSmoothScrollingEnabled(true);

        if((getIntent().hasExtra("EDIT"))) {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    int y = scrollView.getScrollY();
                    //Log.d("Y", Integer.toString(y));
                    if (y >= 1) {
                        fab.animate().setStartDelay(0).scaleX(0).scaleY(0).setInterpolator(new AccelerateInterpolator()).setDuration(50).start();
                    } else {
                        fab.animate().setStartDelay(0).scaleX(1).scaleY(1).setDuration(100).start();
                    }
                }
            });
        }

    }


    private void initViews(){

        String args = getIntent().getStringExtra(OrgFields.ORG_USERNAME_FIELD);
        String selection = OrgFields.ORG_USERNAME_FIELD + "=?";

        int donors = dataBaseHelper.countRows(DataFields.DONOR_TABLE_NAME, new String[]{DataFields.DONOR_ORGANIZATION_FIELD},  new String[]{args});
        OrgProfile org = dataBaseHelper.getOrgProfile(selection, new String[]{args});

        nameText = (TextView) findViewById(R.id.orgProfileNameText);
        adminText = (TextView) findViewById(R.id.orgProfileAdminText);
        phoneText = (TextView) findViewById(R.id.orgProfilePhoneText);
        donorsText = (TextView) findViewById(R.id.orgProfileCounterText);
        locationText = (TextView) findViewById(R.id.orgProfileLocationText);
        userNameText = (TextView) findViewById(R.id.orgProfileUsernameText);

        nameText.setText(org.name);
        adminText.setText(org.admin);

        if(org.email.toLowerCase().equals("blank"))
            org.email = "None";
        emailText.setText(org.email);

        phoneText.setText(org.phone);
        String counter = Integer.toString(donors);
        donorsText.setText(counter);
        locationText.setText(org.district);

        if(!(getIntent().hasExtra("EDIT"))){
            findViewById(R.id.orgProfileUserNameLabel).setVisibility(View.GONE);
            findViewById(R.id.orgProfileUsernameText).setVisibility(View.GONE);
        } else {
            userNameText.setText(org.username);

        }
        scrollView = (ScrollView) findViewById(R.id.orgProfileScrollView);

    }


    public void editOrgProfile(View view){
        if(StaticMethods.isOnline(this)){
            showDialog();
        }
    }




   private void showDialog(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_org_registration, null);

        view.findViewById(R.id.orgNameEditText).setVisibility(View.GONE);
        view.findViewById(R.id.orgUsernameEditText).setVisibility(View.GONE);
        view.findViewById(R.id.orgDistrictEditText).setVisibility(View.GONE);

        adminEditText = (EditText) view.findViewById(R.id.orgAdminEditText);
        emailEditText = (EditText) view.findViewById(R.id.orgEmailEditText);
        phoneEditText = (EditText) view.findViewById(R.id.orgPhoneEditText);
        passEditText = (EditText) view.findViewById(R.id.orgPasswordEditText);

        adminEditText.setText(adminText.getText().toString());
        emailEditText.setText(emailText.getText().toString());
        phoneEditText.setText(phoneText.getText().toString());

       ((TextInputLayout)view.findViewById(R.id.orgRegistrationPassInputLayout)).setHint("Password, Leave it blank not to change");

        builder.setView(view);

        //Positive Button as Update Button which will update profile online
        builder.setCancelable(false).setPositiveButton("Update", null).setNegativeButton("Discard", null);


        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(StaticMethods.isOnline(getApplicationContext())){
                            int result = validate();
                            if(result==1){
                                pd.show();
                                updateProfile();
                            } else if(result==0){
                                showToast("You didn't change anything");
                            }
                        } else {
                            showToast("Connect to the Internet");
                        }

                    }
                });

                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        if(dialog.getWindow()!=null){
            dialog.getWindow().getAttributes().windowAnimations = R.style.LoginDialogAnimation;
        }
        dialog.show();

    }


    private int validate(){

        if(adminEditText.getText().toString().toLowerCase().trim().equals(adminText.getText().toString().toLowerCase()) && emailEditText.getText().toString().toLowerCase().trim().equals(emailText.getText().toString().toLowerCase()) && phoneEditText.getText().toString().trim().equals(phoneText.getText().toString()) && passEditText.getText().toString().length()==0)
            return 0;

        boolean invalidFlag = false;
        String temp = adminEditText.getText().toString().trim();
        if(temp.length()<3) {
            adminEditText.setError("Invalid Name");
            invalidFlag = true;
        } else if(!(temp.matches("[a-zA-Z ]+"))){
            adminEditText.setError("Only alphabets are allowed");
            invalidFlag = true;
        }

        temp = emailEditText.getText().toString().trim();
        if(!(StaticMethods.validateEmail(temp))) {
            emailEditText.setError("Invalid Email");
            invalidFlag = true;
        }

        if(!(StaticMethods.validatePhone(phoneEditText.getText().toString().trim()))){
            phoneEditText.setError("Invalid Phone");
            invalidFlag = true;
        }

        if(passEditText.getText().toString().length()>0 && passEditText.getText().toString().length()<6){
            passEditText.setError("Minimum password length is 6");
            invalidFlag = true;
        }

        if(invalidFlag)
            return 2;

        return 1;

    }

    private void updateProfile(){

        HashMap<String, String> params = new HashMap<>();
        String temp = adminEditText.getText().toString().trim();

        if(!(temp.toLowerCase().equals(adminText.getText().toString().toLowerCase())))
            params.put(OrgFields.ORG_ADMIN_FIELD, temp);

        temp = emailEditText.getText().toString().trim();
        if((StaticMethods.validateEmail(temp)))
            params.put(OrgFields.ORG_EMAIL_FIELD, temp.toLowerCase());

        temp = phoneEditText.getText().toString().trim();
        if(!(temp.equals(phoneText.getText().toString())))
            params.put(OrgFields.ORG_PHONE_FIELD, temp);

        temp = passEditText.getText().toString();
        if(temp.length()>0)
            params.put(OrgFields.ORG_PASSWORD_FIELD, temp);

        params.put("username", getIntent().getStringExtra(OrgFields.ORG_USERNAME_FIELD));

        GsonRequestIn gsonRequestIn = new GsonRequestIn(RequestTag.ORG_PROFILE_UPDATE_URL, OrgReply.class, params, this, this);
        VolleyHelper.getInstance(this).addToRequestQueue(gsonRequestIn);
    }

    @Override
    public void onResponse(Object response) {
        OrgReply reply = (OrgReply) response;
        if(reply.success ==1 && reply.orgProfiles!=null){

            updateLocal(reply.orgProfiles.get(0));
        } else {

            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
            showToast(reply.message);
        }

    }


    @Override
    public void onErrorResponse(VolleyError error) {
        pd.dismiss();
        showToast("Connection Error");
    }

    private void updateLocal(OrgProfile orgProfile){
        String args= new LoginPreference(this).getStringPreferences(LoginPreference.ORG_USERNAME);
        String selection = OrgFields.ORG_USERNAME_FIELD + "=?";

        if(pd!=null && pd.isShowing()) pd.dismiss();

        if(dataBaseHelper.deleteOrg(selection, new String[]{args})){
            if(dataBaseHelper.insertSingleOrg(orgProfile)==1){
                if(dialog!=null && dialog.isShowing()) dialog.dismiss();
                showToast("Information is Updated");
                initViews();
            } else {

                showToast("Try Again!");
            }
        } else {
            showToast("Try Again!");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            gotoParent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String msg){
        Toast.makeText(OrgProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void gotoParent(){
        if(getIntent().hasExtra("EDIT")){
            startActivity(new Intent(OrgProfileActivity.this, WelcomeActivity.class));
            finish();
        }
        else {
            startActivity(new Intent(OrgProfileActivity.this, OrgListActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        gotoParent();
    }
}
