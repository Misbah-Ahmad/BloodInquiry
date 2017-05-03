package com.chowdhuryfahim.bloodinquiry.DatabaseFiles;

/*
 *
 * Created by Fahim on 3/29/2017.
 *
 */

public interface CommonQuery {

    String DB_NAME = "bidb";
    String COMMA = ",";
    String PRIMARY_KEY = " PRIMARY KEY";
    String INTEGER_TYPE = " INTEGER";
    String VARCHAR_TYPE = " VARCHAR";
    String AUTO_INCREMENT = " AUTOINCREMENT";

//    String DROP_TABLE_QUERY = "DELETE * FROM ";

    String SELECT_ALL_QUERY = "SELECT * FROM ";

    String DROP_QUERY = "DROP TABLE IF EXISTS ";
}
