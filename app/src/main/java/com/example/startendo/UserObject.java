package com.example.startendo;

import java.io.Serializable;

public class UserObject implements Serializable {
    private String lastmessage;
    private String name;
    private String notificationkey;
    private String phone;
    private String profilepic;
    private Boolean selected = false;
    private String uId;

    public UserObject(String ProfilePic, String Name, String Phone, String LastMessage) {
        this.profilepic = ProfilePic;
        this.name = Name;
        this.phone = Phone;
        this.lastmessage = LastMessage;
    }

    public UserObject(String str) {
        this.uId = str;
    }

    public UserObject(String Uid, String Name, String Phone) {
        this.name = Name;
        this.phone = Phone;
        this.uId = Uid;
    }

    public Boolean getSelected() {
        return this.selected;
    }

    public void setSelected(Boolean bool) {
        this.selected = bool;
    }

    public String getNotificationkey() {
        return this.notificationkey;
    }

    public void setNotificationkey(String str) {
        this.notificationkey = str;
    }

    public String getuId() {
        return this.uId;
    }

    public void setuId(String str) {
        this.uId = str;
    }

    public String getProfilepic() {
        return this.profilepic;
    }

    public void setProfilepic(String str) {
        this.profilepic = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String str) {
        this.phone = str;
    }

    public String getLastmessage() {
        return this.lastmessage;
    }

    public void setLastmessage(String str) {
        this.lastmessage = str;
    }
}
