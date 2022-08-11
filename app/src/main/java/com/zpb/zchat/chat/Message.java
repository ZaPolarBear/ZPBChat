package com.zpb.zchat.chat;

import java.util.Date;

public class Message {
    private String from, message, type, to, messageID, time;


    public Message(String from, String message, String type, String to, String messageID, String time) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.to = to;
        this.messageID = messageID;
        this.time = time;
    }

    public Message() {

    }

    public String getId() {
        return messageID;
    }

    public String getText() {
        return null;
    }


    public Date getCreatedAt() {
        return null;
    }


    public static class Video {

        private String url;

        public Video() {

        }

        public String getUrl() {
            return url;
        }

        public Video(String url) {
            this.url = url;
        }
    }

    public static class Voice {

        private String url;
        private int duration;

        public Voice() {

        }

        public String getUrl() {
            return url;
        }

        public int getDuration() {
            return duration;
        }

        public Voice(String url, int duration) {
            this.url = url;
            this.duration = duration;

        }
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

