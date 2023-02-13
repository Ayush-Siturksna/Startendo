package com.example.startendo.Chat;

import com.example.startendo.UserObject;
import java.io.Serializable;
import java.util.ArrayList;

public class ChatObject implements Serializable {
    private String chatId;
    private String lastMessage;
    long lasttime;
    private String name;
    private String phone;
    private ArrayList<UserObject> userObjectArrayList = new ArrayList<>();

    public ChatObject(String str, String str2, String str3, String str4, long j) {
        this.chatId = str;
        this.name = str2;
        this.phone = str3;
        this.lastMessage = str4;
        this.lasttime = j;
    }

    public ArrayList<UserObject> getUserObjectArrayList() {
        return this.userObjectArrayList;
    }

    public String getChatId() {
        return this.chatId;
    }

    public void setChatId(String str) {
        this.chatId = str;
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

    public void addUserToArrayList(UserObject userObject) {
        this.userObjectArrayList.add(userObject);
    }

    public String getLastMessage() {
        return this.lastMessage;
    }

    public void setLastMessage(String str) {
        this.lastMessage = str;
    }

    public long getLasttime() {
        return this.lasttime;
    }

    public void setLasttime(long j) {
        this.lasttime = j;
    }

    public void setUserObjectArrayList(ArrayList<UserObject> arrayList) {
        this.userObjectArrayList = arrayList;
    }
}
