package com.chowdhuryfahim.bloodinquiry.models;


/*
 * Created by Fahim on 12/25/2016.
 * to save donor information
 */

public class DonorProfile {

    public String donorName;
    public String donorPassword;
    public String donorPhone;
    public String donorGender;
    public String donorBloodGroup;
    public int donorStatus;
    public String donorLocation;
    public String donorDistrict;
    public String donorOrganization;
    public int donorAge;
    public long donorId;
    public int donorShouldShow;


    public DonorProfile(){
        this.donorPassword = "";

    }

    public DonorProfile(String donorName, String donorPassword,
                        String donorPhone, String donorGender, String donorBloodGroup,
                        int donorStatus, String donorLocation, String donorDistrict,
                        String donorOrganization, int donorAge, long donorId,
                        int donorShouldShow) {
        this.donorName = donorName;
        this.donorPassword = donorPassword;
        this.donorPhone = donorPhone;
        this.donorGender = donorGender;
        this.donorBloodGroup = donorBloodGroup;
        this.donorStatus = donorStatus;
        this.donorLocation = donorLocation;
        this.donorDistrict = donorDistrict;
        this.donorOrganization = donorOrganization;
        this.donorAge = donorAge;
        this.donorId = donorId;
        this.donorShouldShow = donorShouldShow;
    }

    //Constructor without password
    public DonorProfile(String donorName,  String donorPhone,
                        String donorGender, String donorBloodGroup, int donorStatus,
                        String donorLocation, String donorDistrict, String donorOrganization,
                        int donorAge, long donorId, int donorShouldShow) {
        this.donorName = donorName;
        this.donorPhone = donorPhone;
        this.donorGender = donorGender;
        this.donorBloodGroup = donorBloodGroup;
        this.donorStatus = donorStatus;
        this.donorLocation = donorLocation;
        this.donorDistrict = donorDistrict;
        this.donorOrganization = donorOrganization;
        this.donorAge = donorAge;
        this.donorId = donorId;
        this.donorShouldShow = donorShouldShow;
    }
    //Constructor without password
    public DonorProfile(String donorName,  String donorPhone,
                        String donorGender, String donorBloodGroup, int donorStatus,
                        String donorLocation, String donorDistrict, String donorOrganization,
                        int donorAge, int donorShouldShow) {
        this.donorName = donorName;
        this.donorPhone = donorPhone;
        this.donorGender = donorGender;
        this.donorBloodGroup = donorBloodGroup;
        this.donorStatus = donorStatus;
        this.donorLocation = donorLocation;
        this.donorDistrict = donorDistrict;
        this.donorOrganization = donorOrganization;
        this.donorAge = donorAge;
        this.donorShouldShow = donorShouldShow;
    }

    public long getDonorId() {
        return donorId;
    }

    public void setDonorId(long donorId) {
        this.donorId = donorId;
    }


    public void setDonorName(String donorName) {

        this.donorName = donorName;
    }


    public void setDonorPassword(String donorPassword) {
        this.donorPassword = donorPassword;
    }

    public void setDonorPhone(String donorPhone) {
        this.donorPhone = donorPhone;
    }

    public void setDonorGender(String donorGender) {
        this.donorGender = donorGender;
    }

    public void setDonorBloodGroup(String donorBloodGroup) {
        this.donorBloodGroup = donorBloodGroup;
    }

    public void setDonorStatus(int donorStatus) {
        this.donorStatus = donorStatus;
    }

    public void setDonorLocation(String donorLocation) {
        this.donorLocation = donorLocation;
    }

    public void setDonorDistrict(String donorDistrict) {
        this.donorDistrict = donorDistrict;
    }

    public void setDonorOrganization(String donorOrganization) {
        this.donorOrganization = donorOrganization;
    }

    public void setDonorAge(int donorAge) {
        this.donorAge = donorAge;
    }

    public void setDonorShouldShow(int donorShouldShow) {
        this.donorShouldShow = donorShouldShow;
    }



    public int getDonorShouldShow() {
        return donorShouldShow;
    }

    public String getDonorName() {
        return donorName;
    }

    public String getDonorPassword() {
        return donorPassword;
    }

    public String getDonorPhone() {
        return donorPhone;
    }

    public String getDonorGender() {
        return donorGender;
    }

    public String getDonorBloodGroup() {
        return donorBloodGroup;
    }

    public int getDonorStatus() {
        return donorStatus;
    }

    public String getDonorLocation() {
        return donorLocation;
    }

    public String getDonorDistrict() {
        return donorDistrict;
    }

    public String getDonorOrganization() {
        return donorOrganization;
    }

    public int getDonorAge() {
        return donorAge;
    }
}
