package com.chowdhuryfahim.bloodinquiry;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.models.DonorProfile;
import com.chowdhuryfahim.bloodinquiry.models.Products;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener{


    android.support.design.widget.TextInputLayout phoneLayout;
    android.support.design.widget.TextInputLayout passLayout;
    EditText phoneText;
    EditText passwordText;
    DonorProfile donorProfile;
    AppCompatButton button;

    OnlineChecker onlineChecker;
    boolean authorized = false;
    GsonRequestIn gsonRequestIn;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        button = (AppCompatButton) findViewById(R.id.loginBtn);
        phoneLayout = (TextInputLayout) findViewById(R.id.phoneInputLayout);
        passLayout = (TextInputLayout) findViewById(R.id.passinputLayout);
        phoneText = (EditText) findViewById(R.id.userPhone);
        passwordText = (EditText) findViewById(R.id.userPassword);
        onlineChecker = new OnlineChecker(this);

        phoneLayout.setCounterEnabled(true);
        phoneLayout.setCounterMaxLength(11);

        passLayout.setPasswordVisibilityToggleEnabled(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneText.getText().toString();
                String pass = passwordText.getText().toString();

                if(signin(phone, pass)){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage("Authorizing...");
                    pd.setCancelable(false);
                    if(onlineChecker.isOnline()){
                        pd.show();
                        authorizeUser(phone, pass);
                    } else {
                        errorHandler("No Internet Connection!");
                    }

                }

            }
        });

    }


    boolean signin(String phone, String pass){

        boolean ok = true;
        if(phone.length()<11 || phone.length()>11){
            phoneText.setError("Invalid Phone Number");
            ok = false;
        }

        if(pass.length()<6) {
            passwordText.setError("Invalid Password");
            ok = false;
        }
        return ok;
    }

    void authorizeUser(String phone, String pass){
        HashMap<String, String> params = new HashMap<>();
        params.put("username", phone);
        params.put("password", pass);
        gsonRequestIn = new GsonRequestIn(RequestTag.LOGIN_URL, Products.class, params, this, this);
        VolleyHelper.getInstance(this).addToRequestQueue(gsonRequestIn);
    }

    @Override
    public void onResponse(Object response) {
        Products products = (Products) response;
        //Log.d("OnResponse", "kenesolor");
        if(products.success==1){

            donorProfile = products.donorProfiles.get(0);
            LoginPreference loginPreference = new LoginPreference(this);
            loginPreference.setBooleanPreferences(LoginPreference.IS_LOGGED_IN, true);
            loginPreference.setStringPreference(LoginPreference.LOGGED_IN_AS, "Donor");
            loginPreference.setStringPreference(LoginPreference.USER__FULL_NAME, donorProfile.donorName);
            loginPreference.setStringPreference(LoginPreference.USER_PHONE, donorProfile.donorPhone);
            loginPreference.setStringPreference(LoginPreference.USER_ORG, donorProfile.donorOrganization);


            pd.dismiss();
            errorHandler(products.message);
            startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
            finish();
        } else {
            authorized = false;
            pd.dismiss();
            errorHandler(products.message);

        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        //Log.d("OnError", "kenesolor");
        pd.dismiss();
        errorHandler("Connection Error!");
    }

    public void gotoRegistration(View view){
        if(StaticMethods.isOnline(this)) {
            Intent intent = new Intent(LoginActivity.this, DonorRegistrationActivity.class);
            intent.putExtra("FROM_LOGIN", 1);
            startActivity(intent);
            overridePendingTransition(R.anim.exit_to_right, R.anim.enter_from_left);
        } else {
            errorHandler("Connect to the internet");
        }
    }

    void errorHandler(String msg){
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {

    }
}
