package com.chowdhuryfahim.bloodinquiry.DatabaseFiles;

/*
 * Created by Fahim on 12/26/2016.
 */

public interface DataFields extends CommonQuery{


     String DONOR_TABLE_NAME = "donors_table";
     String DONOR_ID_FIELD = "id";
     String DONOR_NAME_FIELD = "name";
     String DONOR_PASSWORD_FILED = "password";
     String DONOR_PHONE_FIELD = "phone";
     String DONOR_GENDER_FIELD = "gender";
     String DONOR_BLOODGROUP_FIELD = "blood_group";
     String DONOR_STATUS_FIELD = "status";
     String DONOR_LOCATION_FIELD = "location";
     String DONOR_DISTRICT_FIELD = "district";
     String DONOR_ORGANIZATION_FIELD = "organization";
     String DONOR_AGE_FIELD = "age";
     String DONOR_SHOULDSHOW_FIELD = "should_show";




    String CREATE_DONOR_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS "+ DONOR_TABLE_NAME + "(" +
        DONOR_ID_FIELD + INTEGER_TYPE + COMMA +
        DONOR_NAME_FIELD + VARCHAR_TYPE + COMMA +
        DONOR_PHONE_FIELD + VARCHAR_TYPE + PRIMARY_KEY + COMMA +
        DONOR_GENDER_FIELD + VARCHAR_TYPE + COMMA +
        DONOR_BLOODGROUP_FIELD + VARCHAR_TYPE + COMMA +
        DONOR_STATUS_FIELD + INTEGER_TYPE + COMMA +
        DONOR_LOCATION_FIELD + VARCHAR_TYPE + COMMA +
        DONOR_DISTRICT_FIELD + VARCHAR_TYPE + COMMA +
        DONOR_AGE_FIELD + INTEGER_TYPE + COMMA +
        DONOR_SHOULDSHOW_FIELD + INTEGER_TYPE + COMMA +
        DONOR_ORGANIZATION_FIELD + VARCHAR_TYPE + ")";




//    String DELETE_QUERY =
//            "DELETE * FROM IF EXISTS ";
}
