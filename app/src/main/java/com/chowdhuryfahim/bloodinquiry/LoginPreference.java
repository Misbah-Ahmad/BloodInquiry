package com.chowdhuryfahim.bloodinquiry;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.PublicKey;

/*
 *
 * Created by Fahim on 11/22/2016.
 *
 */

public class LoginPreference {

    public static final String FILE_NAME = "userInfo";
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String LOGGED_IN_AS = "loggedInAs";

    public static final String USER_PHONE = "userPhone";
    public static final String USER__FULL_NAME = "userFullName";

    public static final String LOADED = "loaded";
    public static final String ORG_LOADED = "orgLoaded";
    public static final String IS_DISTRICT_LOADED = "isDistrictLoaded";
    public static final String IS_PREFERRED_LOCATION_SET = "isPreferredLocationSet";

    public static final String ORG_USERNAME = "orgUsername";
    public static final String USER_PREFERRED_LOCATION = "preferredLocation";
    public static final String SEARCH_BLOOD_GROUP = "searchBloodGroup";
    public static final String SEARCH_DISTRICT = "searchDistrict";
    public static final String SEARCH_ORGANIZATION = "searchOrganization";

    public static final String USER_ORG = "userOrg";
    public static final String USER_GROUP = "userGroup";
    public static final String USER_GENDER = "userGender";
    public static final String USER_AGE = "userAge";



    SharedPreferences sharedPreferences;


    public LoginPreference getLoginPreferences(Context context) {
        return (LoginPreference) context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public LoginPreference(){}
    public LoginPreference(Context context){
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }


    public void setStringPreference(String KEY, String Value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, Value);
        editor.commit();
    }

    public void setBooleanPreferences(String KEY, boolean Value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY, Value);
        editor.commit();
    }

    public void setIntegerPreferences(String KEY, int Value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY, Value);
        editor.commit();
    }

    public void setLongPreferences(String KEY, long Value){
        SharedPreferences.Editor editor  = sharedPreferences.edit();
        editor.putLong(KEY, Value);
        editor.commit();
    }

    public  void clearUserData(){
        SharedPreferences.Editor editor  = sharedPreferences.edit();
        editor.remove(USER__FULL_NAME);
        editor.remove(USER_PHONE);
        editor.remove(ORG_USERNAME);
        editor.remove(IS_LOGGED_IN);
        editor.putString(LOGGED_IN_AS, "none");
        editor.putString(SEARCH_ORGANIZATION, "none");
        editor.putString(USER_ORG, "none");
        editor.commit();
    }

    public String getStringPreferences(String KEY){
        return sharedPreferences.getString(KEY, "none");
    }

    public boolean getBooleanPreferences(String KEY){
        return sharedPreferences.getBoolean(KEY, false);
    }

    public int getIntegerPreferences(String KEY){
        return sharedPreferences.getInt(KEY, -1);
    }

    public long getLongPreferences(String KEY){
        return sharedPreferences.getLong(KEY, -1);
    }




}
