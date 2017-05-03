package com.chowdhuryfahim.bloodinquiry;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chowdhuryfahim.bloodinquiry.CustomDesigns.StaticMethods;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.OrgFields;
import com.chowdhuryfahim.bloodinquiry.models.DonorModel;
import com.chowdhuryfahim.bloodinquiry.volley.GsonRequestIn;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;
import com.chowdhuryfahim.bloodinquiry.volley.VolleyHelper;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import java.util.ArrayList;
import java.util.HashMap;


public class WelcomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LoginPreference loginPreference;
    NavigationView navigationView;
    OnlineChecker onlineChecker;
    AutoCompleteTextView preferredLocationEditText;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        onlineChecker = new OnlineChecker(this);
        loginPreference = new LoginPreference(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        callbackManager = CallbackManager.Factory.create();

        //Declaring Drawer and adding Listener
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setNavigationView();
        navigationView.setNavigationItemSelectedListener(this);


    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } //else {
//            super.onBackPressed();
//        }
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.registerDonor) {
            if(onlineChecker.isOnline()) {
                startActivity(new Intent(WelcomeActivity.this, DonorRegistrationActivity.class));
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

            } else{
                showToast("Connect to the Internet");
            }
        } else if (id == R.id.registerOrg) {
            if(!StaticMethods.isOnline(this)){
                showToast("Connect to the internet");
            } else {
                startActivity(new Intent(WelcomeActivity.this, OrgRegistrationActivity.class));
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
            }
        } else if (id == R.id.organizationList) {
            if(!(loginPreference.getBooleanPreferences(LoginPreference.ORG_LOADED)) && !StaticMethods.isOnline(this)){
                showToast("Connect to the internet");
            } else {
                startActivity(new Intent(WelcomeActivity.this, OrgListActivity.class));
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
            }
        } else if (id == R.id.locationUpdate) {
            getPreferredLocation();

        } else if (id == R.id.takeFeedBack) {
            sendFeedback();
        } else if(id == R.id.viewProfile){
            startActivity(new Intent(WelcomeActivity.this, DonorProfileActivity.class));
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

        }else if(id==R.id.nav_logout){
            logout();
        }else if(id==R.id.nav_login){
            login();
        } else if(id==R.id.nav_delete_donor){
            deleteAccount();
        } else if(id==R.id.navOrgDonorList){
            Intent intent = new Intent(WelcomeActivity.this, DonorListActivity.class);

            loginPreference.setStringPreference(LoginPreference.SEARCH_ORGANIZATION, loginPreference.getStringPreferences(LoginPreference.ORG_USERNAME));
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

        } else if(id==R.id.orgInfoUpdate){
            Intent intent = new Intent(WelcomeActivity.this, OrgProfileActivity.class);
            intent.putExtra("EDIT", "YES");
            intent.putExtra(OrgFields.ORG_USERNAME_FIELD, loginPreference.getStringPreferences(LoginPreference.ORG_USERNAME));
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

        } else if(id==R.id.downloadList){
            seekPermission();
        } else if(id==R.id.actionHelp){
            startActivity(new Intent(WelcomeActivity.this, HelpActivity.class));
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        } else if(id==R.id.drawerAppShare){
                appShareDialog();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void filterDonor(View view) {
        startActivity(new Intent(WelcomeActivity.this, DonorSearchActivity.class));
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

    }



    /*
     *  Set Navigation Drawer view depending on the login information
    */
    private void setNavigationView() {
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.nav_header_welcome, null);
        String str = loginPreference.getStringPreferences(LoginPreference.USER__FULL_NAME);
        String des = loginPreference.getStringPreferences(LoginPreference.LOGGED_IN_AS);

        if (loginPreference.getBooleanPreferences(LoginPreference.IS_LOGGED_IN)) {
            String userType = loginPreference.getStringPreferences(LoginPreference.LOGGED_IN_AS);
            switch (userType){
                case "Organization" :
                    ((TextView)view.findViewById(R.id.drawer_head_name)).setText(str);
                    ((TextView)view.findViewById(R.id.drawer_head_description)).setText(des);
                    navigationView.addHeaderView(view);
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.organization_drawer_menu);
                    break;
                case "Donor" :
                    ((TextView)view.findViewById(R.id.drawer_head_name)).setText(str);
                    ((TextView)view.findViewById(R.id.drawer_head_description)).setText(des);
                    navigationView.addHeaderView(view);
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.donor_drawer_menu);
                    break;
            }
        } else {
            ((TextView)view.findViewById(R.id.drawer_head_name)).setText(getString(R.string.app_name));
            (view.findViewById(R.id.drawer_head_description)).setVisibility(View.GONE);
            navigationView.addHeaderView(view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_welcome_drawer);

        }
    }



    /*
     * Creates a Dialog to decide to login as donor or organization
     */
    public void login() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_login_dialog, null);
        builder.setView(view);
        builder.setTitle("Login As");

        TextView donorText = (TextView) view.findViewById(R.id.donorLoginDialogButton);
        TextView orgLoginButton = (TextView) view.findViewById(R.id.orgLoginDialogButton);


        donorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

            }
        });

        orgLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, OrgLoginActivity.class));
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

            }
        });

        AlertDialog dialog = builder.create();

        if(dialog.getWindow()!=null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.LoginDialogAnimation;
        }

        dialog.show();
    }



    /*
     * Method For Deleting Donor Account
     */
    void deleteAccount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure?\nYour blood can save a life. :)");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(onlineChecker.isOnline()){
                    final ProgressDialog pd = new ProgressDialog(WelcomeActivity.this);
                    pd.setMessage("Deleting Account");
                    pd.setCancelable(false);
                    pd.show();

                    HashMap<String, String> params = new HashMap<>();
                    params.put("userphone", loginPreference.getStringPreferences(LoginPreference.USER_PHONE));
                    GsonRequestIn gsonRequestIn =  new GsonRequestIn(RequestTag.DONOR_PROFILE_DELETE_URL, DonorModel.class, params, new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            DonorModel reply = (DonorModel) response;
                            if(reply.success==1) {
                                showToast("Account Deleted");
                                StaticMethods.deleteDonorFromLocal(WelcomeActivity.this);
                                if(pd.isShowing()){
                                    pd.dismiss();
                                }
                                logout();
                            } else {
                                if(pd.isShowing()){
                                    pd.dismiss();
                                }
                                showToast("Could not delete");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(pd.isShowing()){
                                pd.dismiss();
                            }
                            showToast("Connection Error");
                        }
                    });
                    VolleyHelper.getInstance(WelcomeActivity.this).addToRequestQueue(gsonRequestIn);

                } else {
                    showToast("Connect to the Internet");
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();

    }




    /*
    *   Method to SET/UPDATE anonymous users preferred location
    */
    void getPreferredLocation(){

        View view = LayoutInflater.from(this).inflate(R.layout.location_custom_dialog, null);
        preferredLocationEditText = (AutoCompleteTextView) view.findViewById(R.id.preferredLocationEditText);
        final ArrayList<String> districtNames = new DataBaseHelper(this).getDistricts();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, districtNames);
        preferredLocationEditText.setThreshold(1);
        preferredLocationEditText.setAdapter(adapter);

        if(loginPreference.getBooleanPreferences(LoginPreference.IS_PREFERRED_LOCATION_SET)){
            preferredLocationEditText.setText(loginPreference.getStringPreferences(LoginPreference.USER_PREFERRED_LOCATION));
        }

        final AlertDialog dialog = new AlertDialog.Builder(this).
                setCancelable(false).
                setView(view).
                setTitle("Preferred Location").
                setPositiveButton("Save", null).
                setNegativeButton("Discard", null).
                create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String preferredLocation  = preferredLocationEditText.getText().toString();
                        if(preferredLocation.length()<1){
                            loginPreference.setBooleanPreferences(LoginPreference.IS_PREFERRED_LOCATION_SET, false);
                            loginPreference.setStringPreference(LoginPreference.USER_PREFERRED_LOCATION, "none");
                            dialog.dismiss();

                        }else if(districtNames.contains(preferredLocation)){
                            loginPreference.setBooleanPreferences(LoginPreference.IS_PREFERRED_LOCATION_SET, true);
                            loginPreference.setStringPreference(LoginPreference.USER_PREFERRED_LOCATION, preferredLocation);
                            dialog.dismiss();
                        } else {
                            preferredLocationEditText.setError("Invalid Location");

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

        if(dialog.getWindow()!=null){
            dialog.getWindow().getAttributes().windowAnimations = R.style.LoginDialogAnimation;
        }

        dialog.show();

    }


    //Logout by erasing preferences in sharedPreference and set the UI for logged out user
    public void logout(){
        loginPreference.clearUserData();
        setNavigationView();
    }


    //Seek Permission to write in External Storage of Device
    private void seekPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            //this block is executed if user previously denied the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showToast("Permission needed to save the file");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            StaticMethods.fileDownloader(loginPreference.getStringPreferences(LoginPreference.ORG_USERNAME), this);
        }
    }


    //Called when permission is requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    StaticMethods.fileDownloader(loginPreference.getStringPreferences(LoginPreference.ORG_USERNAME), this);
                } else {
                    showToast("Permission Denied\nCannot save the file");
                }
                break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionExit) {
            finishAffinity();
        } else if(id==R.id.actionAbout){
            startActivity(new Intent(WelcomeActivity.this, AboutActivity.class));
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        } else if(id==R.id.actionCredit){
            startActivity(new Intent(WelcomeActivity.this, CreditActivity.class));
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     *  This method shows dialog to share app on Facebook and Messenger
     *
     */
    private void appShareDialog(){
        final String PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=" + this.getPackageName();

        if(StaticMethods.isOnline(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View shareDialog = LayoutInflater.from(this).inflate(R.layout.app_share_dialog, null);
            Button fbShare = (Button) shareDialog.findViewById(R.id.fbAppShareButton);
            Button messengerShare = (Button) shareDialog.findViewById(R.id.messengerAppShareButton);

            builder.setTitle("Share App").setView(shareDialog).setCancelable(true);
            final AlertDialog shareAlertDialog = builder.create();

            fbShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareAlertDialog.dismiss();
                    if(ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareDialog shareDialog = new ShareDialog(WelcomeActivity.this);

                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(PLAY_STORE_LINK))
                                .setQuote("Be a life saver or find a Donor")
                                .build();
                        shareDialog.show(linkContent);
                    }

                }
            });

            messengerShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareAlertDialog.dismiss();
                    if(MessageDialog.canShow(ShareLinkContent.class)) {
                        MessageDialog shareDialog = new MessageDialog(WelcomeActivity.this);
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(PLAY_STORE_LINK))
                                .setQuote("Be a life saver or find one")
                                .build();
                        shareDialog.show(linkContent);
                    }

                }
            });


            if(shareAlertDialog.getWindow()!=null) {
                shareAlertDialog.getWindow().getAttributes().windowAnimations = R.style.LoginDialogAnimation;
            }
            shareAlertDialog.show();

        } else {
            showToast("Connect to the internet");
        }

    }

    void sendFeedback(){
//        Intent sendMailIntent = new Intent(Intent.ACTION_VIEW);
//        sendMailIntent.setType("plain/text");
//        sendMailIntent.setData(Uri.parse(StaticMethods.DEVELOPER_EMAIL));
//        sendMailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
//        sendMailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {StaticMethods.DEVELOPER_EMAIL});
//        sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, "Blood Inquiry");
//        try{
//            startActivity(sendMailIntent);
//        }catch (Exception e){
//            showToast("Gmail Application is not installed or disabled.");
//        }

        Intent sendMailIntent = new Intent(Intent.ACTION_SEND);
        sendMailIntent.setType("message/rfc822");
        sendMailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{StaticMethods.DEVELOPER_EMAIL});
        sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, "Blood Inquiry App");
        try {
            startActivity(Intent.createChooser(sendMailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("No Email app was found");
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}