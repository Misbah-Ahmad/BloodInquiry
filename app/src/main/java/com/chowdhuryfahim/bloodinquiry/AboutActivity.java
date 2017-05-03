package com.chowdhuryfahim.bloodinquiry;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;


public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    AppCompatButton rateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        rateButton = (AppCompatButton) findViewById(R.id.rate_button);
        rateButton.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        if(StaticMethods.isOnline(this)) {
            Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
            }
        } else {
            Toast.makeText(this, "Connect to the internet", Toast.LENGTH_SHORT).show();
        }
    }
}

