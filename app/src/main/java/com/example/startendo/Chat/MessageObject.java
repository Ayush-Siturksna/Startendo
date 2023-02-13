package com.example.startendo.Chat;

import java.util.ArrayList;

public class MessageObject {
    ArrayList<String> mediaUrlList;
    String message;
    String messageId;
    String senderId;
    String senderphone;
    long timestamp;

    public MessageObject(String str, String str2, String str3, ArrayList<String> arrayList, long j) {
        this.messageId = str;
        this.senderId = str2;
        this.message = str3;
        this.timestamp = j;
        this.mediaUrlList = arrayList;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(String str) {
        this.messageId = str;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public void setSenderId(String str) {
        this.senderId = str;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String str) {
        this.message = str;
    }

    public ArrayList<String> getMediaUrlList() {
        return this.mediaUrlList;
    }

    public String getSenderphone() {
        return this.senderphone;
    }

    public void setSenderphone(String str) {
        this.senderphone = str;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long j) {
        this.timestamp = j;
    }
}
