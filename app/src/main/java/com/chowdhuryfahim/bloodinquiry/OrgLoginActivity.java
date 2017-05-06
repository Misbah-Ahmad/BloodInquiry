package com.chowdhuryfahim.bloodinquiry;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;
import com.chowdhuryfahim.bloodinquiry.models.OrgProfile;
import com.chowdhuryfahim.bloodinquiry.models.OrgReply;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;

import java.util.HashMap;

public class OrgLoginActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener{

    android.support.design.widget.TextInputLayout passLayout;
    TextInputEditText usernameText;
    TextInputEditText passwordText;
    OrgProfile orgProfile;
    AppCompatButton button;

    TextView forgotText;

    boolean authorized = false;
    GsonRequestIn gsonRequestIn;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_login);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        button = (AppCompatButton) findViewById(R.id.orgLoginButton);
        passLayout = (TextInputLayout) findViewById(R.id.orgLoginPassInputLayout);
        usernameText = (TextInputEditText) findViewById(R.id.loginUsernameEditText);
        passwordText = (TextInputEditText) findViewById(R.id.orgLoginPasswordEditText);
        forgotText = (TextView) findViewById(R.id.orgForgotPasswordText);

        passLayout.setPasswordVisibilityToggleEnabled(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameText.getText().toString();
                String pass = passwordText.getText().toString();

                if(signin(username, pass)){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    pd = new ProgressDialog(OrgLoginActivity.this);
                    pd.setMessage("Authorizing...");
                    pd.setCancelable(false);
                    if(StaticMethods.isOnline(OrgLoginActivity.this)){
                        pd.show();
                        authorizeUser(username, pass);
                    } else {
                        errorHandler("No Internet Connection!");
                    }

                }

            }
        });

    }


    boolean signin(String username, String pass){

        boolean ok = true;
        if(username.length()<6){
            usernameText.setError("Minimum Length is 6");
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
        gsonRequestIn = new GsonRequestIn(RequestTag.ORG_LOGIN_URL, OrgReply.class, params, this, this);
        VolleyHelper.getInstance(this).addToRequestQueue(gsonRequestIn);
    }

    @Override
    public void onResponse(Object response) {
        OrgReply reply = (OrgReply) response;
        //Log.d("OnResponse", "kenesolor");
        if(reply.success==1){

            orgProfile = reply.orgProfiles.get(0);
            LoginPreference loginPreference = new LoginPreference(this);
            loginPreference.setBooleanPreferences(LoginPreference.IS_LOGGED_IN, true);
            loginPreference.setStringPreference(LoginPreference.LOGGED_IN_AS, "Organization");
            loginPreference.setStringPreference(LoginPreference.USER__FULL_NAME, orgProfile.name);
            loginPreference.setStringPreference(LoginPreference.ORG_USERNAME, orgProfile.username);

            pd.dismiss();
            errorHandler(reply.message);
            startActivity(new Intent(OrgLoginActivity.this, WelcomeActivity.class));
            finish();
        } else {
            authorized = false;
            pd.dismiss();
            errorHandler(reply.message);

        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        //Log.d("OnError", "kenesolor");
        pd.dismiss();
        errorHandler("Connection Error!");
    }

    public void resetOrgPassword(View view){
        Intent intent = new Intent(OrgLoginActivity.this, ResetPasswordActivity.class);
        intent.putExtra("TYPE", "org");
        startActivity(intent);
        finish();
    }


    void errorHandler(String msg){
        Toast.makeText(OrgLoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }



}
