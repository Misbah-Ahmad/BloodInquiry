package com.chowdhuryfahim.bloodinquiry.models;

/*
 * Created by Fahim on 3/29/2017.
 */

public class OrgProfile {
    public int id;
    public String name;
    public String admin;
    public String username;
    public String password;
    public String phone;
    public String district;

    public OrgProfile(){}

    public OrgProfile(int id, String name, String admin, String username, String phone, String district) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.phone = phone;
        this.username = username;
        this.district = district;
        this.password = "";
    }

    public OrgProfile(String name, String admin, String password, String phone, String username, String district) {
        this.name = name;
        this.admin = admin;
        this.password = password;
        this.phone = phone;
        this.username = username;
        this.district = district;
    }
}
