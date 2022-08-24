package com.zpb.zchat.chat;

public class Chat {
    private String name = "";
    private String LastTime = "";
    private String LastMessage = "";
    private String type = "";
    private String receiverUid;
    private String avatar;

    public Chat(String name, String lastTime, String lastMessage, String type, String receiverUid, String avatar) {
        this.name = name;
        this.LastTime = lastTime;
        this.LastMessage = lastMessage;
        this.type = type;
        this.receiverUid = receiverUid;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
