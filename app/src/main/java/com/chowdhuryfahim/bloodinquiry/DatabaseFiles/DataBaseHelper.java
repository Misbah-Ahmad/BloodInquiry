package com.chowdhuryfahim.bloodinquiry.DatabaseFiles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.chowdhuryfahim.bloodinquiry.models.DonorProfile;
import com.chowdhuryfahim.bloodinquiry.models.OrgProfile;
import java.util.ArrayList;

/*
 * Created by Fahim on 3/26/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper implements DataFields, OrgFields, DistrictFields{


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null,1);
    }

//    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
//        super(context, name, factory, version, errorHandler);
//    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {

            sqLiteDatabase.execSQL(CREATE_DONOR_TABLE_QUERY);
            sqLiteDatabase.execSQL(ORG_TABLE_CREATE_QUERY);
            sqLiteDatabase.execSQL(DISTRICT_TABLE_CREATE_QUERY);

        }catch (Exception e){
            //e.printStackTrace();
            //Log.d("dbCreateError", e.toString());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old, int newer) {

    }



    public  int dropTable(String TABLE_NAME){
        SQLiteDatabase dbase = this.getWritableDatabase();
        try {
            dbase.execSQL(DROP_QUERY+TABLE_NAME);
            this.onCreate(dbase);
        } catch (Exception e){
            dbase.close();
            return -1;
        }
        dbase.close();
        return 1;
    }

    public int countRows(String table, String[] column, String[] key){
        String query;
        if(column!=null){
            query = "SELECT COUNT(*)  FROM " +table+ " WHERE " +column[0]+ " = '" +key[0]+ "'";

            for (int i=1; i<column.length; i++) {
                query += " AND " +column[i] + " = '" +key[i]+ "'";
            }
        } else {
            query = "SELECT COUNT(*) FROM " +table;
        }

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);

        int counter = -1;
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            counter = cursor.getInt(0);
        }
        sqLiteDatabase.close();
        cursor.close();
        return counter;
    }


/*************************************
 *
 *  Code for handling Donors Table
 *
 * ***********************************/


    public int insertRows(ArrayList<DonorProfile> profiles){

        SQLiteDatabase dBase;
        try{
            dBase = this.getWritableDatabase();
        }catch (Exception e){
            return -1;
        }
        ContentValues values = new ContentValues();
        for (int i=0; i<profiles.size(); i++){
            values.put(DONOR_ID_FIELD, profiles.get(i).getDonorId());
            values.put(DONOR_NAME_FIELD, profiles.get(i).getDonorName());
            values.put(DONOR_PHONE_FIELD, profiles.get(i).getDonorPhone());
            values.put(DONOR_GENDER_FIELD, profiles.get(i).getDonorGender());
            values.put(DONOR_BLOODGROUP_FIELD, profiles.get(i).getDonorBloodGroup());
            values.put(DONOR_STATUS_FIELD, profiles.get(i).getDonorStatus());
            values.put(DONOR_LOCATION_FIELD, profiles.get(i).getDonorLocation());
            values.put(DONOR_DISTRICT_FIELD, profiles.get(i).getDonorDistrict());
            values.put(DONOR_AGE_FIELD, profiles.get(i).getDonorAge());
            values.put(DONOR_SHOULDSHOW_FIELD, profiles.get(i).getDonorShouldShow());
            values.put(DONOR_ORGANIZATION_FIELD, profiles.get(i).getDonorOrganization());

            try {
                dBase.insert(DONOR_TABLE_NAME, null, values);
            }catch (Exception e){
                dBase.close();
                return  -1;
                //Log.d("InsertException", e.toString());
            }
            values.clear();
        }
        dBase.close();
        return 1;
    }


    public int insertSingle(DonorProfile profiles){

        SQLiteDatabase dBase;
        try{
            dBase = this.getWritableDatabase();
        }catch (Exception e){
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put(DONOR_ID_FIELD, profiles.getDonorId());
        values.put(DONOR_NAME_FIELD, profiles.getDonorName());
        values.put(DONOR_PHONE_FIELD, profiles.getDonorPhone());
        values.put(DONOR_GENDER_FIELD, profiles.getDonorGender());
        values.put(DONOR_BLOODGROUP_FIELD, profiles.getDonorBloodGroup());
        values.put(DONOR_STATUS_FIELD, profiles.getDonorStatus());
        values.put(DONOR_LOCATION_FIELD, profiles.getDonorLocation());
        values.put(DONOR_DISTRICT_FIELD, profiles.getDonorDistrict());
        values.put(DONOR_AGE_FIELD, profiles.getDonorAge());
        values.put(DONOR_SHOULDSHOW_FIELD, profiles.getDonorShouldShow());
        values.put(DONOR_ORGANIZATION_FIELD, profiles.getDonorOrganization());
        try {
            dBase.insert(DONOR_TABLE_NAME, null, values);
        }catch (Exception e){
            //Log.d("InsertException", e.toString());
            return  -1;
        }
        values.clear();
        dBase.close();

        return 1;
    }

    public ArrayList<DonorProfile> getRows(){
        SQLiteDatabase dBase;
        ArrayList<DonorProfile> profiles = new ArrayList<>();
        Cursor cursor;
        try{
            dBase = this.getReadableDatabase();
            cursor = dBase.rawQuery(SELECT_ALL_QUERY+DONOR_TABLE_NAME,null);

            if(cursor.getCount()<=0){
                return null;
            }
        }catch (Exception e){
            return  null;
        }
        cursor.moveToFirst();
        do {
            profiles.add(new DonorProfile(
                    cursor.getString(cursor.getColumnIndex(DONOR_NAME_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_PHONE_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_GENDER_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_BLOODGROUP_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_STATUS_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_LOCATION_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_DISTRICT_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_ORGANIZATION_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_AGE_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_ID_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_SHOULDSHOW_FIELD))
            ));
        } while (cursor.moveToNext());

        cursor.close();
        dBase.close();
        return  profiles;

    }


    public ArrayList<DonorProfile> getRowsByGroupDist(String selection, String[] args){
        SQLiteDatabase dBase;
        ArrayList<DonorProfile> profiles = new ArrayList<>();
        Cursor cursor;

        try{
            dBase = this.getReadableDatabase();
            cursor = dBase.query(DONOR_TABLE_NAME, null, selection, args,null, null, null, null);
            //cursor = dBase.rawQuery("SELECT * FROM "+DONOR_TABLE_NAME+" WHERE "+DONOR_BLOODGROUP_FIELD+"='A+'", null);
            if(cursor.getCount()<=0){
                //Log.d("CURSORCOUNT", Integer.toString(cursor.getCount()));
                dBase.close();
                cursor.close();
                return null;
            }
        } catch (Exception e){

            return  null;
        }

        cursor.moveToFirst();

        do {
            profiles.add(new DonorProfile(
                    cursor.getString(cursor.getColumnIndex(DONOR_NAME_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_PHONE_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_GENDER_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_BLOODGROUP_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_STATUS_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_LOCATION_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_DISTRICT_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_ORGANIZATION_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_AGE_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_ID_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_SHOULDSHOW_FIELD))
            ));
            //Log.d("check",profiles.get(0).getDonorBloodGroup());
        } while (cursor.moveToNext());

        cursor.close();
        dBase.close();
        if(profiles.size()<1) return null;
        return  profiles;
    }

    ///Getting Single Donor Profile
    public DonorProfile getDonorProfile(String selection, String[] args){
        SQLiteDatabase dBase;
        DonorProfile profile;
        Cursor cursor;

        try{
            dBase = this.getReadableDatabase();
            cursor = dBase.query(DONOR_TABLE_NAME, null, selection, args,null, null, null, "1");
            //cursor = dBase.rawQuery("SELECT * FROM "+DONOR_TABLE_NAME+" WHERE "+DONOR_BLOODGROUP_FIELD+"='A+'", null);
            if(cursor.getCount()<=0){
                //Log.d("CURSORCOUNT", Integer.toString(cursor.getCount()));
                dBase.close();
                cursor.close();
                return null;
            }
        } catch (Exception e){
            return  null;
        }

        cursor.moveToFirst();

        do {
            profile = new DonorProfile(
                    cursor.getString(cursor.getColumnIndex(DONOR_NAME_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_PHONE_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_GENDER_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_BLOODGROUP_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_STATUS_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_LOCATION_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_DISTRICT_FIELD)),
                    cursor.getString(cursor.getColumnIndex(DONOR_ORGANIZATION_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_AGE_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_ID_FIELD)),
                    cursor.getInt(cursor.getColumnIndex(DONOR_SHOULDSHOW_FIELD)));
            //Log.d("check",profiles.get(0).getDonorBloodGroup());
        } while (cursor.moveToNext());

        cursor.close();
        dBase.close();
        return  profile;
    }


    public boolean deleteDonor(String selection, String[] args){
        SQLiteDatabase database = this.getWritableDatabase();
        int res;
        try {
            res = database.delete(DONOR_TABLE_NAME, selection, args);
        } catch (Exception e){
            return false;
        }

        return res > 0;
    }

//    public ArrayList<String> getLocations(String selection, String[] args){
//
//        SQLiteDatabase dBase;
//        ArrayList<String>  locs = new ArrayList<>();
//        Cursor cursor;
//
//        try{
//            dBase = this.getReadableDatabase();
//            cursor = dBase.query(true, DONOR_TABLE_NAME, new String[]{DONOR_LOCATION_FIELD}, selection, args,null, null, null, null);
//            //cursor = dBase.rawQuery("SELECT * FROM "+DONOR_TABLE_NAME+" WHERE "+DONOR_BLOODGROUP_FIELD+"='A+'", null);
//            if(cursor.getCount()<=0){
//                //Log.d("CURSORCOUNT", Integer.toString(cursor.getCount()));
//                dBase.close();
//                cursor.close();
//                return null;
//            }
//        } catch (Exception e){
//            return  null;
//        }
//
//        cursor.moveToFirst();
//        //Log.d("cursorCount", Integer.toString(cursor.getCount()));
//        do {
//                    locs.add(cursor.getString(cursor.getColumnIndex(DONOR_LOCATION_FIELD)));
//            //Log.d("check",profiles.get(0).getDonorBloodGroup());
//        } while (cursor.moveToNext());
//
//        cursor.close();
//        dBase.close();
//        return  locs;
//    }


//    public DonorProfile getAuthorizedUser(String phone, String pass) {
//        return null;
//    }



    private class DataContainer{

        int donorNameIndex;
        int donorPhoneIndex;
        int donorGenderIndex;
        int donorBloodGroupIndex;
        int donorStatusIndex;
        int donorLocationIndex;
        int donorDistrictIndex;
        int donorOrganizationIndex;
        int donorAgeIndex;
        int donorIdIndex;
        int donorShouldShowIndex;

        DataContainer(Cursor cursor){
            this.donorIdIndex = cursor.getColumnIndex(DONOR_ID_FIELD);
            this.donorNameIndex = cursor.getColumnIndex(DONOR_NAME_FIELD);
            this.donorPhoneIndex = cursor.getColumnIndex(DONOR_PHONE_FIELD);
            this.donorGenderIndex = cursor.getColumnIndex(DONOR_GENDER_FIELD);
            this.donorBloodGroupIndex = cursor.getColumnIndex(DONOR_BLOODGROUP_FIELD);
            this.donorStatusIndex = cursor.getColumnIndex(DONOR_STATUS_FIELD);
            this.donorLocationIndex = cursor.getColumnIndex(DONOR_LOCATION_FIELD);
            this.donorDistrictIndex = cursor.getColumnIndex(DONOR_DISTRICT_FIELD);
            this.donorAgeIndex = cursor.getColumnIndex(DONOR_AGE_FIELD);
            this.donorShouldShowIndex = cursor.getColumnIndex(DONOR_SHOULDSHOW_FIELD);
            this.donorOrganizationIndex = cursor.getColumnIndex(DONOR_ORGANIZATION_FIELD);

        }

    }





/*****************************************
 *
 *
 *  Code For Organization Table Handling
 *
 *
 *****************************************/

    public int insertSingleOrg(OrgProfile profile){
        SQLiteDatabase database;
        try{
            database = this.getWritableDatabase();
        }catch (Exception e){
            return -1;
        }

        ContentValues values =  new ContentValues();
        values.put(ORG_ID_FIELD, profile.id);
        values.put(ORG_NAME_FIELD, profile.name);
        values.put(ORG_ADMIN_FIELD, profile.admin);
        values.put(ORG_USERNAME_FIELD, profile.username);
        values.put(ORG_PHONE_FIELD, profile.phone);
        values.put(ORG_DISTRICT_FIELD, profile.district);

        try{
            database.insert(ORG_TABLE_NAME, null, values);
        } catch (Exception e){
            database.close();
            values.clear();
            return -1;
        }
        database.close();
        values.clear();
        return 1;
    }


    public int insertAllOrg(ArrayList<OrgProfile> profiles){
        SQLiteDatabase database;

        try{
            database = this.getWritableDatabase();
            onCreate(database);
        }catch (Exception e){
            return -1;
        }

        ContentValues values =  new ContentValues();
        int size = profiles.size();

        for(int i=0; i<size; i++) {
            values.put(ORG_ID_FIELD, profiles.get(i).id);
            values.put(ORG_NAME_FIELD, profiles.get(i).name);
            values.put(ORG_ADMIN_FIELD, profiles.get(i).admin);
            values.put(ORG_USERNAME_FIELD, profiles.get(i).username);
            values.put(ORG_PHONE_FIELD, profiles.get(i).phone);
            values.put(ORG_DISTRICT_FIELD, profiles.get(i).district);

            try {
                database.insert(ORG_TABLE_NAME, null, values);
            } catch (Exception e) {
                database.close();
                values.clear();
                return -1;
            }
            values.clear();
        }
        database.close();
        return 1;
    }


    public ArrayList<OrgProfile> getOrg(String selection, String[] args){
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try{
            database = this.getReadableDatabase();
            cursor = database.query(true, ORG_TABLE_NAME, null, selection, args, null, null, null, null);
            if(cursor.getCount()<=0){
                database.close();
                cursor.close();
                return null;
            }
        } catch (Exception e){
            if(database!=null && database.isOpen()){
                database.close();
            }
            if(cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            return null;
        }
        ArrayList<OrgProfile> profiles = new ArrayList<>();
        cursor.moveToFirst();
        OrgTableIndices indices  = new OrgTableIndices(cursor);
        do {
            profiles.add(new OrgProfile(cursor.getInt(indices.orgIdIndex),
                    cursor.getString(indices.orgNameIndex),
                    cursor.getString(indices.orgAdminIndex),
                    cursor.getString(indices.orgUsernameIndex),
                    cursor.getString(indices.orgPhoneIndex),
                    cursor.getString(indices.orgDistrictIndex)));

        }while (cursor.moveToNext());
        cursor.close();
        database.close();
        return profiles;
    }

    public OrgProfile getOrgProfile(String selection, String[] args){
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try{
            database = this.getReadableDatabase();
            cursor = database.query(ORG_TABLE_NAME, null, selection, args, null, null, null, "1");
            if(cursor.getCount()<=0){
                database.close();
                cursor.close();
                return null;
            }
        } catch (Exception e){
            if(database!=null && database.isOpen()){
                database.close();
            }
            if(cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
            return null;
        }
        OrgProfile profile;
        cursor.moveToFirst();
        OrgTableIndices indices  = new OrgTableIndices(cursor);
        do {
            profile = new OrgProfile(cursor.getInt(indices.orgIdIndex),
                    cursor.getString(indices.orgNameIndex),
                    cursor.getString(indices.orgAdminIndex),
                    cursor.getString(indices.orgUsernameIndex),
                    cursor.getString(indices.orgPhoneIndex),
                    cursor.getString(indices.orgDistrictIndex));

        } while (cursor.moveToNext());
        cursor.close();
        database.close();
        return profile;
    }

    public boolean deleteOrg(String selection, String[] args){
        SQLiteDatabase database = this.getWritableDatabase();
        int res;
        try {
            res = database.delete(ORG_TABLE_NAME, selection, args);
        } catch (Exception e){
            return false;
        }

        return res > 0;
    }



    private class OrgTableIndices {

        int orgIdIndex;
        int orgNameIndex;
        int orgAdminIndex;
        int orgUsernameIndex;
        int orgPhoneIndex;
        int orgDistrictIndex;

        OrgTableIndices(Cursor cursor){
            this.orgIdIndex = cursor.getColumnIndex(ORG_ID_FIELD);
            this.orgNameIndex = cursor.getColumnIndex(ORG_NAME_FIELD);
            this.orgAdminIndex = cursor.getColumnIndex(ORG_ADMIN_FIELD);
            this.orgUsernameIndex = cursor.getColumnIndex(ORG_USERNAME_FIELD);
            this.orgPhoneIndex = cursor.getColumnIndex(ORG_PHONE_FIELD);
            this.orgDistrictIndex = cursor.getColumnIndex(ORG_DISTRICT_FIELD);
        }

    }

    /*****************************************
     *
     *
     *  Code For Handling District Table
     *
     *
     *****************************************/

    public int insertDistrict(){
        SQLiteDatabase database = this.getWritableDatabase();

        try{
            database.execSQL(DISTRICT_INSERT_QUERY);
        } catch (Exception e){
            database.close();
            return  -1;
        }
        database.close();
        return 1;
    }

    public ArrayList<String> getDistricts(){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<String> names = new ArrayList<>();

        try {
            cursor = database.query(true, DISTRICT_TABLE_NAME, null, null, null, null, null, DISTRICT_NAME_FIELD+" ASC", null);
            if(cursor==null){
                if(database.isOpen())database.close();
                return null;
            }
            cursor.moveToFirst();
            do{
                names.add(cursor.getString(cursor.getColumnIndex(DISTRICT_NAME_FIELD)));
            }while (cursor.moveToNext());
        } catch (Exception e) {
            if (database.isOpen())database.close();
            if(cursor!=null && !cursor.isClosed()) cursor.close();
            return null;
        }
        cursor.close();
        database.close();
        return names;
    }
}
