package com.chowdhuryfahim.bloodinquiry;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.OrgFields;
import com.chowdhuryfahim.bloodinquiry.models.EmailVerificationModel;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;
import java.util.HashMap;

/**
 *
 * Created by Fahim Chowdhury on 5/6/2017.
 *
 */

class PasswordSetter implements Response.Listener, Response.ErrorListener{
    private Context context;
    private String password;
    private String userName;
    private String userType;

    GsonRequestIn gsonRequestIn;

    PasswordSetter(Context context, String password, String userName, String userType){
        this.context = context;
        this.password = password;
        this.userName = userName;
        this.userType = userType;
    }

    void updatePassword(){
        HashMap<String, String> params = new HashMap<>();
        params.put(OrgFields.ORG_USERNAME_FIELD, userName);
        params.put(OrgFields.ORG_PASSWORD_FIELD, password);
        params.put("type", userType);

        gsonRequestIn = new GsonRequestIn(RequestTag.RESET_PASSWORD_URL, EmailVerificationModel.class, params, this, this);
        VolleyHelper.getInstance(context).addToRequestQueue(gsonRequestIn);
    }

    @Override
    public void onResponse(Object response) {
        EmailVerificationModel reply = (EmailVerificationModel) response;
        if(reply.success==1){
            Toast.makeText(context, "Your password has successfully changed", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, WelcomeActivity.class));
        } else {
            Toast.makeText(context, reply.message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();

    }


}
