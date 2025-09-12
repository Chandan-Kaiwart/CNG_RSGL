package com.apc.cng_hpcl.home.transaction;

public class MgsDbsModel {
    String Station_Id,
            mgsId,
            notification_approver_id, Station_type, Station_Name, Station_Address, Station_In_Charge_Name, Station_In_Charge_Contact_Number,
            Number_Filling_Bays, Number_Dispenser_Per_Bay, Latitude_Longitude,
            Create_User_Id, Modified_User_Id, create_date, update_date;

    public String getStation_Id() {
        return Station_Id;
    }

    public void setStation_Id(String station_Id) {
        Station_Id = station_Id;
    }

    public String getMgsId() {
        return mgsId;
    }

    public void setMgsId(String mgsId) {
        this.mgsId = mgsId;
    }

    public String getNotification_approver_id() {
        return notification_approver_id;
    }

    public void setNotification_approver_id(String notification_approver_id) {
        this.notification_approver_id = notification_approver_id;
    }

    public String getStation_type() {
        return Station_type;
    }

    public void setStation_type(String station_type) {
        Station_type = station_type;
    }

    public String getStation_Name() {
        return Station_Name;
    }

    public void setStation_Name(String station_Name) {
        Station_Name = station_Name;
    }

    public String getStation_Address() {
        return Station_Address;
    }

    public void setStation_Address(String station_Address) {
        Station_Address = station_Address;
    }

    public String getStation_In_Charge_Name() {
        return Station_In_Charge_Name;
    }

    public void setStation_In_Charge_Name(String station_In_Charge_Name) {
        Station_In_Charge_Name = station_In_Charge_Name;
    }

    public String getStation_In_Charge_Contact_Number() {
        return Station_In_Charge_Contact_Number;
    }

    public void setStation_In_Charge_Contact_Number(String station_In_Charge_Contact_Number) {
        Station_In_Charge_Contact_Number = station_In_Charge_Contact_Number;
    }

    public String getNumber_Filling_Bays() {
        return Number_Filling_Bays;
    }

    public void setNumber_Filling_Bays(String number_Filling_Bays) {
        Number_Filling_Bays = number_Filling_Bays;
    }

    public String getNumber_Dispenser_Per_Bay() {
        return Number_Dispenser_Per_Bay;
    }

    public void setNumber_Dispenser_Per_Bay(String number_Dispenser_Per_Bay) {
        Number_Dispenser_Per_Bay = number_Dispenser_Per_Bay;
    }

    public String getLatitude_Longitude() {
        return Latitude_Longitude;
    }

    public void setLatitude_Longitude(String latitude_Longitude) {
        Latitude_Longitude = latitude_Longitude;
    }

    public String getCreate_User_Id() {
        return Create_User_Id;
    }

    public void setCreate_User_Id(String create_User_Id) {
        Create_User_Id = create_User_Id;
    }

    public String getModified_User_Id() {
        return Modified_User_Id;
    }

    public void setModified_User_Id(String modified_User_Id) {
        Modified_User_Id = modified_User_Id;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }
}
