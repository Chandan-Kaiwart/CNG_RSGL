package com.apc.cng_hpcl.home.notification;

public class DataModel {
String id,msg;
    String date;
    String time;
    String lcvnum;
    String mgs;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    String transId;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    int flag;

    public DataModel() {
    }
    public DataModel(String id, String msg, String date, String time, String lcvnum, String mgs, String dbs, String status, String create_date,int flag1) {
        this.id = id;
        this.msg = msg;
        this.date = date;
        this.time = time;
        this.lcvnum = lcvnum;
        this.mgs = mgs;
        this.dbs = dbs;
        this.status = status;
        this.create_date = create_date;
        this.flag=flag1;
    }
    public DataModel(String id, String msg, String date, String time, String lcvnum, String mgs, String dbs, String status, String create_date,String stage) {
        this.id = id;
        this.msg = msg;
        this.date = date;
        this.time = time;
        this.lcvnum = lcvnum;
        this.mgs = mgs;
        this.dbs = dbs;
        this.status = status;
        this.create_date = create_date;
        this.stage=stage;
    }

    String dbs;
    String status;
    String create_date;

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    String stage;

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLcvnum() {
        return lcvnum;
    }

    public void setLcvnum(String lcvnum) {
        this.lcvnum = lcvnum;
    }

    public String getMgs() {
        return mgs;
    }

    public void setMgs(String mgs) {
        this.mgs = mgs;
    }

    public String getDbs() {
        return dbs;
    }

    public void setDbs(String dbs) {
        this.dbs = dbs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
