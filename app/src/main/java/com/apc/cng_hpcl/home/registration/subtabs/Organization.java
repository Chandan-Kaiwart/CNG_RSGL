package com.apc.cng_hpcl.home.registration.subtabs;

public class Organization {
    private String org_abr, sector, address1, address2, address3, city, state, postal_code, name_cp,
            mobile, landline,lat_long, org_type, org_full_name;

    public Organization() {
    }

    public String getOrg_abr() {
        return org_abr;
    }

    public void setOrg_abr(String org_abr) {
        this.org_abr = org_abr;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getName_cp() {
        return name_cp;
    }

    public void setName_cp(String name_cp) {
        this.name_cp = name_cp;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getLat_long() {
        return lat_long;
    }

    public void setLat_long(String lat_long) {
        this.lat_long = lat_long;
    }

    public String getOrg_type() {
        return org_type;
    }

    public void setOrg_type(String org_type) {
        this.org_type = org_type;
    }

    public String getOrg_full_name() {
        return org_full_name;
    }

    public void setOrg_full_name(String org_full_name) {
        this.org_full_name = org_full_name;
    }

    public Organization(String org_abr, String sector, String address1, String address2, String address3, String city, String state, String postal_code, String name_cp, String mobile, String landline, String lat_long, String org_type, String org_full_name) {
        this.org_abr = org_abr;
        this.sector = sector;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.city = city;
        this.state = state;
        this.postal_code = postal_code;
        this.name_cp = name_cp;
        this.mobile = mobile;
        this.landline = landline;
        this.lat_long = lat_long;
        this.org_type = org_type;
        this.org_full_name = org_full_name;
    }
}
