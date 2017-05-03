package com.chowdhuryfahim.bloodinquiry.DatabaseFiles;

/*
 * Created by Fahim on 3/29/2017.
 */

public interface OrgFields extends CommonQuery {

    String ORG_TABLE_NAME = "org_table";
    String ORG_ID_FIELD = "id";
    String ORG_NAME_FIELD = "name";
    String ORG_ADMIN_FIELD = "admin";
    String ORG_USERNAME_FIELD = "username";
    String ORG_PASSWORD_FIELD = "password";
    String ORG_PHONE_FIELD = "phone";
    String ORG_DISTRICT_FIELD = "district";

    String ORG_TABLE_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS "+ ORG_TABLE_NAME + "(" +
            ORG_ID_FIELD + INTEGER_TYPE + COMMA +
            ORG_NAME_FIELD + VARCHAR_TYPE + COMMA +
            ORG_ADMIN_FIELD + VARCHAR_TYPE + COMMA +
            ORG_USERNAME_FIELD + VARCHAR_TYPE + PRIMARY_KEY + COMMA +
            ORG_PHONE_FIELD + VARCHAR_TYPE + COMMA +
            ORG_DISTRICT_FIELD + VARCHAR_TYPE + ")";

}
