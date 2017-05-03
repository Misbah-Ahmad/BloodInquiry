package com.chowdhuryfahim.bloodinquiry.DatabaseFiles;

/**
 *
 * Created by Fahim Chowdhury on 4/20/2017.
 */

public interface DistrictFields extends CommonQuery{

    String DISTRICT_TABLE_NAME = "districts";
    String DISTRICT_ID_FIELD = "id";
    String DISTRICT_NAME_FIELD = "name";

    String DISTRICT_TABLE_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS "+ DISTRICT_TABLE_NAME + "(" +
            DISTRICT_ID_FIELD + INTEGER_TYPE + PRIMARY_KEY + AUTO_INCREMENT + COMMA +
            DISTRICT_NAME_FIELD + VARCHAR_TYPE +")";

    //String DHAKA = "Noakhali";
    String DISTRICT_INSERT_QUERY = "INSERT INTO " + DISTRICT_TABLE_NAME + "(name) VALUES ('Dhaka'),"+
        "('Faridpur'),('Gazipur'),('Gopalganj'),('Jamalpur'),('Kishoreganj'),('Madaripur'),"+
        "('Manikganj'),('Munshiganj'),('Mymensingh'),('Narayanganj'),('Narsingdi'),('Netrokona'),('Rajbari')," +
        "('Shariatpur'),('Sherpur'),('Tangail'),('Bogra'),('Joypurhat'),('Naogaon'),('Natore'),('Nawabganj')," +
        "('Pabna'),('Rajshahi'),('Sirajgonj'),('Dinajpur'),('Gaibandha'),('Kurigram'),('Lalmonirhat'),('Nilphamari')," +
        "('Panchagarh'),('Rangpur'),('Thakurgaon'),('Barguna'),('Barisal'),('Bhola'),('Jhalokati'),('Patuakhali')," +
        "('Pirojpur'),('Bandarban'),('Brahmanbaria'),('Chandpur'),('Chittagong'),('Comilla'),('Coxs Bazar'),('Feni')," +
        "('Khagrachari'),('Lakshmipur'),('Noakhali'),('Rangamati'),('Habiganj'),('Maulvibazar'),('Sunamganj'),('Sylhet')," +
        "('Bagerhat'),('Chuadanga'),('Jessore'),('Jhenaidah'),('Khulna'),('Kushtia'),('Magura'),('Meherpur'),('Narail'),('Satkhira')";

}
