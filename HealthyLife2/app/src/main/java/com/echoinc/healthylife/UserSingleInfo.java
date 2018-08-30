package com.echoinc.healthylife;

/**
 * Created by msi_ on 02-Jan-18.
 */

public class UserSingleInfo {

    private String userName, userPhone, userBloodGroup, userLastDonationDate;
    private String markerId;

    public UserSingleInfo() {
    }

    public UserSingleInfo(String userName, String userPhone, String userBloodGroup, String userLastDonationDate, String markerId) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.userBloodGroup = userBloodGroup;
        this.userLastDonationDate = userLastDonationDate;
        this.markerId = markerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserBloodGroup() {
        return userBloodGroup;
    }

    public void setUserBloodGroup(String userBloodGroup) {
        this.userBloodGroup = userBloodGroup;
    }

    public String getUserLastDonationDate() {
        return userLastDonationDate;
    }

    public void setUserLastDonationDate(String userLastDonationDate) {
        this.userLastDonationDate = userLastDonationDate;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }
}
