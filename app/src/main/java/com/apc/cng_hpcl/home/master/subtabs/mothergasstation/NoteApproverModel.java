package com.apc.cng_hpcl.home.master.subtabs.mothergasstation;

public class NoteApproverModel {
    String Emp_id;
    String Emp_Type;
    String Emp_Orgnization_id;
    String Emp_First_Name;
    String Emp_Middle_Name;
    String Emp_Last_Name;
    String Emp_Age;
    String Emp_Contact_Number;
    String Emp_Email_Id;
    String Modified_Timestamp;
    String DateTimeStamp;

    public NoteApproverModel(String emp_id, String emp_Type, String emp_Orgnization_id, String emp_First_Name, String emp_Middle_Name, String emp_Last_Name, String emp_Age, String emp_Contact_Number, String emp_Email_Id, String modified_Timestamp, String dateTimeStamp) {
        Emp_id = emp_id;
        Emp_Type = emp_Type;
        Emp_Orgnization_id = emp_Orgnization_id;
        Emp_First_Name = emp_First_Name;
        Emp_Middle_Name = emp_Middle_Name;
        Emp_Last_Name = emp_Last_Name;
        Emp_Age = emp_Age;
        Emp_Contact_Number = emp_Contact_Number;
        Emp_Email_Id = emp_Email_Id;
        Modified_Timestamp = modified_Timestamp;
        DateTimeStamp = dateTimeStamp;
    }

    public NoteApproverModel() {
    }

    public String getEmp_id() {
        return Emp_id;
    }

    public void setEmp_id(String emp_id) {
        Emp_id = emp_id;
    }

    public String getEmp_Type() {
        return Emp_Type;
    }

    public void setEmp_Type(String emp_Type) {
        Emp_Type = emp_Type;
    }

    public String getEmp_Orgnization_id() {
        return Emp_Orgnization_id;
    }

    public void setEmp_Orgnization_id(String emp_Orgnization_id) {
        Emp_Orgnization_id = emp_Orgnization_id;
    }

    public String getEmp_First_Name() {
        return Emp_First_Name;
    }

    public void setEmp_First_Name(String emp_First_Name) {
        Emp_First_Name = emp_First_Name;
    }

    public String getEmp_Middle_Name() {
        return Emp_Middle_Name;
    }

    public void setEmp_Middle_Name(String emp_Middle_Name) {
        Emp_Middle_Name = emp_Middle_Name;
    }

    public String getEmp_Last_Name() {
        return Emp_Last_Name;
    }

    public void setEmp_Last_Name(String emp_Last_Name) {
        Emp_Last_Name = emp_Last_Name;
    }

    public String getEmp_Age() {
        return Emp_Age;
    }

    public void setEmp_Age(String emp_Age) {
        Emp_Age = emp_Age;
    }

    public String getEmp_Contact_Number() {
        return Emp_Contact_Number;
    }

    public void setEmp_Contact_Number(String emp_Contact_Number) {
        Emp_Contact_Number = emp_Contact_Number;
    }

    public String getEmp_Email_Id() {
        return Emp_Email_Id;
    }

    public void setEmp_Email_Id(String emp_Email_Id) {
        Emp_Email_Id = emp_Email_Id;
    }

    public String getModified_Timestamp() {
        return Modified_Timestamp;
    }

    public void setModified_Timestamp(String modified_Timestamp) {
        Modified_Timestamp = modified_Timestamp;
    }

    public String getDateTimeStamp() {
        return DateTimeStamp;
    }

    public void setDateTimeStamp(String dateTimeStamp) {
        DateTimeStamp = dateTimeStamp;
    }
}
