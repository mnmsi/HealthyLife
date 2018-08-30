package com.echoinc.healthylife;

/**
 * Created by msi_ on 10-Dec-17.
 */

public class UsersDetailed {

   private int image;
   private String name, gender, address, phone, birthday, bloodGroup, previousDonation, previousDonationDate;
   private String lat, lng;

    public UsersDetailed() {
    }

    public UsersDetailed(int image, String name, String gender, String address, String phone, String birthday, String bloodGroup, String previousDonation, String previousDonationDate, String lat, String lng) {
        this.image = image;
        this.name = name;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.birthday = birthday;
        this.bloodGroup = bloodGroup;
        this.previousDonation = previousDonation;
        this.previousDonationDate = previousDonationDate;
        this.lat = lat;
        this.lng = lng;
    }

    public UsersDetailed(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getPreviousDonation() {
        return previousDonation;
    }

    public void setPreviousDonation(String previousDonation) {
        this.previousDonation = previousDonation;
    }

    public String getPreviousDonationDate() {
        return previousDonationDate;
    }

    public void setPreviousDonationDate(String previousDonationDate) {
        this.previousDonationDate = previousDonationDate;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
