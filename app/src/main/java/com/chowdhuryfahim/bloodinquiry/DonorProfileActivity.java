package com.chowdhuryfahim.bloodinquiry;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataFields;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.OrgFields;
import com.chowdhuryfahim.bloodinquiry.models.DonorModel;
import com.chowdhuryfahim.bloodinquiry.models.DonorProfile;
import com.chowdhuryfahim.bloodinquiry.models.OrgProfile;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;

import java.util.ArrayList;
import java.util.HashMap;


public class DonorProfileActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener{

    AlertDialog dialog;
    DataBaseHelper dataBaseHelper;
    ProgressDialog pd;
    TextView nameTextView;
    TextView phoneTextView;
    TextView emailTextView;
    TextView bloodTextView;
    TextView genderTextView;
    TextView ageTextView;
    TextView statusTextView;
    TextView orgTextView;
    TextView locationTextView;
    FloatingActionButton fab;

    DonorProfile donorProfile;
    OrgProfile orgProfile;
    LoginPreference loginPreference;
    ScrollView scrollView;

    EditText nameView;
    EditText emailView;
    EditText passView;
    EditText phoneView;
    EditText ageView;
    EditText locationView;
    AutoCompleteTextView districtView;
    RadioGroup radioGroup;
    RadioButton radioButton;
    HashMap<String, String> params;
    final String NO_ORG_TEXT = "No Data Found";

    ArrayList<String> districtNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);

        params = new HashMap<>();


        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Updating");


        dataBaseHelper = new DataBaseHelper(this);
        loginPreference = new LoginPreference(this);

        initViews();

        scrollView.setSmoothScrollingEnabled(true);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int y  = scrollView.getScrollY();
                //Log.d("Y", Integer.toString(y));
                if(y>=1){
                    fab.animate().setStartDelay(0).scaleX(0).scaleY(0).setInterpolator(new AccelerateInterpolator()).setDuration(50).start();
                } else {
                    fab.animate().setStartDelay(0).scaleX(1).scaleY(1).setDuration(100).start();
                }
            }
        });


    }


    void initViews(){

        String selection = DataFields.DONOR_PHONE_FIELD + "=?";
        String[] args ={loginPreference.getStringPreferences(LoginPreference.USER_PHONE)};

        donorProfile = dataBaseHelper.getDonorProfile(selection, args);
        //Log.d("LOGCAT", donorProfile.donorOrganization);
        orgProfile = dataBaseHelper.getOrgProfile(OrgFields.ORG_USERNAME_FIELD + "=?", new String[]{loginPreference.getStringPreferences(LoginPreference.USER_ORG)});

        fab = (FloatingActionButton) findViewById(R.id.donorProfileEditButton);
        scrollView = (ScrollView) findViewById(R.id.donorProfileScrollView);

        nameTextView = (TextView) findViewById(R.id.profileNameText);
        nameTextView.setText(loginPreference.getStringPreferences(LoginPreference.USER__FULL_NAME));

        phoneTextView = (TextView) findViewById(R.id.profilePhoneText);
        phoneTextView.setText(loginPreference.getStringPreferences(LoginPreference.USER_PHONE));

        emailTextView = (TextView) findViewById(R.id.profileEmailText);
        String email = donorProfile.donorEmail;
        if(email.toLowerCase().equals("blank"))
            email = "None";
        emailTextView.setText(email);

        bloodTextView = (TextView) findViewById(R.id.profileBloodText);
        bloodTextView.setText(donorProfile.getDonorBloodGroup());

        genderTextView = (TextView) findViewById(R.id.profileGenderText);
        genderTextView.setText(donorProfile.getDonorGender());

        ageTextView = (TextView) findViewById(R.id.profileAgeText);
        ageTextView.setText(String.valueOf(donorProfile.getDonorAge()));

        statusTextView = (TextView) findViewById(R.id.profileStatusText);
        statusTextView.setText((donorProfile.getDonorStatus())==1 ? "Available" : "Unavailable");

        orgTextView = (TextView) findViewById(R.id.profileOrgText);


        if(orgProfile!=null && orgProfile.name!=null)
            orgTextView.setText(orgProfile.name);
      else
            orgTextView.setText(NO_ORG_TEXT);

        locationTextView = (TextView) findViewById(R.id.profileLocationText);
        locationTextView.setText(donorProfile.getDonorLocation()+", "+donorProfile.getDonorDistrict());

    }

    public void editDonorProfile(View view){
        if(StaticMethods.isOnline(this)){
            showDialog();
        } else {
            Toast.makeText(this, "Connect to the Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialog(){
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_donor_registration, null);
        builder.setView(view);
        //Positive Button as Update Button which will update profile online
        builder.setCancelable(false).setPositiveButton("Update", null).setNegativeButton("Discard", null);
        dialog = builder.create();

        nameView = (EditText) view.findViewById(R.id.donorNameEditText);
        nameView.setText(donorProfile.getDonorName());

        emailView = (EditText) view.findViewById(R.id.donorEmailEditText);
        emailView.setText(donorProfile.donorEmail);

        passView = (EditText) view.findViewById(R.id.donorPasswordEditText);
        TextInputLayout passInputLayout = (TextInputLayout) view.findViewById(R.id.donorRegistrationPassInputLayout);

        passInputLayout.setHint("Password, Leave it blank not to change");

        phoneView = (EditText) view.findViewById(R.id.donorCellEditText);
        phoneView.setText(donorProfile.getDonorPhone());

        ageView = (EditText) view.findViewById(R.id.donorAgeEditText);
        ageView.setText(String.valueOf(donorProfile.getDonorAge()));

        view.findViewById(R.id.donorGenderRadio).setVisibility(View.GONE);
        view.findViewById(R.id.donorGenderSelectText).setVisibility(View.GONE);

        radioGroup = (RadioGroup) view.findViewById(R.id.donorStatusRadio);
        radioButton = (RadioButton) view.findViewById(R.id.donorStatusYes);
        if(donorProfile.getDonorStatus()==1){
            ((RadioButton)view.findViewById(R.id.donorStatusYes)).setChecked(true);
        } else {
            ((RadioButton)view.findViewById(R.id.donorStatusNo)).setChecked(true);
        }

        locationView = (EditText) view.findViewById(R.id.donorLocationEditText);
        locationView.setText(donorProfile.getDonorLocation());

        districtView = (AutoCompleteTextView) view.findViewById(R.id.districtAutoCompleteText);
        districtView.setText(donorProfile.getDonorDistrict());

        districtNames = dataBaseHelper.getDistricts();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, districtNames);
        districtView.setAdapter(adapter);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(StaticMethods.isOnline(DonorProfileActivity.this)){
                            int res = validate();
                            if(res==1){
                                pd.show();
                                updateProfile();
                            } else if(res==0) {
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




    //Validate Updated Data
    int validate(){
        int changed= 0;
        boolean invalidFlag = false;
        params = new HashMap<>();
        String temp = nameView.getText().toString().trim();

        if(!(temp.toLowerCase().equals(donorProfile.donorName.toLowerCase()))){
            if(temp.length()<3){
                nameView.setError("Invalid name");
                invalidFlag = true;
            } else if(!(temp.matches("[a-zA-Z ]+"))) {
                nameView.setError("Only alphabets are allowed");
                invalidFlag = true;
            } else {
                params.put(DataFields.DONOR_NAME_FIELD, temp);
                changed = 1;
            }
        }


        temp = emailView.getText().toString().trim();
        if(!(temp.toLowerCase().equals(donorProfile.donorEmail.toLowerCase()))){
            if(!(StaticMethods.validateEmail(temp))){
                emailView.setError("Invalid email");
                invalidFlag = true;
            } else {
                params.put(DataFields.DONOR_EMAIL_FIELD, temp);
                changed = 1;
            }
        }

        temp = passView.getText().toString();
        if(temp.length()>0){
            if(temp.length()<5){
                passView.setError("Minimum length is 6");
                invalidFlag = true;
            } else {
                params.put(DataFields.DONOR_PASSWORD_FILED, temp);
                changed = 1;
            }
        }

        temp = phoneView.getText().toString().trim();
        if(!(temp.equals(donorProfile.donorPhone))){
            if(!(StaticMethods.validatePhone(temp))){
                phoneView.setError("Invalid Phone Number");
                invalidFlag = true;
            } else {
                params.put(DataFields.DONOR_PHONE_FIELD, temp);
                changed = 1;
            }
        }

        temp = ageView.getText().toString().trim();
        int age = Integer.parseInt(temp);

        if(age!=donorProfile.donorAge){
            if(age<18 || age>55){
                phoneView.setError("Invalid Age");
                invalidFlag = true;
            } else {
                params.put(DataFields.DONOR_AGE_FIELD, temp);
                changed = 1;
            }
        }


        temp = (radioButton.isChecked()) ? "Available" : "Unavailable";
        int status = (temp.equals("Available")) ? 1 : 0;
        if(status!=donorProfile.donorStatus){
            params.put(DataFields.DONOR_STATUS_FIELD, String.valueOf(status));
            changed = 1;
        }

        temp = locationView.getText().toString().trim();
        if(!(temp.toLowerCase().equals(donorProfile.donorLocation.toLowerCase()))) {
            if (temp.length() < 4) {
                locationView.setError("Invalid Location");
                invalidFlag = true;
            } else if(!(temp.matches("[a-zA-Z0-9_,. ]+"))){
                locationView.setError("Only alphabets & numbers are allowed");
                invalidFlag = true;
            } else {
                params.put(DataFields.DONOR_LOCATION_FIELD, temp);
                changed = 1;
            }
        }

        try{
            temp = districtView.getText().toString();
            if(temp.length()>=2) {
                char[] arr = temp.toCharArray();
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
                temp = "";
                for (char c : arr) temp += c;
            }
            temp = temp.trim();
        }
        catch (Exception e){
            temp = "aa";
        }

        if(!(districtNames.contains(temp))){
            districtView.setError("Select a valid district");
            invalidFlag = true;
        } else if(!(temp.equals(donorProfile.donorDistrict))){
            params.put(DataFields.DONOR_DISTRICT_FIELD, temp);
            changed = 1;
        }

        if(invalidFlag) return 2;
        return changed;
    }

    void updateProfile(){
        params.put("userphone", loginPreference.getStringPreferences(LoginPreference.USER_PHONE));
        GsonRequestIn gsonRequestIn = new GsonRequestIn(RequestTag.DONOR_PROFILE_UPDATE_URL, DonorModel.class, params, this, this);
        VolleyHelper.getInstance(this).addToRequestQueue(gsonRequestIn);
    }

    @Override
    public void onResponse(Object response) {
        DonorModel reply = (DonorModel) response;
        if(reply.success ==1 && reply.profile!=null){
            updateLocal(reply.profile);
        } else {
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
            showToast(reply.message);
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        if(pd!=null && pd.isShowing()){
            pd.dismiss();
        }
//        Log.e("LOGCAT", "error");
        //error.printStackTrace();
        //Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        showToast("Connection Error!");
    }

    void updateLocal(DonorProfile profile){
        //Log.d("LOCAL", profile.donorDistrict);

        String arg = loginPreference.getStringPreferences(LoginPreference.USER_PHONE);
        loginPreference.setStringPreference(LoginPreference.USER_PHONE, profile.donorPhone);
        if(dataBaseHelper.deleteDonor(DataFields.DONOR_PHONE_FIELD + "=?", new String[]{arg})){
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
            if(dialog!=null && dialog.isShowing()){
                dialog.dismiss();
            }

            if((dataBaseHelper.insertSingle(profile)==1)){
                //loginPreference.setStringPreference(LoginPreference.USER_PHONE, profile.donorPhone);
                loginPreference.setStringPreference(LoginPreference.USER__FULL_NAME, profile.donorName);
                initViews();
                showToast("Profile Updated");
            } else {
                showToast("Couldn\'t Update Profile");
            }
        } else {
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
            showToast("Couldn\'t Update Profile");
        }
    }

    void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
