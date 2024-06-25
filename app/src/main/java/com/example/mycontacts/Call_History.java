package com.example.mycontacts;

public class Call_History {
    private String uid;
    private String c_name;
    private String cc_image;
    private String node_id;

    public void setNode_id(String node_id) {this.node_id = node_id;}

    public String getNode_id() {
        return node_id;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    private String date_time;
    public Call_History(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getCc_image() {
        return cc_image;
    }

    public void setCc_image(String cc_image) {
        this.cc_image = cc_image;
    }
}
