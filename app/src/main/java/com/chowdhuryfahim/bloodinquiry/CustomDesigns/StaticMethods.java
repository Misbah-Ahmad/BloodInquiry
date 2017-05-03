package com.chowdhuryfahim.bloodinquiry.CustomDesigns;

import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataBaseHelper;
import com.chowdhuryfahim.bloodinquiry.DatabaseFiles.DataFields;
import com.chowdhuryfahim.bloodinquiry.LoginPreference;
import com.chowdhuryfahim.bloodinquiry.volley.RequestTag;

/*
 * Created by Fahim on 4/3/2017.
 */

public class StaticMethods {

    public final static String DEVELOPER_EMAIL = "misbahahmad3@hotmail.com";
    public static boolean deleteDonorFromLocal(Context context){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        String arg = new LoginPreference(context).getStringPreferences(LoginPreference.USER_PHONE);
        return dataBaseHelper.deleteDonor(DataFields.DONOR_PHONE_FIELD + "=?", new String[]{arg});
    }


    /**
     * return true if phone is a valid phone number format other wise false
     *@author Fahim Chowdhury
     * @param phone
     * @return true if phone is a valid phone number format other wise false
     */
    public static boolean validatePhone(String phone) {
        return ((phone!=null && phone.length()==11) && (phone.charAt(0)=='0' && phone.charAt(1)=='1' && (phone.charAt(2) == '1' || phone.charAt(2) == '5' ||
                phone.charAt(2) == '6' || phone.charAt(2) == '7' || phone.charAt(2) == '8' || phone.charAt(2) == '9')));
    }

    /**
     *
     * @param context
     * @return true if device is online otherwise false
     */

    public static boolean isOnline(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    //Download Manager
    public static void fileDownloader(String username, Context context){
        if (isOnline(context)) {
            Uri uri = Uri.parse(RequestTag.DOWNLOAD_URL + username);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Donors.pdf");
            request.setTitle("Donors List");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } else {
            Toast.makeText(context, "Connect to the internet", Toast.LENGTH_SHORT).show();
        }
    }






}
