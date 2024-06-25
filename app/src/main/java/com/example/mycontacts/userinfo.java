package com.example.mycontacts;

public class userinfo {
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    private String Number;
    private String Name;
    private String Email;
    private String Note;
    private String imageurl;
    private String user_id;

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    private String node_id;

    public userinfo() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public userinfo(String number, String name, String email, String note, String imageurl) {
        Number = number;
        Name = name;
        Email = email;
        Note = note;
        imageurl = imageurl;
    }

    public String getNumber() {
        return Number;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getNote() {
        return Note;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setNote(String note) {
        Note = note;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
