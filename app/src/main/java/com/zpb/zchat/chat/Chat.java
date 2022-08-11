package com.zpb.zchat.chat;

public class Chat {
    private String name = "";
    private String LastTime = "";
    private String LastMessage = "";
    private String type = "";
    private String receiverUid;

    public Chat(String name, String LastTime, String LastMessage, String type, String receiverUid){
        this.name = name;
        this.LastTime = LastTime;
        this.LastMessage = LastMessage;
        this.type = type;
        this.receiverUid = receiverUid;
    }

    public Chat() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName () {return  this.name;}

    public void setName (String name) {this.name = name;}

    public String getLastMessage () {return  this.LastMessage;}

    public void setLastMessage (String Messages) {this.LastMessage = Messages;}

    public String getLastTime () {return  this.LastTime;}

    public void setLastTime (String time) {this.LastTime = time;}

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }
}
