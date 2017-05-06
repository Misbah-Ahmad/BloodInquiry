package com.chowdhuryfahim.bloodinquiry;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataFields;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.OrgFields;
import com.chowdhuryfahim.bloodinquiry.models.EmailVerificationModel;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;
import java.util.HashMap;


public class ResetPasswordActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener{

    EditText usernameEditText;
    EditText codeEditText;
    Button resetButton;
    Button verifyButton;

    String finalEmail;
    int confirmationCode;
    boolean codeSent;
    boolean verified;

    String type;
    String btnText = "Get Verification Code";
    String verifyButtonText = "Set New Password";
    String usernameHint = "Username";
    String codeHint = "Verification Code";
    String username;

    LoginPreference loginPreference;
    GsonRequestIn gsonRequestIn;
    DataBaseHelper dataBaseHelper;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        pd = new ProgressDialog(this);
        pd.setMessage("Sending Verification Code");
        pd.setCancelable(false);

        codeSent = false;
        verified = false;
        type = getIntent().getStringExtra("TYPE");

        dataBaseHelper = new DataBaseHelper(this);
        loginPreference = new LoginPreference(this);

        usernameEditText = (EditText) findViewById(R.id.resetPasswordUserName);
        codeEditText = (EditText) findViewById(R.id.resetPasswordVerificationCode);
        codeEditText.setHint(codeHint);
        codeEditText.setVisibility(View.GONE);
        resetButton = (Button) findViewById(R.id.passwordResetButton);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        verifyButton.setVisibility(View.GONE);

        resetButton.setText(btnText);
        verifyButton.setText(verifyButtonText);

        if(type.equals("donor")) {
            usernameEditText.setHint("Phone Number");
        } else {
            usernameEditText.setHint("Username");
        }


    }

    public void sendCode(View view){
        if(!StaticMethods.isOnline(this)){
            showToast("Connect to the internet");
            return;
        }
        username = usernameEditText.getText().toString().trim();

        if(type.equals("donor")){
            if(!StaticMethods.validatePhone(username)){
                usernameEditText.setError("Invalid Phone Number");
                return;
            }

            finalEmail = dataBaseHelper.getDonorProfile(DataFields.DONOR_PHONE_FIELD+"=?", new String[]{username}).donorEmail;
            if(finalEmail.toLowerCase().equals("blank")){
                takeEmail();
            } else {
                requestCode();
            }


        } else {
            if(!(username.matches("[a-zA-Z0-9_]+"))){
                usernameEditText.setError("Invalid username");
                return;
            }

            finalEmail = dataBaseHelper.getOrgProfile(OrgFields.ORG_USERNAME_FIELD+"=?", new String[]{username}).email;
            if(finalEmail.toLowerCase().equals("blank")){
                takeEmail();
            } else {
                requestCode();
            }
        }
    }


    void sendMail(String email, String code){
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("code", code);
        gsonRequestIn = new GsonRequestIn(RequestTag.EMAIL_SEND_URL, EmailVerificationModel.class, params, this, this);
        VolleyHelper.getInstance(this).addToRequestQueue(gsonRequestIn);
    }

    @Override
    public void onResponse(Object response) {
        EmailVerificationModel reply = (EmailVerificationModel) response;
        if(reply.success==1){
            codeSent = true;
            usernameEditText.setText("");
            usernameHint = "New Password";
            usernameEditText.setHint(usernameHint);
            codeEditText.setVisibility(View.VISIBLE);
            verifyButton.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.GONE);
            pd.dismiss();

            Toast.makeText(this, "A verification code was sent to your email.\nif you didn\'t get one, check spam/junk folder too", Toast.LENGTH_LONG).show();
        } else {
            showToast(reply.message);
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        showToast("Connection Error");
    }


    public void verifyUser(View view){

        String userCode = codeEditText.getText().toString().trim();
        String newPass = usernameEditText.getText().toString().trim();

        if(newPass.length()<6){
            usernameEditText.setError("minimum password length is 6");
            return;
        }

        if(!(userCode.equals(Integer.toString(confirmationCode)))){
            codeEditText.setError("invalid code");
            return;
        }

        if(StaticMethods.isOnline(this)) {
            PasswordSetter passwordSetter = new PasswordSetter(this, newPass, username, type);
            showToast("Resetting Password..");
            passwordSetter.updatePassword();
        } else {
            showToast("Connect to the internet");
        }
    }

    void requestCode(){
        if(finalEmail!=null && !(finalEmail.toLowerCase().equals("blank"))){
            pd.show();
            confirmationCode = generateCode();
            sendMail(finalEmail, Integer.toString(confirmationCode));
        }
    }

    void takeEmail(){
        View view = LayoutInflater.from(this).inflate(R.layout.email_input_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).
                setView(view).
                setCancelable(false).
                setTitle("Enter email").
                setNegativeButton("Cancel", null).
                setPositiveButton("Done", null).create();
        final EditText emailText = (EditText) view.findViewById(R.id.emailToSendCode);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String email = emailText.getText().toString().trim();
                        if(StaticMethods.validateEmail(email)){
                            finalEmail = email;
                            dialog.dismiss();
                            requestCode();
                        } else {
                            emailText.setError("Invalid Email Address");
                        }
                    }
                });

                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finalEmail = null;
                        dialog.dismiss();
                    }
                });

            }

        });

        if(dialog.getWindow()!=null){
            dialog.getWindow().getAttributes().windowAnimations = R.style.LoginDialogAnimation;
        }

        dialog.show();
    }


    int generateCode(){
        return  (int)(((Math.random()+.5)*101)*((Math.random()+.7)*50)*((Math.random()+.9)*11));
    }

    void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ResetPasswordActivity.this, WelcomeActivity.class));
        finish();
    }
}
